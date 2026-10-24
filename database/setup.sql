CREATE DATABASE IF NOT EXISTS employee_db;
USE employee_db;

CREATE TABLE IF NOT EXISTS departments (
	id BIGINT NOT NULL AUTO_INCREMENT,
	name VARCHAR(100) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE KEY uk_departments_name (name)
);

CREATE TABLE IF NOT EXISTS employees (
	id BIGINT NOT NULL AUTO_INCREMENT,
	name VARCHAR(100) NOT NULL,
	department_id BIGINT NOT NULL,
	manager_id BIGINT NULL,
	job_title VARCHAR(100) NOT NULL,
	email VARCHAR(150) NOT NULL,
	phone VARCHAR(20) NOT NULL,
	salary DOUBLE NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_employees_department FOREIGN KEY (department_id) REFERENCES departments(id),
	CONSTRAINT fk_employees_manager FOREIGN KEY (manager_id) REFERENCES employees(id) ON DELETE SET NULL,
	CONSTRAINT chk_employees_salary CHECK (salary >= 0 AND salary <= 999999999999.99)
);
