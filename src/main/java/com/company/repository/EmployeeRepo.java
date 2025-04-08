package com.company.repository;

import java.util.List;
import com.company.model.Employee;

public interface EmployeeRepo {
	
	// Insert employee data
	public void createEmployee(Employee employee);
	
	// Retrieve employee by ID
	public Employee getEmployeeById(int id);
	
	// Retrieve all employees
	public List<Employee> getAllEmployees();
	
	// Update existing employee
	public void updateEmployee(Employee employee);
	
	// Delete employee by ID
	public Employee deleteEmployee(int id);
	
	// Method to get total employee count
	public int getEmployeeCount();

	// New method to get next available ID
	public int getNextAvailableId();
	
	// Bulk insert employees using multithreading
	void bulkInsertEmployees(List<Employee> employees);

}
