package com.zorvyn.finance.controller;

import com.zorvyn.finance.dto.ApiResponse;
import com.zorvyn.finance.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final FinancialRecordService service;

    // Total income, expense, net balance, savings rate
    // All roles can see this
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    public ResponseEntity<?> getSummary() {
        return ResponseEntity.ok(
                ApiResponse.ok("Summary fetched successfully",
                        service.getSummary()));
    }

    // Category wise breakdown — Analyst and Admin only
    @GetMapping("/category-wise")
    @PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
    public ResponseEntity<?> getCategoryWise() {
        return ResponseEntity.ok(
                ApiResponse.ok("Category wise data fetched successfully",
                        service.getCategoryWise()));
    }

    // Last 10 transactions — All roles
    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    public ResponseEntity<?> getRecentActivity() {
        return ResponseEntity.ok(
                ApiResponse.ok("Recent activity fetched successfully",
                        service.getRecentActivity()));
    }

    // Month by month trends — Analyst and Admin only
    @GetMapping("/monthly-trend")
    @PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
    public ResponseEntity<?> getMonthlyTrend() {
        return ResponseEntity.ok(
                ApiResponse.ok("Monthly trends fetched successfully",
                        service.getMonthlyTrends()));
    }
}