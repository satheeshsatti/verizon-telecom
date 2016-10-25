package com.verizonwireless.service;

import com.verizonwireless.domain.Department;
import com.verizonwireless.domain.Employee;

public interface CompanyService {

	Employee getEmployeeDetailsById(long id);

	Employee createOrUpdateEmployee(Employee employee);
	
	void deleteEmployee(long id);

	Department getDepartmentDetailsById(long departmentId);

	Department createDepartment(Department department);

}