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

import com.company.config.DBConnection;
import com.company.dao.EmployeeDAO;
import com.company.dao.EmployeeDAOImpl;
import com.company.model.Employee;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

@WebServlet("/employees/*")
public class EmployeeServlet extends HttpServlet {
    private EmployeeDAOImpl employeeDAO = new EmployeeDAOImpl();
    private Gson gson = new Gson();

    // GET method
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        System.out.println("<h3>GET method called</h3>");

        String idParam = request.getParameter("id");

        try {
            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                Employee employee = employeeDAO.getEmployeeById(id);
                String jsonResponse = gson.toJson(employee);
                out.print(jsonResponse);
            } else {
                List<Employee> employees = employeeDAO.getAllEmployees();
                String jsonResponse = gson.toJson(employees);
                out.print(jsonResponse);
            }
        } catch (NumberFormatException e) {  
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid employee ID: " + e.getMessage());
        } catch (Exception e) {  
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving employee data: " + e.getMessage());
        } finally {
            out.close();
        }
    }

 // POST method
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Read JSON from request body instead of using getParameter()
            BufferedReader reader = request.getReader();
            Employee employee = gson.fromJson(reader, Employee.class);

            // Extract values from the Employee object
            int id = employee.getId();
            String name = employee.getName();
            int age = employee.getAge();
            double salary = employee.getSalary();
            String department = employee.getDepartment();

            // Validation check (same as before)
            if (id == 0 || name == null || age == 0 || salary == 0.0 || department == null ||
                name.trim().isEmpty() || department.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "{\"error\": \"Missing or empty fields\"}");
                return;
            }

            // Save to database
            employeeDAO.createEmployee(employee);

            // Convert response to JSON and send
            String jsonResponse = gson.toJson(employee);
            out.print(jsonResponse);
            out.flush();

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Error adding employee: " + e.getMessage() + "\"}");
        }
    }

    // PUT method
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Read JSON request body
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                jsonBuffer.append(line);
            }

            String requestBody = jsonBuffer.toString();
            System.out.println("Received JSON: " + requestBody); // Debugging

            // Convert JSON to Employee object
            Employee employee = gson.fromJson(requestBody, Employee.class);
            System.out.println("Parsed Employee: " + employee); // Debugging

            // Check if Employee ID exists before updating
            Employee existingEmployee = employeeDAO.getEmployeeById(employee.getId());
            if (existingEmployee == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Employee ID not found: " + employee.getId() + "\"}");
                out.flush();
                return;
            }

            // Call update method
            employeeDAO.updateEmployee(employee);
            System.out.println("Employee updated successfully"); // Debugging

            // Send updated employee data as response
            String jsonResponse = gson.toJson(employee);
            out.print(jsonResponse);
            out.flush();

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Error updating employee: " + e.getMessage() + "\"}");
        }
    }
    // DELETE method
    @Override
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
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Invalid ID format. Please provide a numeric ID.\"}");
            return;
        }

        EmployeeDAO employeeDao = new EmployeeDAOImpl();
        boolean isDeleted = employeeDao.deleteEmployee(id);

        if (isDeleted) {
            response.setStatus(HttpServletResponse.SC_OK);
            out.write("{\"message\": \"Employee deleted successfully.\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.write("{\"error\": \"Employee not found.\"}");
        }
    }
         
} 
