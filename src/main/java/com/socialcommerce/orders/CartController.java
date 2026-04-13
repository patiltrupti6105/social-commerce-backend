package com.socialcommerce.orders;

import com.socialcommerce.common.response.ApiResponse;
import com.socialcommerce.orders.dto.*;
import com.socialcommerce.orders.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(userId)));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDTO>> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddCartItemRequest request) {
        Long userId = extractUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(cartService.addItem(userId, request)));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDTO>> updateQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        Long userId = extractUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(cartService.updateQuantity(userId, itemId, request.getQuantity())));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId) {
        Long userId = extractUserId(userDetails);
        cartService.removeItem(userId, itemId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    private Long extractUserId(UserDetails userDetails) {
        // Person 1's UserDetails implementation should expose user ID
        // Adapt this based on how Person 1 implements UserDetails
        return Long.parseLong(userDetails.getUsername());
    }
}