package com.socialcommerce.auth;

import com.socialcommerce.auth.dto.*;

public interface AuthService {
    UserDTO        register(RegisterRequest request);
    LoginResponse  login(LoginRequest request);
    LoginResponse  refreshToken(String refreshToken);
    void           logout(String token);
}
