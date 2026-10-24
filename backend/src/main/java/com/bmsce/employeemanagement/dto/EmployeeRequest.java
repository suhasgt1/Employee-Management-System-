package com.bmsce.employeemanagement.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmployeeRequest(
        @NotBlank(message = "Employee name is required")
        @Size(max = 100, message = "Employee name must be at most 100 characters")
        String name,
        @NotNull(message = "Department is required")
        Long departmentId,
        Long managerId,
        @NotBlank(message = "Job title is required")
        @Size(max = 100, message = "Job title must be at most 100 characters")
        String jobTitle,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 150, message = "Email must be at most 150 characters")
        String email,
        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[0-9]{10}$", message = "Phone must contain exactly 10 digits")
        String phone,
        @NotNull(message = "Salary is required")
        @DecimalMin(value = "0.0", message = "Salary cannot be negative")
        @DecimalMax(value = "999999999999.99", message = "Salary cannot exceed 999999999999.99")
        @Digits(integer = 12, fraction = 2, message = "Salary must have at most 12 integer digits and 2 decimal places")
        Double salary
) {
}
