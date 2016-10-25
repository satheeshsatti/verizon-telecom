package com.verizonwireless.controller;

import java.math.BigDecimal;
import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.UriTemplate;

import springfox.documentation.annotations.ApiIgnore;

import com.verizonwireless.domain.Department;
import com.verizonwireless.domain.Employee;
import com.verizonwireless.exception.DataNotFoundException;
import com.verizonwireless.exception.InvalidArgumentException;
import com.verizonwireless.service.CompanyService;

@RestController
public class CompanyController {

	@Autowired
	private CompanyService companyService;

	@RequestMapping(value = "/employees/{id}", method = RequestMethod.GET)
	public Employee getEmployeeDetailsById(@PathVariable long id) {
		Employee employee = this.companyService.getEmployeeDetailsById(id);
		if (employee == null) {
			throw new DataNotFoundException("Employee Id "+id+" is not found");
		}
		return employee;
	}

	@RequestMapping(value = "/departments/{departmentId}/employees", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> createEmployee(@PathVariable("departmentId") long departmentId, 
			@Valid @RequestBody Employee employee,@ApiIgnore @Value("#{request.requestURL}") StringBuffer url) {
		Department department = getDepartmentDetailsById(departmentId);
		employee.setDepartment(department);
		this.companyService.createOrUpdateEmployee(employee);
		URI location = getLocationForChildResource(url, employee.getId());
		return ResponseEntity.created(location).build();
	}
	
	@RequestMapping(value="/employees/{id}", method=RequestMethod.PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateEmployeeDetails(@PathVariable long id, @Valid @RequestBody Employee employee) {
		Employee employeeInfo = getEmployeeDetailsById(id);
		employee.setId(employeeInfo.getId());
		employee.setDepartment(employeeInfo.getDepartment());
		companyService.createOrUpdateEmployee(employee);
	}

	@RequestMapping(value="/employees/{id}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteEmployee(@PathVariable long id) {
		companyService.deleteEmployee(id);
	}

	@RequestMapping(value = "/departments/{id}", method = RequestMethod.GET)
	public Department getDepartmentDetailsById(@PathVariable long id) {
		Department department = this.companyService.getDepartmentDetailsById(id);
		if (department == null) {
			throw new DataNotFoundException("Department Id "+id+" is not found");
		} 
		return department;
	}

	@RequestMapping(value = "/departments", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> createDepartment(@Valid @RequestBody Department department, 
			@ApiIgnore @Value("#{request.requestURL}") StringBuffer url) {
		this.companyService.createDepartment(department);
		URI location = getLocationForChildResource(url, department.getId());
		return ResponseEntity.created(location).build();
	}
	
	@RequestMapping(value = "/departments/{departmentId}/employees/{employeeId}", method = RequestMethod.GET)
	public Employee getEmployeeAndDepartmentByIds(@PathVariable("departmentId") long departmentId, 
			@PathVariable("employeeId") long employeeId) {
		Employee employee = this.companyService.getEmployeeDetailsById(employeeId);
		if (employee == null) {
			throw new DataNotFoundException("Employee Id "+employeeId+" is not found");
		}
		if (employee.getDepartment().getId() != departmentId) {
			throw new InvalidArgumentException("Department Id"+departmentId+" is not valid for Employee Id "+employeeId);
		}
		return employee;
	}

	private URI getLocationForChildResource(StringBuffer url, Object childId) {
		String childUrl = url.append("/{childId}").toString();
		return new UriTemplate(childUrl).expand(childId);
	}

}