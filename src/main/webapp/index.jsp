<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Employee Management System</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f4f4f4;
        }
        .container {
            width: 80%;
            margin: auto;
            background: white;
            padding: 20px;
            box-shadow: 0px 0px 10px 0px #000;
        }
        h2 {
            text-align: center;
        }
        form {
            margin-bottom: 20px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        table, th, td {
            border: 1px solid black;
        }
        th, td {
            padding: 10px;
            text-align: left;
        }
        .btn {
            padding: 8px 15px;
            border: none;
            background-color: #28a745;
            color: white;
            cursor: pointer;
        }
        .btn:hover {
            background-color: #218838;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Employee Management System</h2>
        
        <form action="employees" method="post">
            <label>ID:</label>
            <input type="number" name="id" required>
            <label>Name:</label>
            <input type="text" name="name" required>
            <label>Age:</label>
            <input type="number" name="age" required>
            <label>Salary:</label>
            <input type="number" step="0.01" name="salary" required>
            <label>Department:</label>
            <input type="text" name="department" required>
            <button type="submit" class="btn">Add Employee</button>
        </form>
        
        <form action="employees" method="get">
            <label>Filter by Department:</label>
            <input type="text" name="department">
            <button type="submit" class="btn">Filter</button>
        </form>
        
        <form action="employees" method="get">
            <input type="hidden" name="sort" value="salary">
            <button type="submit" class="btn">Sort by Salary</button>
        </form>
        
        <h3>Employee List</h3>
        <iframe src="employees" width="100%" height="300px" style="border: none;"></iframe>
    </div>
</body>
</html>