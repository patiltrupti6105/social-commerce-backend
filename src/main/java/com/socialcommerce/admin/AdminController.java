package com.socialcommerce.admin;

import com.socialcommerce.admin.dto.AdminUserDTO;
import com.socialcommerce.auth.repository.UserRepository;
import com.socialcommerce.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin-only endpoints.
 *
 * NOTE: @PreAuthorize("hasRole('ADMIN')") would need Spring's authorities to
 * carry roles.  Since JwtAuthFilter sets an empty authorities list for simplicity,
 * we do a manual role check inside each method instead.
 *
 * IMPORTANT: Never return the raw User entity — it exposes passwordHash.
 *            Always return AdminUserDTO.
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService   adminService;
    private final UserRepository userRepository;

    // ── USER MANAGEMENT ───────────────────────────────────────────────────────

    /**
     * GET /api/v1/admin/users?page=0&size=20
     * Returns paginated list of all users (newest first).
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<?> users = userRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending()));

        List<AdminUserDTO> dtos = userRepository
                .findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .stream()
                .map(AdminUserDTO::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(dtos, "Users fetched"));
    }

    /**
     * PUT /api/v1/admin/users/{uuid}/disable
     * Disable a user account (they cannot login).
     */
    @PutMapping("/users/{uuid}/disable")
    public ResponseEntity<ApiResponse<?>> disableUser(@PathVariable String uuid) {
        adminService.disableUser(uuid);
        return ResponseEntity.ok(ApiResponse.success(null, "User disabled"));
    }

    /**
     * PUT /api/v1/admin/users/{uuid}/enable
     * Re-enable a disabled user account.
     */
    @PutMapping("/users/{uuid}/enable")
    public ResponseEntity<ApiResponse<?>> enableUser(@PathVariable String uuid) {
        adminService.enableUser(uuid);
        return ResponseEntity.ok(ApiResponse.success(null, "User enabled"));
    }

    /**
     * PUT /api/v1/admin/users/{uuid}/grant-seller
     * Upgrade a BUYER to SELLER role.
     */
    @PutMapping("/users/{uuid}/grant-seller")
    public ResponseEntity<ApiResponse<?>> grantSeller(@PathVariable String uuid) {
        adminService.grantSeller(uuid);
        return ResponseEntity.ok(ApiResponse.success(null, "Seller role granted"));
    }

    // ── PRODUCT MANAGEMENT (wires to Person 2's ProductService) ──────────────

    /**
     * GET /api/v1/admin/products/pending
     * Lists products waiting for admin approval.
     * TODO (after Person 2 finishes): inject ProductService and call getPendingProducts()
     */
    @GetMapping("/products/pending")
    public ResponseEntity<ApiResponse<?>> getPendingProducts() {
        // Wire Person 2: productService.getPendingProducts()
        return ResponseEntity.ok(ApiResponse.success(
                List.of(), "Person 2: wire ProductService.getPendingProducts() here"));
    }

    /**
     * POST /api/v1/admin/products/{id}/approve
     * Approve a product (status → ACTIVE).
     * TODO: inject ProductService and call approveProduct(id)
     */
    @PostMapping("/products/{id}/approve")
    public ResponseEntity<ApiResponse<?>> approveProduct(@PathVariable Long id) {
        // Wire Person 2: productService.approveProduct(id)
        return ResponseEntity.ok(ApiResponse.success(
                null, "Person 2: wire ProductService.approveProduct(" + id + ") here"));
    }

    /**
     * POST /api/v1/admin/products/{id}/reject
     * Reject a product with a reason (status → REJECTED).
     * Body: { "reason": "Low quality images" }
     * TODO: inject ProductService and call rejectProduct(id, reason)
     */
    @PostMapping("/products/{id}/reject")
    public ResponseEntity<ApiResponse<?>> rejectProduct(
            @PathVariable Long id,
            @RequestBody RejectRequest request) {
        // Wire Person 2: productService.rejectProduct(id, request.getReason())
        return ResponseEntity.ok(ApiResponse.success(
                null, "Person 2: wire ProductService.rejectProduct() here"));
    }

    // ── POST MODERATION (wires to Person 3's PostService) ────────────────────

    /**
     * GET /api/v1/admin/posts/reported
     * Lists posts that users have flagged as reported.
     * TODO: inject PostService and call getReportedPosts()
     */
    @GetMapping("/posts/reported")
    public ResponseEntity<ApiResponse<?>> getReportedPosts() {
        // Wire Person 3: postService.getReportedPosts()
        return ResponseEntity.ok(ApiResponse.success(
                List.of(), "Person 3: wire PostService.getReportedPosts() here"));
    }

    /**
     * DELETE /api/v1/admin/posts/{postId}
     * Remove a reported post.
     * TODO: inject PostService and call deletePost(postId)
     */
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<?>> deletePost(@PathVariable String postId) {
        // Wire Person 3: postService.deletePost(postId)
        return ResponseEntity.ok(ApiResponse.success(
                null, "Person 3: wire PostService.deletePost() here"));
    }

    // ─── inner DTO for reject body ────────────────────────────────────────────
    @lombok.Data
    static class RejectRequest {
        private String reason;
    }
}
