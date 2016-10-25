package com.verizonwireless.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import scala.annotation.meta.setter;

import com.verizonwireless.domain.Department;
import com.verizonwireless.domain.Employee;
import com.verizonwireless.exception.DataNotFoundException;
import com.verizonwireless.exception.InvalidDataException;
import com.verizonwireless.repositories.DepartmentRepository;
import com.verizonwireless.repositories.EmployeeRepository;

@Service("companyService")
@Transactional
class CompanyServiceImpl implements CompanyService {

	@Autowired
	private final EmployeeRepository employeeRepository;
	
	@Autowired
	private final DepartmentRepository departmentRepository; 

	public CompanyServiceImpl(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
		this.employeeRepository = employeeRepository;
		this.departmentRepository = departmentRepository;
	}

	@Override
	public Employee getEmployeeDetailsById(long id) {
		return this.employeeRepository.findOne(id);
	}
	
	@Override
	public Employee createOrUpdateEmployee(Employee employee) {
		if (employee.getSalary().compareTo(employee.getDepartment().getSalaryMaxRange()) == 1) {
			throw new InvalidDataException("Employee Salary greater than Department Max Salary");
		}
		if (employee.getSalary().compareTo(employee.getDepartment().getSalaryMinRange()) == -1) {
			throw new InvalidDataException("Employee Salary less than Department Min Salary");
		}
		return this.employeeRepository.save(employee);
	}

	public void deleteEmployee(long id) {
		this.employeeRepository.delete(id);
	}

	@Override
	public Department getDepartmentDetailsById(long departmentId) {
		Department department = this.departmentRepository.findOne(departmentId);
		if (department == null) {
			throw new DataNotFoundException("Department Id "+departmentId+" is not found");
		} 
		return department;
	}

	@Override
	public Department createDepartment(Department department) {
		if (department.getSalaryMaxRange().compareTo(department.getSalaryMinRange()) == -1 ) {
			throw new InvalidDataException("Department Max Salary is less than Department Min Salary");
		}
		return this.departmentRepository.save(department);
	}

}