package com.company.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.company.dbconnection.DBConnection;
import com.company.model.Employee;


public class EmployeeDAOImpl implements EmployeeDAO {

	private Connection conn;//multiple method access same connection

	public EmployeeDAOImpl() {
        this.conn = DBConnection.getConnection(); // Get the connection from DBConnection
        if (this.conn == null) {
            System.out.println(" Database connection failed!");
        }
    }

	//Insert
	@Override
	public void createEmployee(Employee employee) {
		String sql = "INSERT INTO employees(id,name,age,salary,department) VALUES(?,?,?,?,?)";
		try(PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setInt(1, employee.getId());
			pstmt.setString(2, employee.getName());
			pstmt.setInt(3, employee.getAge());
			pstmt.setDouble(4, employee.getSalary());
			pstmt.setString(5, employee.getDepartment());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}

	//Get employee by ID
	@Override
	public void getEmployeeById(int id) {
	String sql = "SELECT * FROM employees WHERE id = ?";
	try(PreparedStatement pstmt = conn.prepareStatement(sql))
	{
		pstmt.setInt(1, id);
		ResultSet rs = pstmt.executeQuery();
		if(rs.next())
		{
			System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Age: " + rs.getInt("age") +", Salary: " + rs.getDouble("salary") + ", Department: " + rs.getString("department"));
        
		}
		
	} catch (SQLException e) {
		
		e.printStackTrace();
	}
		
	}

	@Override
	public void getAllEmployees() {
		String sql = "SELECT * FROM employees";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            List<Employee> employees = new ArrayList<>();
            while (rs.next()) {
                employees.add(new Employee(rs.getInt("id"), rs.getString("name"), rs.getInt("age"), rs.getDouble("salary"), rs.getString("department")));
            }
            employees.forEach(System.out::println);
        } catch (SQLException e) {
            e.printStackTrace();
        }	
		
	}

	@Override
	public void updateEmployee(Employee employee) {
		String sql = "UPDATE employees SET name = ?, age = ?, department = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employee.getName());
            pstmt.setInt(2, employee.getAge());
            pstmt.setDouble(3, employee.getSalary());
            pstmt.setString(4, employee.getDepartment());
            pstmt.setInt(5, employee.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	@Override
	public void deleteEmployee(int id) {
		 String sql = "DELETE FROM employees WHERE id = ?";
	        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            pstmt.setInt(1, id);
	            pstmt.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } 
		
	}
	
}
