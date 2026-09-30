package com.socialcommerce.auth.controller;

import com.socialcommerce.auth.AuthService;
import com.socialcommerce.auth.dto.*;
import com.socialcommerce.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration, login, token refresh, and password reset")
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "Register new user",
        description = "Create a new user account with BUYER, SELLER, or ADMIN role"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid input or email already exists"
        )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(
            @Valid @RequestBody RegisterRequest request) {
        UserDTO user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "Registration successful"));
    }

    @Operation(
        summary = "Login",
        description = "Authenticate user and receive JWT access token + refresh token"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Invalid credentials"
        )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @Operation(
        summary = "Refresh access token",
        description = "Get a new access token using refresh token (no authentication required)"
    )
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @RequestBody RefreshRequest body) {
        LoginResponse response = authService.refreshToken(body.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed"));
    }

    @Operation(
        summary = "Logout",
        description = "Invalidate current session (token will be blacklisted)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(
            @RequestHeader(value = "Authorization", required = false) String token) {
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

    @Operation(
        summary = "Request password reset",
        description = "Generate a password reset token (in production, this would be emailed)"
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @RequestBody java.util.Map<String, String> body) {
        String email = body.getOrDefault("email", "").trim();
        if (email.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email is required"));
        }
        String token = authService.forgotPassword(email);
        return ResponseEntity.ok(ApiResponse.success(token,
                "Reset token generated. Use it at /reset-password within 1 hour."));
    }

    @Operation(
        summary = "Reset password",
        description = "Change password using reset token from /forgot-password"
    )
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(
            @RequestBody java.util.Map<String, String> body) {
        String token = body.getOrDefault("token", "").trim();
        String newPassword = body.getOrDefault("newPassword", "").trim();
        if (token.isEmpty() || newPassword.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("token and newPassword are required"));
        }
        if (newPassword.length() < 8) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Password must be at least 8 characters"));
        }
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successful. Please log in."));
    }
}
