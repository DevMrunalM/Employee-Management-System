package com.company.model;

import java.lang.invoke.StringConcatFactory;
import java.util.Objects;

public class Employee implements Comparable<Employee>{
	
	private int id;
	private String name;
	private int age;
	private double salary;
	private String department;
	
	//Default constructor
	public Employee() {
		super();
	}

	//parameterized constructor
	public Employee(int id, String name, int age, double salary, String department) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
		this.salary = salary;
		this.department = department;
	}
	
	//getter and setter for encapsulation
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}
	
	 @Override
	    public int compareTo(Employee other) {
	        return Double.compare(this.salary, other.salary);
	    }
	
	 @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;
	        Employee employee = (Employee) o;
	        return id == employee.id && age == employee.age && Double.compare(employee.salary, salary) == 0 && Objects.equals(name, employee.name) && Objects.equals(department, employee.department);
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(id, name, age, salary, department);
	    }

	    @Override
	    public String toString() {
	        return "Employee{" +
	                "id=" + id +
	                ", name='" + name + '\'' +
	                ", age=" + age +
	                ", salary=" + salary +
	                ", department='" + department + '\'' +
	                '}';
	    }
	}