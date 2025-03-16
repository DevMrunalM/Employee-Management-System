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
            Employee employee = gson.fromJson(reader, Employee.class);

            if (employee.getId() == 0 || employee.getName() == null || employee.getAge() == 0 || 
                employee.getSalary() == 0.0 || employee.getDepartment() == null || 
                employee.getName().trim().isEmpty() || employee.getDepartment().trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "{\"error\": \"Missing or empty fields\"}");
                return;
            }

            employeeDAO.createEmployee(employee);
            logger.info("Employee created: {}", employee);
            out.print(gson.toJson(employee));
            out.flush();
        } catch (Exception e) {
            logger.error("Error adding employee: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Error adding employee: " + e.getMessage() + "\"}");
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                jsonBuffer.append(line);
            }

            String requestBody = jsonBuffer.toString();
            logger.debug("Received JSON: {}", requestBody);
            Employee employee = gson.fromJson(requestBody, Employee.class);

            Employee existingEmployee = employeeDAO.getEmployeeById(employee.getId());
            if (existingEmployee == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Employee ID not found: " + employee.getId() + "\"}");
                out.flush();
                return;
            }

            employeeDAO.updateEmployee(employee);
            logger.info("Employee updated successfully: {}", employee);
            out.print(gson.toJson(employee));
            out.flush();
        } catch (Exception e) {
            logger.error("Error updating employee: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Error updating employee: " + e.getMessage() + "\"}");
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"ID parameter is missing\"}");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            logger.error("Invalid ID format: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Invalid ID format. Please provide a numeric ID.\"}");
            return;
        }

        boolean isDeleted = employeeDAO.deleteEmployee(id);
        if (isDeleted) {
            logger.info("Employee deleted: ID {}", id);
            response.setStatus(HttpServletResponse.SC_OK);
            out.write("{\"message\": \"Employee deleted successfully.\"}");
        } else {
            logger.warn("Employee not found: ID {}", id);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.write("{\"error\": \"Employee not found.\"}");
        }
    }
}
