package com.company.services;

import com.company.model.Employee;
import com.company.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.Collections;  

public class EmployeeUtility {

    // Get all employees sorted by salary
    public static List<Employee> getAllEmployeesSortedBySalary(List<Employee> employees) {  
        Collections.sort(employees, new EmployeeSalaryComparator());  
        return employees;
    }

    // Get employees by department
    public static List<Employee> getEmployeesByDepartment(List<Employee> employees, String department) {
        return employees.stream()
                .filter(emp -> emp.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }

    // Print all employees
    public static void printAllEmployees(List<Employee> employees) {
        employees.forEach(System.out::println);
    }

    // Check if an employee exists in the database
    public static boolean employeeExists(int id) {
        String query = "SELECT COUNT(*) FROM employees WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // Returns true if employee exists
            }
        } catch (SQLException e) {
            System.err.println("Error checking employee existence: " + e.getMessage());
        }
        return false;
    }

    // Equals method for Employee
    public static boolean areEmployeesEqual(Employee e1, Employee e2) {
        if (e1 == e2) return true;
        if (e1 == null || e2 == null) return false;
        return e1.getId() == e2.getId() &&
               e1.getAge() == e2.getAge() &&
               Double.compare(e1.getSalary(), e2.getSalary()) == 0 &&
               Objects.equals(e1.getName(), e2.getName()) &&
               Objects.equals(e1.getDepartment(), e2.getDepartment());
    }
}
