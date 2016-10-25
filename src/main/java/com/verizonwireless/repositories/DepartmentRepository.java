package com.verizonwireless.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.verizonwireless.domain.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

	

}

