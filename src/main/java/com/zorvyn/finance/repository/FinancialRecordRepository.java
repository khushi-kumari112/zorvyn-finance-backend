package com.zorvyn.finance.repository;

import com.zorvyn.finance.model.FinancialRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    // Basic filters
    List<FinancialRecord> findByDeletedFalse();

    List<FinancialRecord> findByTypeAndDeletedFalse(String type);

    List<FinancialRecord> findByCategoryIgnoreCaseAndDeletedFalse(String category);

    List<FinancialRecord> findByDateBetweenAndDeletedFalse(LocalDate from, LocalDate to);

    // Dashboard - totals
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r " +
            "WHERE r.type = 'INCOME' AND r.deleted = false")
    Double getTotalIncome();

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r " +
            "WHERE r.type = 'EXPENSE' AND r.deleted = false")
    Double getTotalExpense();

    // Dashboard - category wise
    @Query("SELECT r.category, r.type, SUM(r.amount) " +
            "FROM FinancialRecord r WHERE r.deleted = false " +
            "GROUP BY r.category, r.type " +
            "ORDER BY r.category")
    List<Object[]> getCategoryWiseTotals();

    // Dashboard - recent 10 records
    @Query("SELECT r FROM FinancialRecord r " +
            "WHERE r.deleted = false ORDER BY r.date DESC")
    List<FinancialRecord> findRecentRecords(Pageable pageable);

    // Dashboard - monthly trends
    @Query("SELECT YEAR(r.date), MONTH(r.date), r.type, SUM(r.amount) " +
            "FROM FinancialRecord r WHERE r.deleted = false " +
            "GROUP BY YEAR(r.date), MONTH(r.date), r.type " +
            "ORDER BY YEAR(r.date) DESC, MONTH(r.date) DESC")
    List<Object[]> getMonthlyTrends();

    // Dashboard - record count
    @Query("SELECT COUNT(r) FROM FinancialRecord r WHERE r.deleted = false")
    Long getActiveRecordCount();

    // Paginated versions of filters
    Page<FinancialRecord> findByDeletedFalse(Pageable pageable);

    Page<FinancialRecord> findByTypeAndDeletedFalse(String type, Pageable pageable);

    Page<FinancialRecord> findByCategoryIgnoreCaseAndDeletedFalse(String category, Pageable pageable);

    Page<FinancialRecord> findByDateBetweenAndDeletedFalse(LocalDate from, LocalDate to, Pageable pageable);
}