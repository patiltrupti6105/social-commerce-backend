package com.socialcommerce.analytics;

import com.socialcommerce.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverview() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getOverview()));
    }

    @GetMapping("/top-products")
    public ResponseEntity<ApiResponse<Object>> getTopProducts() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getTopProducts()));
    }

    @GetMapping("/orders-by-day")
    public ResponseEntity<ApiResponse<Object>> getOrdersByDay() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getOrdersByDay()));
    }
}