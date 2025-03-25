package com.company.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.company.dao.EmployeeDAOImpl;
import com.company.model.Employee;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/employees/*")
public class EmployeeServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServlet.class);
    private EmployeeDAOImpl employeeDAO = new EmployeeDAOImpl();
    private Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        logger.info("GET method called");

        String idParam = request.getParameter("id");

        try {
            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                Employee employee = employeeDAO.getEmployeeById(id);
                out.print(gson.toJson(employee));
            } else {
                List<Employee> employees = employeeDAO.getAllEmployees();
                out.print(gson.toJson(employees));
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid employee ID: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid employee ID: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error retrieving employee data: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving employee data: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            Employee[] employees = gson.fromJson(reader, Employee[].class);

            for (Employee employee : employees) {
                if (employee.getId() == 0 || employee.getName() == null || employee.getAge() == 0 ||
                    employee.getSalary() == 0.0 || employee.getDepartment() == null ||
                    employee.getName().trim().isEmpty() || employee.getDepartment().trim().isEmpty()) {
                    logger.warn("Skipping employee with missing/invalid fields: {}", gson.toJson(employee));
                    continue; // Skip invalid employee data
                }

                employeeDAO.createEmployee(employee);
                logger.info("Employee created successfully: {}", gson.toJson(employee));
            }

            out.print("{\"message\": \"Employees created successfully.\"}");
            out.flush();
        } catch (Exception e) {
            logger.error("Error adding employees: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Error adding employees: " + e.getMessage() + "\"}");
        }
    }


    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            BufferedReader reader = request.getReader();
            Employee[] employees = gson.fromJson(reader, Employee[].class);

            boolean allUpdated = true;
            StringBuilder failedIds = new StringBuilder();

            for (Employee employee : employees) {
                Employee existingEmployee = employeeDAO.getEmployeeById(employee.getId());
                if (existingEmployee != null) {
                    employeeDAO.updateEmployee(employee);
                    logger.info("Employee updated: {}", employee);
                } else {
                    failedIds.append(employee.getId()).append(", ");
                    allUpdated = false;
                }
            }

            if (allUpdated) {
                out.print("{\"message\": \"All employees updated successfully.\"}");
            } else {
                out.print("{\"message\": \"Some employees were not updated. Failed IDs: " + failedIds.toString() + "\"}");
            }
            out.flush();
        } catch (Exception e) {
            logger.error("Error updating employees: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Error updating employees: " + e.getMessage() + "\"}");
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            BufferedReader reader = request.getReader();
            int[] ids = gson.fromJson(reader, int[].class);

            for (int id : ids) {
                boolean isDeleted = employeeDAO.deleteEmployee(id);
                if (!isDeleted) {
                    logger.warn("Employee not found: ID {}", id);
                } else {
                    logger.info("Employee deleted: ID {}", id);
                }
            }

            out.write("{\"message\": \"Employees deleted successfully.\"}");
            out.flush();
        } catch (Exception e) {
            logger.error("Error deleting employees: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Error deleting employees: " + e.getMessage() + "\"}");
        }
    }
}
