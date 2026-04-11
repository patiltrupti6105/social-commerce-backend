package com.socialcommerce.orders;

import com.socialcommerce.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    // TODO P4: inject CartService

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getCart() {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: get cart"));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<?>> addItem(@RequestBody Object request) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: add item to cart"));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<?>> updateItem(@PathVariable Long itemId, @RequestBody Object request) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: update item quantity"));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<?>> removeItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: remove item"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<?>> clearCart() {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: clear cart"));
    }
}
