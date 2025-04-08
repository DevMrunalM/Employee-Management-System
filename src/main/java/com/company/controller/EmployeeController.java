package com.company.controller;

import com.company.services.EmployeeService;
import com.company.repository.EmployeeRepoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/employees/*")
public class EmployeeController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private EmployeeService employeeService;

    @Override
    public void init() throws ServletException {
        this.employeeService = new EmployeeService(new EmployeeRepoImpl());
    }

    private Integer extractEmpId(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                return Integer.parseInt(pathInfo.substring(1));
            } catch (NumberFormatException e) {
                logger.error("Invalid employee ID format: {}", pathInfo);
                return null;
            }
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer empId = extractEmpId(request);
        request.setAttribute("empId", empId);
        employeeService.handleGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/process-data".equals(pathInfo)) {
            employeeService.handleProcessData(request, response);
            return;
        }

        String requestBody = request.getReader().lines().collect(Collectors.joining());
        request.setAttribute("requestBody", requestBody);
        employeeService.handlePost(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer empId = extractEmpId(request);
        request.setAttribute("empId", empId);

        String requestBody = request.getReader().lines().collect(Collectors.joining());
        request.setAttribute("requestBody", requestBody);
        employeeService.handlePut(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer empId = extractEmpId(request);
        request.setAttribute("empId", empId);

        String requestBody = request.getReader().lines().collect(Collectors.joining());
        request.setAttribute("requestBody", requestBody);
        employeeService.handleDelete(request, response);
    }
}