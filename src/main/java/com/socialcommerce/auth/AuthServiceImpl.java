package com.socialcommerce.auth;

import com.socialcommerce.auth.dto.*;
import com.socialcommerce.auth.entity.PasswordResetToken;
import com.socialcommerce.auth.entity.User;
import com.socialcommerce.auth.repository.PasswordResetTokenRepository;
import com.socialcommerce.auth.repository.UserRepository;
import com.socialcommerce.common.exception.ResourceNotFoundException;
import com.socialcommerce.common.exception.UnauthorizedException;
import com.socialcommerce.config.JwtUtil;
import com.socialcommerce.users.entity.UserProfile;
import com.socialcommerce.users.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository        userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtUtil               jwtUtil;
    private final PasswordResetTokenRepository resetTokenRepository;

    // ─── REGISTER ────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public UserDTO register(RegisterRequest request) {

        // 1. Reject duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        // 2. Build User entity
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : User.Role.BUYER);
        user.setIsActive(true);
        // uuid and createdAt are set by @PrePersist in User entity

        User saved = userRepository.save(user);

        // 3. Auto-create a blank UserProfile row (1-to-1)
        UserProfile profile = new UserProfile();
        profile.setUserId(saved.getId());
        profile.setFollowersCount(0);
        profile.setFollowingCount(0);
        profile.setPostCount(0);
        userProfileRepository.save(profile);

        return UserDTO.from(saved);
    }

    // ─── LOGIN ───────────────────────────────────────────────────────────────
    @Override
    public LoginResponse login(LoginRequest request) {

        // 1. Find user by email
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No account found with email: " + request.getEmail()));

        // 2. Block disabled accounts
        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is disabled. Contact admin.");
        }

        // 3. Verify password against BCrypt hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Incorrect password.");
        }

        // 4. Issue JWT pair
        String accessToken  = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken, UserDTO.from(user));
    }

    // ─── REFRESH TOKEN ───────────────────────────────────────────────────────
    @Override
    public LoginResponse refreshToken(String refreshToken) {

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new UnauthorizedException("Refresh token is invalid or expired. Please login again.");
        }

        String uuid = jwtUtil.extractUserId(refreshToken);
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is disabled.");
        }

        String newAccess  = jwtUtil.generateAccessToken(user);
        String newRefresh = jwtUtil.generateRefreshToken(user);

        return new LoginResponse(newAccess, newRefresh, UserDTO.from(user));
    }

    // ─── LOGOUT ──────────────────────────────────────────────────────────────
    @Override
    public void logout(String token) {
        /*
         * Stateless JWT: the real logout happens on the frontend by deleting
         * the stored token.  For production you would add the JTI to a Redis
         * blacklist here.  For this college project this stub is fine.
         */
    }

    // ─── FORGOT PASSWORD ─────────────────────────────────────────────────────
    @Override
    @Transactional
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No account registered with this email."));

        // Invalidate any previous tokens for this user
        resetTokenRepository.deleteByUserId(user.getId());

        // Generate a URL-safe random token
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        PasswordResetToken prt = PasswordResetToken.builder()
                .token(rawToken)
                .userId(user.getId())
                .build();
        resetTokenRepository.save(prt);

        /*
         * In production: send an email with a link like
         *   https://yourapp.com/reset-password?token=<rawToken>
         * For this demo we return the token directly in the API response
         * so you can test without configuring SMTP.
         */
        return rawToken;
    }

    // ─── RESET PASSWORD ──────────────────────────────────────────────────────
    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken prt = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token."));

        if (prt.isUsed()) {
            throw new IllegalArgumentException("This reset link has already been used.");
        }
        if (prt.isExpired()) {
            throw new IllegalArgumentException("Reset link has expired. Please request a new one.");
        }

        User user = userRepository.findById(prt.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        prt.setUsed(true);
        resetTokenRepository.save(prt);
    }
}
