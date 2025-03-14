package com.company.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.company.config.DBConnection;
import com.company.model.Employee;

public class EmployeeDAOImpl implements EmployeeDAO{
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
        	 e.printStackTrace();
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
    
    // Get all employees sorted by salary using Lambda Expression
    public List<Employee> getAllEmployeesSortedBySalary() {
        return getAllEmployees().stream()
                .sorted(Comparator.comparingDouble(Employee::getSalary))
                .collect(Collectors.toList());
    }

    // Get employees by department using Java 8 Stream API
    public List<Employee> getEmployeesByDepartment(String department) {
        return getAllEmployees().stream()
                .filter(emp -> emp.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }

    // Print all employees using method reference
    public void printAllEmployees() {
        getAllEmployees().forEach(System.out::println);
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
    @Override
    public boolean deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                System.out.println("No employee found with ID: " + id);
                return false; // Employee was not found
            } else {
                System.out.println("Employee with ID " + id + " deleted successfully.");
                return true; // Employee deleted successfully
            }
            
        } catch (SQLException e) {
            System.out.println("Error deleting employee: " + e.getMessage()); 
        }
        return false; // Return false in case of exception
    }
}
