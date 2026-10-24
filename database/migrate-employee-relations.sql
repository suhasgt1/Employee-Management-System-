USE employee_db;

CREATE TABLE IF NOT EXISTS departments (
	id BIGINT NOT NULL AUTO_INCREMENT,
	name VARCHAR(100) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE KEY uk_departments_name (name)
);

DROP PROCEDURE IF EXISTS migrate_employee_relations;
DELIMITER //
CREATE PROCEDURE migrate_employee_relations()
BEGIN
	IF NOT EXISTS (
		SELECT 1
		FROM information_schema.COLUMNS
		WHERE TABLE_SCHEMA = DATABASE()
			AND TABLE_NAME = 'employees'
			AND COLUMN_NAME = 'department_id'
	) THEN
		ALTER TABLE employees ADD COLUMN department_id BIGINT NULL;
	END IF;

	IF NOT EXISTS (
		SELECT 1
		FROM information_schema.COLUMNS
		WHERE TABLE_SCHEMA = DATABASE()
			AND TABLE_NAME = 'employees'
			AND COLUMN_NAME = 'manager_id'
	) THEN
		ALTER TABLE employees ADD COLUMN manager_id BIGINT NULL;
	END IF;

	IF EXISTS (
		SELECT 1
		FROM information_schema.COLUMNS
		WHERE TABLE_SCHEMA = DATABASE()
			AND TABLE_NAME = 'employees'
			AND COLUMN_NAME = 'department'
	) THEN
		SET @migration_sql = 'INSERT INTO departments (name)
			SELECT DISTINCT TRIM(department)
			FROM employees
			WHERE department IS NOT NULL AND TRIM(department) <> ''''
			ON DUPLICATE KEY UPDATE name = VALUES(name)';
		PREPARE migration_statement FROM @migration_sql;
		EXECUTE migration_statement;
		DEALLOCATE PREPARE migration_statement;

		SET @migration_sql = 'UPDATE employees AS e
			JOIN departments AS d ON d.name = TRIM(e.department)
			SET e.department_id = d.id';
		PREPARE migration_statement FROM @migration_sql;
		EXECUTE migration_statement;
		DEALLOCATE PREPARE migration_statement;
	END IF;

	IF EXISTS (SELECT 1 FROM employees WHERE department_id IS NULL) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Migration stopped: every employee must map to a department.';
	END IF;

	ALTER TABLE employees MODIFY COLUMN department_id BIGINT NOT NULL;

	IF NOT EXISTS (
		SELECT 1
		FROM information_schema.KEY_COLUMN_USAGE
		WHERE TABLE_SCHEMA = DATABASE()
			AND TABLE_NAME = 'employees'
			AND COLUMN_NAME = 'department_id'
			AND REFERENCED_TABLE_NAME = 'departments'
	) THEN
		ALTER TABLE employees
			ADD CONSTRAINT fk_employees_department
			FOREIGN KEY (department_id) REFERENCES departments(id);
	END IF;

	IF NOT EXISTS (
		SELECT 1
		FROM information_schema.KEY_COLUMN_USAGE
		WHERE TABLE_SCHEMA = DATABASE()
			AND TABLE_NAME = 'employees'
			AND COLUMN_NAME = 'manager_id'
			AND REFERENCED_TABLE_NAME = 'employees'
	) THEN
		ALTER TABLE employees
			ADD CONSTRAINT fk_employees_manager
			FOREIGN KEY (manager_id) REFERENCES employees(id) ON DELETE SET NULL;
	END IF;

	IF NOT EXISTS (
		SELECT 1
		FROM information_schema.TABLE_CONSTRAINTS
		WHERE TABLE_SCHEMA = DATABASE()
			AND TABLE_NAME = 'employees'
			AND CONSTRAINT_NAME = 'chk_employees_salary'
			AND CONSTRAINT_TYPE = 'CHECK'
	) THEN
		ALTER TABLE employees
			ADD CONSTRAINT chk_employees_salary
			CHECK (salary >= 0 AND salary <= 999999999999.99);
	END IF;

	IF EXISTS (
		SELECT 1
		FROM information_schema.COLUMNS
		WHERE TABLE_SCHEMA = DATABASE()
			AND TABLE_NAME = 'employees'
			AND COLUMN_NAME = 'department'
	) THEN
		SET @migration_sql = 'ALTER TABLE employees DROP COLUMN department';
		PREPARE migration_statement FROM @migration_sql;
		EXECUTE migration_statement;
		DEALLOCATE PREPARE migration_statement;
	END IF;
END//
DELIMITER ;

CALL migrate_employee_relations();
DROP PROCEDURE migrate_employee_relations;
