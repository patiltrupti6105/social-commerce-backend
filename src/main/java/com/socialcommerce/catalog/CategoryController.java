package com.socialcommerce.catalog;

import com.socialcommerce.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    // TODO P2: inject CategoryRepository / CategoryService

    @GetMapping
    public ResponseEntity<ApiResponse<?>> listCategories() {
        return ResponseEntity.ok(ApiResponse.success( "TODO: list categories (public)"));
    }
}
