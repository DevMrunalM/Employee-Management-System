package com.company.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.company.dbconnection.DBConnection;
import com.company.model.Employee;

public class EmployeeDAOImpl {
    private Connection conn = DBConnection.getConnection();

    // Create Employee (INSERT)
    public void createEmployee(Employee employee) {
        String sql = "INSERT INTO employees (id, name, age, salary, department) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, employee.getId());
            pstmt.setString(2, employee.getName());
            pstmt.setInt(3, employee.getAge());
            pstmt.setDouble(4, employee.getSalary());
            pstmt.setString(5, employee.getDepartment());
            pstmt.executeUpdate();
        } catch (SQLException e) {
        	 System.out.println("Error inserting employee: " + e.getMessage()); // Exception Handling Added
        }
    }

    // Read Employee by ID (SELECT)
    public Employee getEmployeeById(int id) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Employee(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getDouble("salary"),
                    rs.getString("department")
                );
            }
        } catch (SQLException e) {
        	System.out.println("Error fetching employee by ID: " + e.getMessage()); // Exception Handling Added
        }
        return null;
    }

    // Read All Employees (SELECT)
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                employees.add(new Employee(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getDouble("salary"),
                    rs.getString("department")
                ));
            }
        } catch (SQLException e) {
        	System.out.println("Error fetching all employees: " + e.getMessage()); // Exception Handling Added
        }
        return employees;
    }

    // Update Employee (UPDATE)
    public void updateEmployee(Employee employee) {
        String sql = "UPDATE employees SET name = ?, age = ?, salary = ?, department = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employee.getName());
            pstmt.setInt(2, employee.getAge());
            pstmt.setDouble(3, employee.getSalary());
            pstmt.setString(4, employee.getDepartment());
            pstmt.setInt(5, employee.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
        	System.out.println("Error updating employee: " + e.getMessage()); 
        }
    }

    // Delete Employee (DELETE)
    public void deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
        	System.out.println("Error deleting employee: " + e.getMessage());
        }
    }
}
