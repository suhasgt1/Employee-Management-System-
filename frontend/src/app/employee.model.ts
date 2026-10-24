export interface Employee {
  id: number;
  name: string;
  departmentId: number;
  department: string;
  managerId: number | null;
  managerName: string | null;
  jobTitle: string;
  email: string;
  phone: string;
  salary: number;
}

export interface Department {
  id: number;
  name: string;
}

export type EmployeePayload = Omit<Employee, 'id' | 'department' | 'managerName'>;

export interface EmployeeForm {
  name: string;
  departmentId: number | null;
  managerId: number | null;
  jobTitle: string;
  email: string;
  phone: string;
  salary: number | null;
}
