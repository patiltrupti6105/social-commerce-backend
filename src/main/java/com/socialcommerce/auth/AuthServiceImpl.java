package com.socialcommerce.auth;

import com.socialcommerce.auth.dto.*;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public UserDTO register(RegisterRequest request) {
        throw new UnsupportedOperationException("TODO P1: implement register");
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        throw new UnsupportedOperationException("TODO P1: implement login");
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        throw new UnsupportedOperationException("TODO P1: implement refreshToken");
    }

    @Override
    public void logout(String token) {
        throw new UnsupportedOperationException("TODO P1: implement logout");
    }
}
