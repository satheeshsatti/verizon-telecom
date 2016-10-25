package com.verizonwireless.domain;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Department {

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
	@DecimalMin(value = "1.0", message = "Max Min cannot be less than 1")
	private BigDecimal salaryMinRange;

	@Column(nullable = false)
	@NotNull
	@DecimalMin(value = "1.0", message = "Max Salary cannot be less than 1")
	private BigDecimal salaryMaxRange;
	
	public Department() {
		super();
	}

	public Department(String name, BigDecimal salaryMinRange,
			BigDecimal salaryMaxRange) {
		super();
		this.name = name;
		this.salaryMinRange = salaryMinRange;
		this.salaryMaxRange = salaryMaxRange;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getSalaryMinRange() {
		return salaryMinRange;
	}

	public BigDecimal getSalaryMaxRange() {
		return salaryMaxRange;
	}

	@Override
	public String toString() {
		return "Department [id=" + id + ", name=" + name + ", salarMinRange="
				+ salaryMinRange + ", salaryMaxRange=" + salaryMaxRange + "]";
	}
}