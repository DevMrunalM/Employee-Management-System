package com.company.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.company.model.Employee;
import com.company.repository.EmployeeRepo;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private final EmployeeRepo employeeRepo;
    private Gson gson = new Gson();

    // Constructor Injection
    public EmployeeService(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;
    }
    
    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        logger.info("GET method called");

        // 1️⃣ Try to get ID from query parameters (?id=1)
        String idParam = request.getParameter("id");

        // 2️⃣ Try to get ID from path parameters (/employees/1)
        String pathInfo = request.getPathInfo(); // This gets "/1" if the request is "/employees/1"

        // 3️⃣ Check if sorting is requested (?sortBySalary=true)
        String sortBySalary = request.getParameter("sortBySalary");

        try {
        	if (pathInfo != null && pathInfo.equalsIgnoreCase("/sortBySalary")) {  
                // Sorting employees by salary in ascending order using path parameter
                List<Employee> sortedEmployees = employeeRepo.getAllEmployees()
                        .stream()
                        .sorted(Comparator.comparingDouble(Employee::getSalary))
                        .collect(Collectors.toList());

                out.print(gson.toJson(sortedEmployees));
            } else if (sortBySalary != null && sortBySalary.equalsIgnoreCase("true")) {  
                // Sorting employees by salary in ascending order using query parameter
                List<Employee> sortedEmployees = employeeRepo.getAllEmployees()
                        .stream()
                        .sorted(Comparator.comparingDouble(Employee::getSalary))
                        .collect(Collectors.toList());

                out.print(gson.toJson(sortedEmployees)); 
            }else if (idParam != null) {  
                // Query Parameter Handling
                int id = Integer.parseInt(idParam);
                Employee employee = employeeRepo.getEmployeeById(id);
                out.print(gson.toJson(employee));
            } else if (pathInfo != null && pathInfo.length() > 1) {  
                // Path Parameter Handling
            	try {
                    int id = Integer.parseInt(pathInfo.substring(1)); // Remove leading "/"
                    Employee employee = employeeRepo.getEmployeeById(id);
                    out.print(gson.toJson(employee));
                } catch (NumberFormatException e) {
                    logger.error("Invalid path parameter: {}", pathInfo);
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path parameter: " + pathInfo);
                }
            } else {  
                // No ID, return all employees
                List<Employee> employees = employeeRepo.getAllEmployees();
                out.print(gson.toJson(employees));
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid employee ID: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid employee ID: " + e.getMessage());
        } finally {
            out.close();
        }
    }


    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            String requestData = reader.lines().reduce("", (acc, line) -> acc + line);

            if (requestData.trim().startsWith("[")) {
                // Bulk Insert: JSON array
                Employee[] employeesArray = gson.fromJson(requestData, Employee[].class);
                for (Employee emp : employeesArray) {
                    employeeRepo.createEmployee(emp);
                }
                logger.info("Bulk employees created successfully.");
                out.print("{\"message\": \"Bulk employees created successfully.\"}");
            } else {
                // Single Employee Insert: JSON object
                Employee employee = gson.fromJson(requestData, Employee.class);
                employeeRepo.createEmployee(employee);
                logger.info("Employee created successfully: {}", gson.toJson(employee));
                out.print("{\"message\": \"Employee created successfully.\"}");
            }
            out.flush();
        } catch (Exception e) {
            logger.error("Error adding employee: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Error adding employee: " + e.getMessage() + "\"}");
        } finally {
            out.close();
        }
    }


    public void handlePut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            BufferedReader reader = request.getReader();
            String requestData = reader.lines().reduce("", (acc, line) -> acc + line);

            if (requestData.trim().startsWith("[")) {
                // Bulk Update: JSON array
                Employee[] employeesArray = gson.fromJson(requestData, Employee[].class);
                for (Employee emp : employeesArray) {
                    employeeRepo.updateEmployee(emp);
                }
                logger.info("Bulk employees updated successfully.");
                out.print("{\"message\": \"Bulk employees updated successfully.\"}");
            } else {
                // Single Employee Update: JSON object
                Employee employee = gson.fromJson(requestData, Employee.class);
                employeeRepo.updateEmployee(employee);
                logger.info("Employee updated successfully: {}", gson.toJson(employee));
                out.print("{\"message\": \"Employee updated successfully.\"}");
            }
            out.flush();
        } catch (Exception e) {
            logger.error("Error updating employee: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Error updating employee: " + e.getMessage() + "\"}");
        } finally {
            out.close();
        }
    }

    public void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            BufferedReader reader = request.getReader();
            String requestData = reader.lines().reduce("", (acc, line) -> acc + line).trim();

            if (requestData.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "{\"error\": \"Request body is empty\"}");
                return;
            }

            if (requestData.startsWith("[")) {
                // Bulk Delete: JSON array
                Integer[] idsArray = gson.fromJson(requestData, Integer[].class);
                boolean allDeleted = true;

                for (Integer id : idsArray) {
                    if (id == null || !employeeRepo.deleteEmployee(id)) {
                        allDeleted = false;
                    }
                }

                if (allDeleted) {
                    out.write("{\"message\": \"All employees deleted successfully.\"}");
                } else {
                    out.write("{\"message\": \"Some employees could not be found or deleted.\"}");
                }
            } else {
                // Single Employee Delete: JSON object
                Integer id = gson.fromJson(requestData, Integer.class);
                
                if (id == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "{\"error\": \"Invalid employee ID\"}");
                    return;
                }

                boolean isDeleted = employeeRepo.deleteEmployee(id);

                if (isDeleted) {
                    out.write("{\"message\": \"Employee deleted successfully.\"}");
                } else {
                    out.write("{\"message\": \"Employee not found.\"}");
                }
            }
            out.flush();
        } catch (Exception e) {
            logger.error("Error deleting employee: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Error deleting employee: " + e.getMessage() + "\"}");
        } finally {
            out.close();
        }
    }

}
