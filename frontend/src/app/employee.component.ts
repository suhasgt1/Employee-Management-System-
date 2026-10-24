import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Department, Employee, EmployeeForm, EmployeePayload } from './employee.model';
import { EmployeeService } from './employee.service';

@Component({
  selector: 'app-employee',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './employee.component.html',
  styleUrl: './employee.component.css'
})
export class EmployeeComponent implements OnInit {
  employees: Employee[] = [];
  departments: Department[] = [];
  form: EmployeeForm = this.emptyForm();
  newDepartmentName = '';
  showDepartmentCreator = false;
  addingDepartment = false;
  isEditing = false;
  editingId: number | null = null;
  loading = false;
  saving = false;
  deletingEmployeeId: number | null = null;
  errorMessage = '';
  successMessage = '';

  constructor(private readonly employeeService: EmployeeService) {}

  ngOnInit(): void {
    this.loadEmployees();
    this.loadDepartments();
  }

  loadEmployees(): void {
    this.loading = true;
    this.errorMessage = '';

    this.employeeService.getEmployees().subscribe({
      next: (employees) => {
        this.employees = employees;
        this.loading = false;
      },
      error: (error: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = this.getErrorMessage(error, 'Could not load employees. Check that the backend and MySQL are running.');
      }
    });
  }

  loadDepartments(): void {
    this.employeeService.getDepartments().subscribe({
      next: (departments) => {
        this.departments = departments;
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage = this.getErrorMessage(error, 'Could not load departments.');
      }
    });
  }

  addDepartment(): void {
    const name = this.newDepartmentName.trim();
    if (!name || this.addingDepartment) {
      return;
    }

    this.addingDepartment = true;
    this.errorMessage = '';
    this.employeeService.createDepartment(name).subscribe({
      next: (department) => {
        this.departments = [...this.departments, department].sort((left, right) =>
          left.name.localeCompare(right.name));
        this.form.departmentId = department.id;
        this.newDepartmentName = '';
        this.showDepartmentCreator = false;
        this.addingDepartment = false;
      },
      error: (error: HttpErrorResponse) => {
        this.addingDepartment = false;
        this.errorMessage = this.getErrorMessage(error, 'Unable to add department. Please try again.');
      }
    });
  }

  submitForm(formDirective: NgForm): void {
    if (this.saving) {
      return;
    }
    if (formDirective.invalid || !this.form.name.trim() || this.form.departmentId === null
      || !this.form.jobTitle.trim() || !this.form.email.trim() || !/^\d{10}$/.test(this.form.phone)
      || this.form.salary === null || !Number.isFinite(this.form.salary)
      || this.form.salary < 0 || this.form.salary > 999999999999.99) {
      formDirective.form.markAllAsTouched();
      return;
    }

    const payload: EmployeePayload = {
      name: this.form.name.trim(),
      departmentId: this.form.departmentId,
      managerId: this.form.managerId,
      jobTitle: this.form.jobTitle.trim(),
      email: this.form.email.trim(),
      phone: this.form.phone.trim(),
      salary: Number(this.form.salary)
    };

    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const request = this.isEditing && this.editingId !== null
      ? this.employeeService.updateEmployee(this.editingId, payload)
      : this.employeeService.createEmployee(payload);

    request.subscribe({
      next: () => {
        this.saving = false;
        this.successMessage = this.isEditing ? 'Employee updated successfully.' : 'Employee created successfully.';
        this.resetForm(formDirective, false);
        this.loadEmployees();
      },
      error: (error: HttpErrorResponse) => {
        this.saving = false;
        this.errorMessage = this.getErrorMessage(error, 'Unable to save employee. Please try again.');
      }
    });
  }

  editEmployee(employee: Employee): void {
    this.form = {
      name: employee.name,
      departmentId: employee.departmentId,
      managerId: employee.managerId,
      jobTitle: employee.jobTitle,
      email: employee.email,
      phone: employee.phone,
      salary: employee.salary
    };
    this.isEditing = true;
    this.editingId = employee.id;
    this.errorMessage = '';
    this.successMessage = '';
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelEdit(formDirective: NgForm): void {
    this.resetForm(formDirective);
  }

  deleteEmployee(employee: Employee): void {
    if (this.deletingEmployeeId !== null) {
      return;
    }
    const confirmed = window.confirm(`Delete employee "${employee.name}" (ID ${employee.id})?`);
    if (!confirmed) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';
    this.deletingEmployeeId = employee.id;
    this.employeeService.deleteEmployee(employee.id).subscribe({
      next: () => {
        this.deletingEmployeeId = null;
        this.successMessage = 'Employee deleted successfully.';
        this.loadEmployees();
        if (this.editingId === employee.id) {
          this.form = this.emptyForm();
          this.isEditing = false;
          this.editingId = null;
        }
      },
      error: (error: HttpErrorResponse) => {
        this.deletingEmployeeId = null;
        this.errorMessage = this.getErrorMessage(error, 'Unable to delete employee. Please try again.');
      }
    });
  }

  trackById(_index: number, employee: Employee): number {
    return employee.id;
  }

  private resetForm(formDirective: NgForm, clearMessages = true): void {
    this.form = this.emptyForm();
    this.isEditing = false;
    this.editingId = null;
    formDirective.resetForm(this.form);
    if (clearMessages) {
      this.errorMessage = '';
      this.successMessage = '';
    }
  }

  private emptyForm(): EmployeeForm {
    return { name: '', departmentId: null, managerId: null, jobTitle: '', email: '', phone: '', salary: null };
  }

  private getErrorMessage(error: HttpErrorResponse, fallback: string): string {
    if (error.error && typeof error.error.message === 'string') {
      return error.error.message;
    }
    if (error.status === 0) {
      return 'Cannot reach the API. Check that Spring Boot is running and the Angular API proxy is configured.';
    }
    return fallback;
  }
}
