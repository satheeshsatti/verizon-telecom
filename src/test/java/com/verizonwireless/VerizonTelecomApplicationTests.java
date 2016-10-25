package com.verizonwireless;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import com.verizonwireless.domain.Department;
import com.verizonwireless.domain.Employee;
import com.verizonwireless.exception.ErrorMessage;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class VerizonTelecomApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void testGetEmployeeDetailsByIdWithDepartment() {
		String url = "/employees/{employeeId}";
		Employee employee = restTemplate.getForObject(url, Employee.class, 1);
		assertEquals("employee 1", employee.getName());
		assertEquals("department 1", employee.getDepartment().getName());
	}

	@Test
	public void testGetEmployeeDetailsByIdNotFound() {
		String url = "/employees/{employeeId}";
		ResponseEntity<ErrorMessage> employeeResponse = restTemplate.getForEntity(url, ErrorMessage.class, 100);
		assertEquals(HttpStatus.NOT_FOUND, employeeResponse.getStatusCode());
		assertEquals(1, employeeResponse.getBody().getErrors().size());
	}
	
	@Test
	public void testGetDepartmentDetailsById() {
		String url = "/departments/{id}";
		Department department = restTemplate.getForObject(url, Department.class, 1);
		assertEquals("department 1", department.getName());
	}

	@Test
	public void testGetDepartmentDetailsByIdNotFound() {
		String url = "/departments/{id}";
		ResponseEntity<ErrorMessage> employeeResponse = restTemplate.getForEntity(url, ErrorMessage.class, 100);
		assertEquals(HttpStatus.NOT_FOUND, employeeResponse.getStatusCode());
		assertEquals(1, employeeResponse.getBody().getErrors().size());
	}

	@Test
	public void testCreateEmployee() {
		String url = "/departments/{departmentId}/employees";
		Employee employee = new Employee("New Emp", "MGR 1", new BigDecimal(120.0));
		URI uri = restTemplate.postForLocation(url, employee, 1);
		assertNotNull(uri);
	}
	
	@Test
	public void testCreateEmployeeInvalidDepartment() {
		String url = "/departments/{departmentId}/employees";
		Employee employee = new Employee("New Emp", "MGR 1", new BigDecimal(120.0));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Employee> entity = new HttpEntity<Employee>(employee, headers);
		ResponseEntity<ErrorMessage> response = restTemplate.exchange(url, HttpMethod.POST, entity, ErrorMessage.class, 100);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals(1, response.getBody().getErrors().size());
	}
	
	@Test
	public void testCreateEmployeeSalaryLessThanDepartmentMinSalary() {
		String url = "/departments/{departmentId}/employees";
		Employee employee = new Employee("New Emp", "MGR 1", new BigDecimal(10.0));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Employee> entity = new HttpEntity<Employee>(employee, headers);
		ResponseEntity<ErrorMessage> response = restTemplate.exchange(url, HttpMethod.POST, entity, ErrorMessage.class, 1);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(1, response.getBody().getErrors().size());
	}

	@Test
	public void testCreateEmployeeSalaryGreaterThanDepartmentMaxSalary() {
		String url = "/departments/{departmentId}/employees";
		Employee employee = new Employee("New Emp", "MGR 1", new BigDecimal(500.0));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Employee> entity = new HttpEntity<Employee>(employee, headers);
		ResponseEntity<ErrorMessage> response = restTemplate.exchange(url, HttpMethod.POST, entity, ErrorMessage.class, 1);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(1, response.getBody().getErrors().size());
	}
	
	@Test
	public void testUpdateEmployeeDetails() {
		String url = "/employees/{id}";
		Employee employee = new Employee("Upd Emp 4", "MGR 4", new BigDecimal(250.0));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Employee> entity = new HttpEntity<Employee>(employee, headers);
		ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class, 4);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		Employee updatedEmployee = restTemplate.getForObject(url, Employee.class, 4);
		assertEquals("Upd Emp 4", updatedEmployee.getName());
	}

	@Test
	public void testUpdateEmployeeDetailsWithInvalidManager() {
		String url = "/employees/{id}";
		Employee employee = new Employee("Upd Emp 4", null, new BigDecimal(100.0));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Employee> entity = new HttpEntity<Employee>(employee, headers);
		ResponseEntity<ErrorMessage> response = restTemplate.exchange(url, HttpMethod.PUT, entity, ErrorMessage.class, 4);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(1, response.getBody().getErrors().size());
	}
	
	@Test
	public void testDeleteEmployee() {
		String url = "/employees/{id}";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Void> entity = new HttpEntity<Void>(headers);
		ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class, 5);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<ErrorMessage> employeeResponse = restTemplate.getForEntity(url, ErrorMessage.class, 5);
		assertEquals(HttpStatus.NOT_FOUND, employeeResponse.getStatusCode());
		assertEquals(1, employeeResponse.getBody().getErrors().size());
		
	}

	@Test
	public void testDeleteEmployeeNotFound() {
		String url = "/employees/{id}";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Void> entity = new HttpEntity<Void>(headers);
		ResponseEntity<ErrorMessage> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, ErrorMessage.class, 15);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(1, response.getBody().getErrors().size());
	}
	
	@Test
	public void testCreateDepartment() {
		String url = "/departments";
		Department department = new Department("New Dept", new BigDecimal(100.0), new BigDecimal(400.0));
		URI uri = restTemplate.postForLocation(url, department, 1);
		assertNotNull(uri);
	}
	
	@Test
	public void testCreateInvalidFieldsDepartment() {
		String url = "/departments";
		Department department = new Department(null, null, null);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Department> entity = new HttpEntity<Department>(department, headers);
		ResponseEntity<ErrorMessage> response = restTemplate.exchange(url, HttpMethod.POST, entity, ErrorMessage.class, 100);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(3, response.getBody().getErrors().size());
	}
	
	@Test
	public void testCreateDepartmentMinSalaryGreaterThanMaxSalary() {
		String url = "/departments";
		Department department = new Department("New Dept", new BigDecimal(150.0), new BigDecimal(50.0));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Department> entity = new HttpEntity<Department>(department, headers);
		ResponseEntity<ErrorMessage> response = restTemplate.exchange(url, HttpMethod.POST, entity, ErrorMessage.class, 100);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(1, response.getBody().getErrors().size());
	}
	
	@Test
	public void testGetEmployeeAndDepartmentByIds() {
		String url = "/departments/{departmentId}/employees";
		Employee employee = new Employee("New Emp", "MGR 10", new BigDecimal(120.0));
		URI uri = restTemplate.postForLocation(url, employee, 1);
		Employee employeeResult = restTemplate.getForObject(uri, Employee.class);
		assertEquals("New Emp", employeeResult.getName());
	}
	
	
	
	



}