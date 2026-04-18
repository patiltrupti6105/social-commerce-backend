package com.socialcommerce.admin;

import com.socialcommerce.auth.entity.User;
import com.socialcommerce.auth.repository.UserRepository;
import com.socialcommerce.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    // ─── DISABLE USER ─────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void disableUser(String uuid) {
        User user = findByUuid(uuid);
        user.setIsActive(false);
        userRepository.save(user);
    }

    // ─── ENABLE USER ──────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void enableUser(String uuid) {
        User user = findByUuid(uuid);
        user.setIsActive(true);
        userRepository.save(user);
    }

    // ─── GRANT SELLER ROLE ────────────────────────────────────────────────────
    @Override
    @Transactional
    public void grantSeller(String uuid) {
        User user = findByUuid(uuid);
        user.setRole(User.Role.SELLER);
        userRepository.save(user);
    }

    // ─── helper ───────────────────────────────────────────────────────────────
    private User findByUuid(String uuid) {
        return userRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + uuid));
    }
}
