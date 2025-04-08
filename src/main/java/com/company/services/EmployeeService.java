package com.company.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.company.model.Employee;
import com.company.repository.EmployeeRepo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

    public void handleProcessData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Thread backgroundThread = new Thread(() -> {
        	//thread does nothing, wait for 8 seconds
//            try {
//                long start = System.currentTimeMillis();
//                Thread.sleep(8000);
//                long end = System.currentTimeMillis();
//                long delay = (end - start) / 1000;
//                logger.info("Data processing completed in {} seconds", delay);
//            } catch (InterruptedException e) {
//                logger.error("Background processing was interrupted: {}", e.getMessage());
//            }
        	
        	//5% salary hike
        	try {
                List<Employee> employees = employeeRepo.getAllEmployees();
                for (Employee e : employees) {
                    e.setSalary(e.getSalary() * 1.05); // 5% hike
                    employeeRepo.updateEmployee(e);
                }
                logger.info("Employee salaries updated with a 5% hike.");
            } catch (Exception e) {
                logger.error("Error processing employee salary hike: {}", e.getMessage());
            }
        	
        });

        backgroundThread.start();

        response.setContentType("text/plain");
        response.getWriter().write("Background employee data processing has started. It may take a few seconds...\n");
    }

    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        logger.info("GET method called");

        Integer empId = (Integer) request.getAttribute("empId");
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo != null && pathInfo.equals("/count")) {
                int count = employeeRepo.getEmployeeCount();
                logger.info("Total Employee Count: {}", count);
                out.print("{\"totalEmployees\": " + count + "}");
            } else if (pathInfo != null && pathInfo.equals("/salary/sort")) {
                List<Employee> sortedEmployees = employeeRepo.getAllEmployees();
                sortedEmployees.sort(Comparator.comparingDouble(Employee::getSalary));
                out.print(gson.toJson(sortedEmployees));
            } else if (empId != null) {
                if (!validateEmpId(request, response)) return;
                Employee employee = employeeRepo.getEmployeeById(empId);
                out.print(gson.toJson(employee));
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
            String pathInfo = request.getPathInfo();
            String requestBody = (String) request.getAttribute("requestBody");

            if ("/bulk-insert".equalsIgnoreCase(pathInfo)) {
                List<Employee> employees = gson.fromJson(requestBody, new TypeToken<List<Employee>>() {}.getType());
                int nextId = employeeRepo.getNextAvailableId();
                for (Employee employee : employees) {
                    if (employee.getId() == 0) {
                        employee.setId(nextId++);
                    }
                    employeeRepo.createEmployee(employee);
                    logger.info("Bulk Employee added: {}", gson.toJson(employee));
                }
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"message\": \"Bulk employee records added successfully.\", \"employees\": " + gson.toJson(employees) + "}");
            } else {
                Employee employee = gson.fromJson(requestBody, Employee.class);
                if (employee.getId() == 0) {
                    employee.setId(employeeRepo.getNextAvailableId());
                }
                employeeRepo.createEmployee(employee);
                logger.info("Employee created successfully: {}", gson.toJson(employee));
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"message\": \"Employee added successfully.\", \"employee\": " + gson.toJson(employee) + "}");
            }
        } catch (Exception e) {
            logger.error("Error adding employee(s): {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Error adding employee(s)\"}");
        } finally {
            out.close();
        }
    }


    public void handlePut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String requestBody = (String) request.getAttribute("requestBody");

            //  Bulk data
            if (requestBody.trim().startsWith("[")) {
                List<Employee> employees = gson.fromJson(requestBody, new TypeToken<List<Employee>>() {}.getType());
                List<Employee> updatedEmployees = new ArrayList<>();

                for (Employee updatedEmployee : employees) {
                    Employee existingEmployee = employeeRepo.getEmployeeById(updatedEmployee.getId());
                    if (existingEmployee != null) {
                        if (updatedEmployee.getName() == null) updatedEmployee.setName(existingEmployee.getName());
                        if (updatedEmployee.getAge() == 0) updatedEmployee.setAge(existingEmployee.getAge());
                        if (updatedEmployee.getSalary() == 0.0) updatedEmployee.setSalary(existingEmployee.getSalary());
                        if (updatedEmployee.getDepartment() == null) updatedEmployee.setDepartment(existingEmployee.getDepartment());
                        employeeRepo.updateEmployee(updatedEmployee);
                        updatedEmployees.add(updatedEmployee);
                        logger.info("Bulk Employee updated: {}", gson.toJson(updatedEmployee));
                    }
                }
                out.print("{\"message\": \"Bulk employee update successful.\", \"employees\": " + gson.toJson(updatedEmployees) + "}");
            }

            // Single Data
            else {
                if (!validateEmpId(request, response)) return;
                Integer empId = (Integer) request.getAttribute("empId");
                Employee existingEmployee = employeeRepo.getEmployeeById(empId);

                if (existingEmployee == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Employee ID not found\"}");
                    return;
                }

                Employee updatedEmployee = gson.fromJson(requestBody, Employee.class);

                if (updatedEmployee.getName() == null) updatedEmployee.setName(existingEmployee.getName());
                if (updatedEmployee.getAge() == 0) updatedEmployee.setAge(existingEmployee.getAge());
                if (updatedEmployee.getSalary() == 0.0) updatedEmployee.setSalary(existingEmployee.getSalary());
                if (updatedEmployee.getDepartment() == null) updatedEmployee.setDepartment(existingEmployee.getDepartment());

                updatedEmployee.setId(empId);
                employeeRepo.updateEmployee(updatedEmployee);

                logger.info("Employee updated successfully: {}", gson.toJson(updatedEmployee));
                out.print("{\"message\": \"Employee updated successfully.\", \"employee\": " + gson.toJson(updatedEmployee) + "}");
            }
        } catch (Exception e) {
            logger.error("Error updating employee(s): {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Error updating employee(s)\"}");
        } finally {
            out.close();
        }
    }

    public void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String requestBody = (String) request.getAttribute("requestBody");

            if (requestBody.trim().startsWith("[")) {
                List<Integer> empIds = gson.fromJson(requestBody, new TypeToken<List<Integer>>() {}.getType());
                List<Employee> deletedEmployees = new ArrayList<>();
                for (Integer empId : empIds) {
                    Employee deleted = employeeRepo.deleteEmployee(empId);
                    if (deleted != null) {
                        deletedEmployees.add(deleted);
                        logger.info("Bulk Employee deleted: {}", gson.toJson(deleted));
                    }
                }
                out.print("{\"message\": \"Bulk employee delete successful.\", \"employees\": " + gson.toJson(deletedEmployees) + "}");
            } else {
                Integer empId = (Integer) request.getAttribute("empId");
                if (empId == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Employee ID is required\"}");
                    return;
                }
                Employee deletedEmployee = employeeRepo.deleteEmployee(empId);
                if (deletedEmployee != null) {
                    logger.info("Employee deleted successfully: {}", gson.toJson(deletedEmployee));
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print("{\"message\": \"Employee deleted successfully.\", \"employee\": " + gson.toJson(deletedEmployee) + "}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Employee not found\"}");
                }
            }
        } catch (Exception e) {
            logger.error("Error deleting employee(s): {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error deleting employee(s)\"}");
        } finally {
            out.close();
        }
    }
}