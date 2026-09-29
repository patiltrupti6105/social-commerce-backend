package com.socialcommerce.auth;

import com.socialcommerce.auth.dto.*;

public interface AuthService {
    UserDTO        register(RegisterRequest request);
    LoginResponse  login(LoginRequest request);
    LoginResponse  refreshToken(String refreshToken);
    void           logout(String token);
    /** Issues a single-use reset token (returns it for dev; in prod dispatch via email). */
    String         forgotPassword(String email);
    /** Validates the token and updates the password. */
    void           resetPassword(String token, String newPassword);
}
