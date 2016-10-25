package com.verizonwireless.domain;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Employee {

	@Id
	@GeneratedValue
	@ApiModelProperty(hidden=true)
	private long id;

	@Column(nullable = false)
	@NotNull
    @Size(min=1, max=50)
	private String name;

	@Column(nullable = false)
	@NotNull
    @Size(min=1, max=50)
	private String managerName;
	
	@ManyToOne
	@ApiModelProperty(hidden=true)
	private Department department;

	@Column(nullable = false)
	@DecimalMin(value = "1.0", message = "Salary cannot be less than 1")
	private BigDecimal salary;

	
	public Employee() {
		super();
	}

	public Employee(String name, String managerName, BigDecimal salary) {
		super();
		this.name = name;
		this.managerName = managerName;
		this.salary = salary;
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Department getDepartment() {
		return department;
	}
	
	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}

	public BigDecimal getSalary() {
		return salary;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", name=" + name + ", managerName="
				+ managerName + ", department=" + department + ", salary="
				+ salary + "]";
	}

}