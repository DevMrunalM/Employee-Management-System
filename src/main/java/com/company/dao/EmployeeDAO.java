package com.company.dao;

import java.util.List;

import com.company.model.Employee;

public interface EmployeeDAO {
	
		 //insert employee data
		 public void createEmployee(Employee employee);
		 
		 //Retrieve employee by ID
		 public Employee getEmployeeById(int id);
		 
		 //Retrieve all employee
		 public List<Employee> getAllEmployees();
		 
		 //Update existing employee
		 public void updateEmployee(Employee employee);
		 
		 //delete employee by ID
		 public void deleteEmployee(int id);
}
