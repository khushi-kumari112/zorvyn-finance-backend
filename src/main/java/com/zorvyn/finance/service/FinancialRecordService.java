package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.RecordRequest;
import com.zorvyn.finance.model.FinancialRecord;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import org.springframework.data.domain.PageRequest;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository repo;

    // =====================
    // CRUD OPERATIONS
    // =====================

    public FinancialRecord create(RecordRequest req) {
        FinancialRecord record = FinancialRecord.builder()
                .amount(req.getAmount())
                .type(req.getType().toUpperCase())
                .category(req.getCategory())
                .date(req.getDate())
                .notes(req.getNotes())
                .deleted(false)
                .build();
        return repo.save(record);
    }

    public Page<FinancialRecord> getAll(String type,
                                        String category,
                                        LocalDate from,
                                        LocalDate to,
                                        int page,
                                        int size) {

        // Always sort by date descending — newest first
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        if (type != null && !type.isBlank()) {
            return repo.findByTypeAndDeletedFalse(type.toUpperCase(), pageable);
        }
        if (category != null && !category.isBlank()) {
            return repo.findByCategoryIgnoreCaseAndDeletedFalse(category, pageable);
        }
        if (from != null && to != null) {
            if (from.isAfter(to)) {
                throw new RuntimeException("From date cannot be after to date");
            }
            return repo.findByDateBetweenAndDeletedFalse(from, to, pageable);
        }
        return repo.findByDeletedFalse(pageable);
    }

    public FinancialRecord getById(Long id) {
        return repo.findById(id)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new RuntimeException(
                        "Record not found with id: " + id));
    }

    public FinancialRecord update(Long id, RecordRequest req) {
        FinancialRecord record = getById(id);
        record.setAmount(req.getAmount());
        record.setType(req.getType().toUpperCase());
        record.setCategory(req.getCategory());
        record.setDate(req.getDate());
        record.setNotes(req.getNotes());
        return repo.save(record);
    }

    // Soft delete — marks as deleted, never removed from DB
    // Financial data must always be preserved for audit trail
    public void softDelete(Long id) {
        FinancialRecord record = getById(id);
        record.setDeleted(true);
        repo.save(record);
    }

    // =====================
    // DASHBOARD LOGIC
    // =====================

    public Map<String, Object> getSummary() {
        double totalIncome  = repo.getTotalIncome();
        double totalExpense = repo.getTotalExpense();
        double netBalance   = totalIncome - totalExpense;
        long recordCount    = repo.getActiveRecordCount();

        // Savings rate — shows financial health
        double savingsRate = totalIncome > 0
                ? Math.round((netBalance / totalIncome) * 100.0)
                : 0.0;

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalIncome",   totalIncome);
        summary.put("totalExpense",  totalExpense);
        summary.put("netBalance",    netBalance);
        summary.put("savingsRate",   savingsRate + "%");
        summary.put("totalRecords",  recordCount);
        summary.put("asOf",          LocalDate.now().toString());

        return summary;
    }

    public List<Map<String, Object>> getCategoryWise() {
        List<Object[]> raw = repo.getCategoryWiseTotals();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : raw) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("category", row[0]);
            map.put("type",     row[1]);
            map.put("total",    row[2]);
            result.add(map);
        }
        return result;
    }

    public List<FinancialRecord> getRecentActivity() {
        return repo.findRecentRecords(PageRequest.of(0, 10));
    }

    public List<Map<String, Object>> getMonthlyTrends() {
        List<Object[]> raw = repo.getMonthlyTrends();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : raw) {
            int monthNumber = ((Number) row[1]).intValue();
            String monthName = Month.of(monthNumber).name();

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("year",  row[0]);
            map.put("month", monthName);
            map.put("type",  row[2]);
            map.put("total", row[3]);
            result.add(map);
        }
        return result;
    }
}