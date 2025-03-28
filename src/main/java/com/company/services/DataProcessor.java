package com.company.services;

import com.company.model.Employee;
import com.company.repository.EmployeeRepo;
import com.company.repository.EmployeeRepoImpl;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataProcessor implements Runnable {
    private EmployeeRepo employeeDAO = new EmployeeRepoImpl();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private List<Employee> employees;
    private boolean isBackgroundTask;
    
    public DataProcessor(List<Employee> employees) {
        this.employees = employees;
        this.isBackgroundTask = false;
    }

    public DataProcessor(boolean isBackgroundTask) {
        this.isBackgroundTask = isBackgroundTask;
    }

    
   
    @Override
    public void run() {
        if (isBackgroundTask) {
            System.out.println("Running background data processing...");
            try {
                Thread.sleep(5000); // Simulating delay
                List<Employee> employees = employeeDAO.getAllEmployees();
                employees.forEach(emp -> {
                    emp.setSalary(emp.getSalary() + 5000);
                    employeeDAO.updateEmployee(emp);
                });
                System.out.println("Background data processing completed.");
            } catch (InterruptedException e) {
                System.out.println("Background task interrupted.");
            }
        } else {
            System.out.println("Processing bulk employee insertion...");
            employees.forEach(emp -> {
                executorService.execute(() -> {
                    employeeDAO.createEmployee(emp);
                    System.out.println("Inserted employee: " + emp.getName());
                });
            });
            System.out.println("Bulk employee insertion submitted to executor.");
        }
    }

    public static void shutdownExecutor() {
        executorService.shutdown();
    }
}
