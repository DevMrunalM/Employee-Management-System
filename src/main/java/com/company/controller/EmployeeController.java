package com.company.controller;

import com.company.services.EmployeeService;
import com.company.repository.EmployeeRepoImpl;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/employees/*")
public class EmployeeController extends HttpServlet {
    private EmployeeService employeeService;

    @Override
    public void init() {
        // Inject EmployeeRepoImpl into EmployeeService
        employeeService = new EmployeeService(new EmployeeRepoImpl());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        employeeService.handleGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        employeeService.handlePost(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        employeeService.handlePut(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        employeeService.handleDelete(request, response);
    }
}
