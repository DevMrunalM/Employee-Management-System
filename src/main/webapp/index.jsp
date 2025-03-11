<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Employee Management System</title>
</head>
<body>
    <h2>Welcome to Employee Management System</h2>
    
    <p>Click below to access Employee Servlet:</p>
    <a href="employees">Go to EmployeeServlet (GET)</a> 

    <h3>Enter Employee Details</h3>
   <form action="employees" method="post">
    <label for="id">Employee ID:</label>
    <input type="text" id="id" name="id" required><br><br>

    <label for="name">Name:</label>
    <input type="text" id="name" name="name" required><br><br>

    <label for="age">Age:</label>
    <input type="number" id="age" name="age" required><br><br>

    <label for="salary">Salary:</label>
    <input type="number" id="salary" name="salary" required><br><br>

    <label for="department">Department:</label>
    <input type="text" id="department" name="department" required><br><br>

    <button type="submit">Submit (POST)</button>
</form>


    <%-- Debugging: Print out request information --%>
    <p>Context Path: <%= request.getContextPath() %></p>
</body>
</html>
