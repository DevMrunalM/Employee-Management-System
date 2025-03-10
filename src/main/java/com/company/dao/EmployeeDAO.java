package com.company.dao;

import com.company.model.Employee;

public interface EmployeeDAO {
	
		 //insert employee data
		 public void createEmployee(Employee employee);
		 
		 //Retrieve employee by ID
		 public void getEmployeeById(int id);
		 
		 //Retrieve all employee
		 public void getAllEmployees();
		 
		 //Update existing employee
		 public void updateEmployee(Employee employee);
		 
		 //delete employee by ID
		 public void deleteEmployee(int id);
}
