package com.bmsce.employeemanagement.controller;

import com.bmsce.employeemanagement.entity.Department;
import com.bmsce.employeemanagement.repository.DepartmentRepository;
import com.bmsce.employeemanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department engineering;

    @BeforeEach
    void clearEmployees() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
        engineering = departmentRepository.save(new Department("Engineering"));
    }

    @Test
    void supportsEmployeeCrudLifecycle() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson("Demo Employee", engineering.getId(), "null",
                                "demo@example.com", "9876543210", "45000")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Demo Employee"))
                .andExpect(jsonPath("$.department").value("Engineering"));

        Long employeeId = employeeRepository.findAll().getFirst().getId();
        Department technology = departmentRepository.save(new Department("Technology"));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/employees/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeId));

        mockMvc.perform(put("/api/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson("Updated Employee", technology.getId(), "null",
                                "updated@example.com", "9876543211", "50000")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeId))
                .andExpect(jsonPath("$.department").value("Technology"))
                .andExpect(jsonPath("$.jobTitle").value("Developer"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.phone").value("9876543211"));

        mockMvc.perform(delete("/api/employees/{id}", employeeId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void supportsDepartmentsAndManagerRelationships() throws Exception {
        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Research"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Research"));

        Long managerId = createEmployee("Manager", "manager@example.com");
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson("Direct Report", engineering.getId(), managerId.toString(),
                                "report@example.com", "9876543212", "40000")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.managerId").value(managerId))
                .andExpect(jsonPath("$.managerName").value("Manager"));

        Long directReportId = employeeRepository.findAll().stream()
                .filter(employee -> employee.getEmail().equals("report@example.com"))
                .findFirst().orElseThrow().getId();
        mockMvc.perform(put("/api/employees/{id}", managerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson("Manager", engineering.getId(), directReportId.toString(),
                                "manager@example.com", "9876543210", "45000")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("cannot manage")));

        mockMvc.perform(delete("/api/employees/{id}", managerId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/employees/{id}", directReportId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.managerId").value(nullValue()));
    }

    @Test
    void rejectsInvalidEmployeeFields() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson("", engineering.getId(), "null",
                                "demo@example.com", "9876543210", "45000")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Employee name is required")));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson("Demo", engineering.getId(), "null",
                                "demo@example.com", "98765 43210", "45000")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Phone must contain exactly 10 digits")));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson("Demo", engineering.getId(), "null",
                                "not-an-email", "9876543210", "45000")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Email must be valid")));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson("Demo", engineering.getId(), "null",
                                "demo@example.com", "9876543210", "-1")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Salary cannot be negative")));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson("Demo", engineering.getId(), "null",
                                "demo@example.com", "9876543210", "45000.123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Salary must have at most 12 integer digits and 2 decimal places")));
    }

    @Test
    void reportsMalformedRequestsAndMissingEmployees() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("invalid JSON")));

        mockMvc.perform(get("/api/employees/not-a-number"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/employees/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Employee not found")));
    }

    private Long createEmployee(String name, String email) throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson(name, engineering.getId(), "null",
                                email, "9876543210", "45000")))
                .andExpect(status().isCreated());
        return employeeRepository.findAll().stream()
                .filter(employee -> employee.getEmail().equals(email))
                .findFirst().orElseThrow().getId();
    }

    private String employeeJson(String name, Long departmentId, String managerId,
                                String email, String phone, String salary) {
        return """
                {"name":"%s","departmentId":%d,"managerId":%s,"jobTitle":"Developer","email":"%s","phone":"%s","salary":%s}
                """.formatted(name, departmentId, managerId, email, phone, salary);
    }
}
