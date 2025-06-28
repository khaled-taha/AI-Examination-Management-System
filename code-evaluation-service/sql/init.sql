-- Create test database
CREATE DATABASE IF NOT EXISTS testdb;
USE testdb;

-- Create sample tables for SQL evaluation tests
CREATE TABLE IF NOT EXISTS employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    salary DECIMAL(10,2) NOT NULL,
    hire_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    location VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    grade DECIMAL(3,2) NOT NULL,
    major VARCHAR(50)
);

-- Insert sample data
INSERT INTO employees (name, department, salary, hire_date) VALUES
('John Doe', 'Engineering', 75000.00, '2020-01-15'),
('Jane Smith', 'Marketing', 65000.00, '2019-03-20'),
('Bob Johnson', 'Engineering', 80000.00, '2018-07-10'),
('Alice Brown', 'HR', 55000.00, '2021-02-28'),
('Charlie Wilson', 'Engineering', 70000.00, '2020-11-05');

INSERT INTO departments (name, location) VALUES
('Engineering', 'Building A'),
('Marketing', 'Building B'),
('HR', 'Building C'),
('Finance', 'Building A');

INSERT INTO students (name, age, grade, major) VALUES
('Alice Johnson', 20, 3.85, 'Computer Science'),
('Bob Smith', 22, 3.92, 'Mathematics'),
('Carol Davis', 21, 3.78, 'Physics'),
('David Wilson', 23, 3.65, 'Computer Science'),
('Eva Brown', 20, 3.88, 'Engineering');

-- Create indexes for better performance
CREATE INDEX idx_employees_department ON employees(department);
CREATE INDEX idx_employees_salary ON employees(salary);
CREATE INDEX idx_students_major ON students(major);
CREATE INDEX idx_students_grade ON students(grade);

-- Create a view for testing
CREATE VIEW user_summary AS
SELECT 
    category,
    COUNT(*) as total_products,
    AVG(price) as avg_price
FROM products 
GROUP BY category; 