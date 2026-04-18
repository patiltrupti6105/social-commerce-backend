package com.socialcommerce.auth;

import com.socialcommerce.auth.dto.*;
import com.socialcommerce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/v1/auth/register
     * Body: { "name":"Alice", "email":"alice@mail.com", "password":"secret123", "role":"BUYER" }
     * role is optional — defaults to BUYER
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(
            @Valid @RequestBody RegisterRequest request) {
        UserDTO user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "Registration successful"));
    }

    /**
     * POST /api/v1/auth/login
     * Body: { "email":"alice@mail.com", "password":"secret123" }
     * Returns: { accessToken, refreshToken, user }
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    /**
     * POST /api/v1/auth/refresh
     * Body: { "refreshToken":"<refresh token from login>" }
     * Returns: new accessToken + refreshToken pair
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @RequestBody RefreshRequest body) {
        LoginResponse response = authService.refreshToken(body.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed"));
    }

    /**
     * POST /api/v1/auth/logout
     * Header: Authorization: Bearer <token>
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(
            @RequestHeader(value = "Authorization", required = false) String token) {
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }
}
