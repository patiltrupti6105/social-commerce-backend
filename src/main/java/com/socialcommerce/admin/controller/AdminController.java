package com.socialcommerce.admin.controller;

import com.socialcommerce.admin.dto.AdminUserDTO;
import com.socialcommerce.admin.service.AdminService;
import com.socialcommerce.auth.repository.UserRepository;
import com.socialcommerce.catalog.dto.ProductSummaryDTO;
import com.socialcommerce.catalog.service.ProductService;
import com.socialcommerce.common.response.ApiResponse;
import com.socialcommerce.social.PostService;
import com.socialcommerce.social.document.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final PostService postService;
    private final com.socialcommerce.analytics.service.AnalyticsService analyticsService;
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<AdminUserDTO> dtos = userRepository
            .findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))
            .stream().map(AdminUserDTO::from).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos, "Users fetched"));
    }

    @PutMapping("/users/{uuid}/disable")
    public ResponseEntity<ApiResponse<?>> disableUser(@PathVariable String uuid) {
        adminService.disableUser(uuid);
        return ResponseEntity.ok(ApiResponse.success(null, "User disabled"));
    }
    
    @PutMapping("/users/{uuid}/enable")
    public ResponseEntity<ApiResponse<?>> enableUser(@PathVariable String uuid) {
        adminService.enableUser(uuid);
        return ResponseEntity.ok(ApiResponse.success(null, "User enabled"));
    }

    @PutMapping("/users/{uuid}/grant-seller")
    public ResponseEntity<ApiResponse<?>> grantSeller(@PathVariable String uuid) {
        adminService.grantSeller(uuid);
        return ResponseEntity.ok(ApiResponse.success(null, "Seller role granted"));
    }

    @GetMapping("/products/pending")
    public ResponseEntity<ApiResponse<List<ProductSummaryDTO>>> getPendingProducts() {
        return ResponseEntity.ok(ApiResponse.success(productService.getPendingProducts()));
    }

    @PostMapping("/products/{id}/approve")
    public ResponseEntity<ApiResponse<?>> approveProduct(@PathVariable Long id) {
        productService.approveProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product approved"));
    }

    @PostMapping("/products/{id}/reject")
    public ResponseEntity<ApiResponse<?>> rejectProduct(
            @PathVariable Long id,
            @RequestBody RejectRequest request) {
        productService.rejectProduct(id, request.getReason());
        return ResponseEntity.ok(ApiResponse.success(null, "Product rejected"));
    }

    @GetMapping("/posts/reported")
    public ResponseEntity<ApiResponse<List<Post>>> getReportedPosts() {
        return ResponseEntity.ok(ApiResponse.success(postService.getReportedPosts()));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<?>> deletePost(@PathVariable String postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(ApiResponse.success(null, "Post deleted"));
    }

    @lombok.Data
    static class RejectRequest {
        private String reason;
    }
}
