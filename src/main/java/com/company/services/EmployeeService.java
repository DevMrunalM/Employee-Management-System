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

    public EmployeeService(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    private boolean validateEmpId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer empId = (Integer) request.getAttribute("empId");

        if (empId == null) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Employee ID is required\"}");
            return false;
        }

        Employee existingEmployee = employeeRepo.getEmployeeById(empId);
        if (existingEmployee == null) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Employee ID not found\"}");
            return false;
        }
        return true;
    }

    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        logger.info("GET method called");

        Integer empId = (Integer) request.getAttribute("empId");
        String sortBySalary = request.getParameter("sortBySalary");
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo != null && pathInfo.equals("/count")) {
                // Get total employee count
                int count = employeeRepo.getEmployeeCount();
                logger.info("Total Employee Count: {}", count);
                out.print("{\"totalEmployees\": " + count + "}");
            } else if (empId != null) {
                if (!validateEmpId(request, response)) return;

                Employee employee = employeeRepo.getEmployeeById(empId);
                out.print(gson.toJson(employee));
            } else if ("true".equalsIgnoreCase(sortBySalary)) {
                List<Employee> sortedEmployees = employeeRepo.getAllEmployees();
                out.print(gson.toJson(sortedEmployees));
            } else {
                List<Employee> employees = employeeRepo.getAllEmployees();
                out.print(gson.toJson(employees));
            }
        } catch (Exception e) {
            logger.error("Error fetching employees: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Error fetching employees\"}");
        } finally {
            out.close();
        }
    }


    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            Employee employee = gson.fromJson(reader, Employee.class);
            employeeRepo.createEmployee(employee);
            logger.info("Employee created successfully: {}", gson.toJson(employee));
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"message\": \"Employee added successfully.\", \"employee\": " + gson.toJson(employee) + "}");
        } catch (Exception e) {
            logger.error("Error adding employee: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Error adding employee\"}");
        } finally {
            out.close();
        }
    }

    public void handlePut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            if (!validateEmpId(request, response)) return;

            Integer empId = (Integer) request.getAttribute("empId");

            // Fetch the existing employee details
            Employee existingEmployee = employeeRepo.getEmployeeById(empId);
            if (existingEmployee == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Employee not found\"}");
                return;
            }

            // Read input JSON
            BufferedReader reader = request.getReader();
            Employee updatedEmployee = gson.fromJson(reader, Employee.class);

            // Preserve unchanged fields
            if (updatedEmployee.getName() == null) {
                updatedEmployee.setName(existingEmployee.getName());
            }
            if (updatedEmployee.getAge() == 0) { 
                updatedEmployee.setAge(existingEmployee.getAge());
            }
            if (updatedEmployee.getSalary() == 0.0) { 
                updatedEmployee.setSalary(existingEmployee.getSalary());
            }
            if (updatedEmployee.getDepartment() == null) {
                updatedEmployee.setDepartment(existingEmployee.getDepartment());
            }

            // Ensure empId remains unchanged
            updatedEmployee.setId(empId);

            // Update the employee in the database
            employeeRepo.updateEmployee(updatedEmployee);

            logger.info("Employee updated successfully: {}", gson.toJson(updatedEmployee));
            out.print("{\"message\": \"Employee updated successfully.\", \"employee\": " + gson.toJson(updatedEmployee) + "}");
        } catch (Exception e) {
            logger.error("Error updating employee: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Error updating employee\"}");
        } finally {
            out.close();
        }
    }

    public void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Integer empId = (Integer) request.getAttribute("empId");
            if (empId == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Employee ID is required\"}");
                return;
            }

            // Get the deleted employee details
            Employee deletedEmployee = employeeRepo.deleteEmployee(empId);

            if (deletedEmployee != null) {
                logger.info("Employee deleted successfully: {}", gson.toJson(deletedEmployee));
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"message\": \"Employee deleted successfully.\", \"employee\": " + gson.toJson(deletedEmployee) + "}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Employee not found\"}");
            }
        } catch (Exception e) {
            logger.error("Error deleting employee: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error deleting employee\"}");
        } finally {
            out.close();
        }
    }

    public void getEmployeeCount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            int count = employeeRepo.getEmployeeCount();
            logger.info("Total Employee Count: {}", count);
            out.print("{\"totalEmployees\": " + count + "}");
        } catch (Exception e) {
            logger.error("Error fetching employee count: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Error fetching employee count\"}");
        } finally {
            out.close();
        }
    }

}
