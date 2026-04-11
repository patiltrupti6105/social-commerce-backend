package com.socialcommerce.search;

import com.socialcommerce.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    // TODO P2: inject SearchService (optional; listing may stay on ProductController)

    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<?>> getSuggestions(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: search suggestions"));
    }
}
