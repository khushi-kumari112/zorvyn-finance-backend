package com.zorvyn.finance.config;

import com.zorvyn.finance.model.FinancialRecord;
import com.zorvyn.finance.model.Role;
import com.zorvyn.finance.model.User;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FinancialRecordRepository recordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // Only seed if database is empty — prevents duplicate data on restart
        if (userRepository.count() > 0) return;

        // =====================
        // SEED USERS
        // =====================

        User admin = User.builder()
                .name("Khushi Admin")
                .email("admin@zorvyn.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .active(true)
                .build();

        User analyst = User.builder()
                .name("Priya Analyst")
                .email("analyst@zorvyn.com")
                .password(passwordEncoder.encode("analyst123"))
                .role(Role.ANALYST)
                .active(true)
                .build();

        User viewer = User.builder()
                .name("Rahul Viewer")
                .email("viewer@zorvyn.com")
                .password(passwordEncoder.encode("viewer123"))
                .role(Role.VIEWER)
                .active(true)
                .build();

        userRepository.save(admin);
        userRepository.save(analyst);
        userRepository.save(viewer);

        // =====================
        // SEED FINANCIAL RECORDS
        // =====================

        // April 2026
        recordRepository.save(record(75000, "INCOME",  "Salary",     "2026-04-01", "April monthly salary"));
        recordRepository.save(record(15000, "EXPENSE", "Rent",       "2026-04-02", "Monthly rent payment"));
        recordRepository.save(record(3500,  "EXPENSE", "Food",       "2026-04-03", "Weekly groceries"));
        recordRepository.save(record(1200,  "EXPENSE", "Transport",  "2026-04-04", "Monthly metro pass"));
        recordRepository.save(record(5000,  "INCOME",  "Freelance",  "2026-04-05", "Logo design project"));

        // March 2026
        recordRepository.save(record(75000, "INCOME",  "Salary",     "2026-03-01", "March monthly salary"));
        recordRepository.save(record(15000, "EXPENSE", "Rent",       "2026-03-02", "Monthly rent payment"));
        recordRepository.save(record(12000, "INCOME",  "Freelance",  "2026-03-10", "Website development project"));
        recordRepository.save(record(4200,  "EXPENSE", "Food",       "2026-03-15", "Monthly groceries"));
        recordRepository.save(record(8500,  "EXPENSE", "Utilities",  "2026-03-20", "Electricity and internet bill"));

        // February 2026
        recordRepository.save(record(75000, "INCOME",  "Salary",     "2026-02-01", "February monthly salary"));
        recordRepository.save(record(15000, "EXPENSE", "Rent",       "2026-02-02", "Monthly rent payment"));
        recordRepository.save(record(3800,  "EXPENSE", "Food",       "2026-02-14", "Groceries and dining"));
        recordRepository.save(record(20000, "INCOME",  "Bonus",      "2026-02-20", "Performance bonus Q4"));
        recordRepository.save(record(6500,  "EXPENSE", "Shopping",   "2026-02-25", "Clothing and household items"));

        System.out.println("✅ DataSeeder: 3 users and 15 financial records created successfully");
    }

    private FinancialRecord record(double amount, String type,
                                   String category, String date, String notes) {
        return FinancialRecord.builder()
                .amount(amount)
                .type(type)
                .category(category)
                .date(LocalDate.parse(date))
                .notes(notes)
                .deleted(false)
                .build();
    }
}