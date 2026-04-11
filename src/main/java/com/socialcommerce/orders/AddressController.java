package com.socialcommerce.orders;

import com.socialcommerce.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    // TODO P4: inject AddressRepository / AddressService

    @GetMapping
    public ResponseEntity<ApiResponse<?>> listAddresses() {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: list addresses for current user"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createAddress(@RequestBody Object request) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: create address"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateAddress(@PathVariable Long id, @RequestBody Object request) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: update address"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteAddress(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: delete address"));
    }
}
