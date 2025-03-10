package com.company.test;

import com.company.dao.EmployeeDAO;
import com.company.dao.EmployeeDAOImpl;
import com.company.model.Employee;

public class EmployeeTest {

	 public static void main(String[] args) {
		
		 EmployeeDAO employeeDAO = new EmployeeDAOImpl();
		 
		 Employee employee = new Employee(1, "mrunal", 28, 80000, "IT");
		 employeeDAO.createEmployee(employee);
		 System.out.println("Employee inserted successfully!!");
		 
		 

	        // Fetch employee by ID
		 System.out.println("Fetching Employee Details:");
	     employeeDAO.getEmployeeById(1);
	}
	 
}
