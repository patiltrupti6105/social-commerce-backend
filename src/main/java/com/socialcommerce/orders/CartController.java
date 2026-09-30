package com.socialcommerce.orders;

import com.socialcommerce.common.BaseController;
import com.socialcommerce.common.response.ApiResponse;
import com.socialcommerce.orders.dto.*;
import com.socialcommerce.orders.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController extends BaseController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart() {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(currentUserNumericId())));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDTO>> addItem(@Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(cartService.addItem(currentUserNumericId(), request)));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDTO>> updateQuantity(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
            cartService.updateQuantity(currentUserNumericId(), itemId, request.getQuantity())));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> removeItem(@PathVariable Long itemId) {
        cartService.removeItem(currentUserNumericId(), itemId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        cartService.clearCart(currentUserNumericId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
