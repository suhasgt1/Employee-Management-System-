package com.bmsce.employeemanagement.service;

import com.bmsce.employeemanagement.dto.EmployeeRequest;
import com.bmsce.employeemanagement.dto.EmployeeResponse;
import com.bmsce.employeemanagement.entity.Department;
import com.bmsce.employeemanagement.entity.Employee;
import com.bmsce.employeemanagement.exception.ConflictException;
import com.bmsce.employeemanagement.exception.ResourceNotFoundException;
import com.bmsce.employeemanagement.repository.DepartmentRepository;
import com.bmsce.employeemanagement.repository.EmployeeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        return toResponse(findEmployee(id));
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        Employee employee = new Employee();
        applyRequest(employee, request, null);
        return toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = findEmployee(id);
        applyRequest(employee, request, id);
        return toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = findEmployee(id);
        List<Employee> reports = employeeRepository.findByManager_Id(id);
        reports.forEach(report -> report.setManager(null));
        employeeRepository.saveAll(reports);
        employeeRepository.delete(employee);
    }

    private void applyRequest(Employee employee, EmployeeRequest request, Long employeeId) {
        employee.setName(request.name().trim());
        employee.setDepartment(departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id " + request.departmentId())));
        employee.setJobTitle(request.jobTitle().trim());
        employee.setEmail(request.email().trim());
        employee.setPhone(request.phone());
        employee.setSalary(request.salary());

        Employee manager = request.managerId() == null ? null : findEmployee(request.managerId());
        if (manager != null && wouldCreateManagerCycle(employeeId, manager)) {
            throw new ConflictException("An employee cannot manage themselves or one of their own managers");
        }
        employee.setManager(manager);
    }

    private boolean wouldCreateManagerCycle(Long employeeId, Employee proposedManager) {
        Employee current = proposedManager;
        while (current != null) {
            if (current.getId().equals(employeeId)) {
                return true;
            }
            current = current.getManager();
        }
        return false;
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));
    }

    private EmployeeResponse toResponse(Employee employee) {
        Employee manager = employee.getManager();
        Department department = employee.getDepartment();
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                department.getId(),
                department.getName(),
                manager == null ? null : manager.getId(),
                manager == null ? null : manager.getName(),
                employee.getJobTitle(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getSalary()
        );
    }
}
