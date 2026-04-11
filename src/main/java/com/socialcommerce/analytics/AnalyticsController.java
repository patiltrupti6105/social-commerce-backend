package com.socialcommerce.analytics;

import com.socialcommerce.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/analytics")
// @PreAuthorize("hasRole('ADMIN')") // TODO P4
public class AnalyticsController {

    // TODO P4: inject AnalyticsService

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<?>> getOverview() {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: overview metrics"));
    }

    @GetMapping("/top-products")
    public ResponseEntity<ApiResponse<?>> getTopProducts() {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: top products"));
    }

    @GetMapping("/orders-by-day")
    public ResponseEntity<ApiResponse<?>> getOrdersByDay() {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: orders by day"));
    }
}
