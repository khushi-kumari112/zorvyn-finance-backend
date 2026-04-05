package com.zorvyn.finance.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RecordRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be a positive number")
    private Double amount;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "INCOME|EXPENSE",
            message = "Type must be either INCOME or EXPENSE")
    private String type;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}