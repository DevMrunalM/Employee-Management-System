package com.company.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.company.config.DBConnection;
import com.company.model.Employee;
import com.company.services.EmployeeUtility;

public class EmployeeRepoImpl implements EmployeeRepo {
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
            System.out.println("Error inserting employee: " + e.getMessage());
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
            System.out.println("Error fetching employee by ID: " + e.getMessage());
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
            System.out.println("Error fetching all employees: " + e.getMessage());
        }
        return employees;
    }

    // Get all employees sorted by salary (using EmployeeUtility)
    public List<Employee> getAllEmployeesSortedBySalary() {
        return EmployeeUtility.getAllEmployeesSortedBySalary(getAllEmployees());
    }

    // Get employees by department (using EmployeeUtility)
    public List<Employee> getEmployeesByDepartment(String department) {
        return EmployeeUtility.getEmployeesByDepartment(getAllEmployees(), department);
    }

    // Print all employees (using EmployeeUtility)
    public void printAllEmployees() {
        EmployeeUtility.printAllEmployees(getAllEmployees());
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
    public Employee deleteEmployee(int id) {
        String selectSql = "SELECT * FROM employees WHERE id = ?";
        String deleteSql = "DELETE FROM employees WHERE id = ?";
        
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setInt(1, id);
            ResultSet rs = selectStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("No employee found with ID: " + id);
                return null;
            }

            Employee deletedEmployee = new Employee(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("age"),
                rs.getDouble("salary"),
                rs.getString("department")
            );

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, id);
                deleteStmt.executeUpdate();
            }

            System.out.println("Employee with ID " + id + " deleted successfully.");
            return deletedEmployee;

        } catch (SQLException e) {
            System.out.println("Error deleting employee: " + e.getMessage());
        }
        return null;
    }

    public int getEmployeeCount() {
        String sql = "SELECT COUNT(*) FROM employees";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting employee count: " + e.getMessage());
        }
        return 0;
    }

    
}
