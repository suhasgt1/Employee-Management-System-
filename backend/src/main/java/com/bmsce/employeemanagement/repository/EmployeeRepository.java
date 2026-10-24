package com.bmsce.employeemanagement.repository;

import com.bmsce.employeemanagement.entity.Employee;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Override
    @EntityGraph(attributePaths = {"department", "manager"})
    List<Employee> findAll(Sort sort);

    List<Employee> findByManager_Id(Long managerId);
}
