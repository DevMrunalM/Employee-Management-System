package com.company.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.company.dao.EmployeeDAO;
import com.company.dao.EmployeeDAOImpl;
import com.company.model.Employee;

@WebServlet("/employees")
public class EmployeeServlet extends HttpServlet {
    private EmployeeDAOImpl employeeDAO = new EmployeeDAOImpl();

    // GET method
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        System.out.println("<h3>GET method called</h3>");
        out.close();
        String idParam = request.getParameter("id");

        try {
            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                employeeDAO.getEmployeeById(id);
            } else {
                employeeDAO.getAllEmployees();
            }
        } catch (NumberFormatException e) {  // 🔹 Handle invalid ID format
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid employee ID: " + e.getMessage());
        } catch (Exception e) {  // 🔹 Catch unexpected errors
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving employee data: " + e.getMessage());
        }
    }

    //Post
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html"); // Ensure correct response type
        PrintWriter out = response.getWriter();

        try {
            // 🔹 Validate inputs before parsing
            String idStr = request.getParameter("id");
            String name = request.getParameter("name");
            String ageStr = request.getParameter("age");
            String salaryStr = request.getParameter("salary");
            String department = request.getParameter("department");

            if (idStr == null || name == null || ageStr == null || salaryStr == null || department == null ||
                idStr.trim().isEmpty() || name.trim().isEmpty() || ageStr.trim().isEmpty() || salaryStr.trim().isEmpty() || department.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or empty fields");
                return;
            }

            // 🔹 Parse validated inputs
            int id = Integer.parseInt(idStr);
            int age = Integer.parseInt(ageStr);
            double salary = Double.parseDouble(salaryStr);

            // 🔹 Create employee and save to DB
            Employee employee = new Employee(id, name, age, salary, department);
            employeeDAO.createEmployee(employee);

            // 🔹 Send success response
            out.println("<h3>Employee added successfully</h3>");

        } catch (NumberFormatException e) {  
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid number format: " + e.getMessage());
        } catch (Exception e) {  
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error adding employee: " + e.getMessage());
        }
    }


    // PUT method
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Employee employee = new Employee(
                Integer.parseInt(request.getParameter("id")),
                request.getParameter("name"),
                Integer.parseInt(request.getParameter("age")),
                Double.parseDouble(request.getParameter("salary")),
                request.getParameter("department")
            );
            employeeDAO.updateEmployee(employee);
            response.getWriter().println("Employee updated successfully");
        } catch (NumberFormatException e) {  // 🔹 Handle invalid input formats
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input: " + e.getMessage());
        } catch (Exception e) {  // 🔹 Catch unexpected errors
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating employee: " + e.getMessage());
        }
    }

    // DELETE method
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            employeeDAO.deleteEmployee(id);
            response.getWriter().println("Employee deleted successfully");
        } catch (NumberFormatException e) {  // 🔹 Handle invalid ID format
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid employee ID: " + e.getMessage());
        } catch (Exception e) {  // 🔹 Catch unexpected errors
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting employee: " + e.getMessage());
        }
    }
}
