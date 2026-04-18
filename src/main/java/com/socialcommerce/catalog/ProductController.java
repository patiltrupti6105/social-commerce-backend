/*package com.socialcommerce.catalog;

import com.socialcommerce.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    // TODO P2: inject ProductService

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success( "TODO: list products"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success( "TODO: get product by id"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createProduct(@RequestBody Object request) {
        return ResponseEntity.ok(ApiResponse.success( "TODO: create product"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateProduct(@PathVariable Long id, @RequestBody Object request) {
        return ResponseEntity.ok(ApiResponse.success( "TODO: update product"));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<?>> submitProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success( "TODO: submit for review"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> archiveProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success( "TODO: archive product"));
    }

    @GetMapping("/seller/my")
    public ResponseEntity<ApiResponse<?>> getMyProducts() {
        return ResponseEntity.ok(ApiResponse.success( "TODO: get seller's own products"));
    }
}
*/
package com.socialcommerce.catalog;

import com.socialcommerce.catalog.entity.Product;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @GetMapping
    public List<Product> getProducts() {
        return productService.getActiveProducts();
    }
}