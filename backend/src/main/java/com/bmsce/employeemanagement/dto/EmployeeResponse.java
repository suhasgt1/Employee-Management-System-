package com.bmsce.employeemanagement.dto;

public record EmployeeResponse(
        Long id,
        String name,
        Long departmentId,
        String department,
        Long managerId,
        String managerName,
        String jobTitle,
        String email,
        String phone,
        Double salary
) {
}
