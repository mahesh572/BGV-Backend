package com.org.bgv.company.dto;

import java.util.List;

import com.org.bgv.common.FilterRequest;
import com.org.bgv.common.PaginationRequest;
import com.org.bgv.common.SortingRequest;

import lombok.Data;

@Data
public class EmployeeSearchRequest {

	/**
	 * Company context (MANDATORY for employee search)
	 */
	private Long companyId;

	/**
	 * Free text search (name, email, phone, employeeCode, user email)
	 */
	private String search;

	/**
	 * Pagination
	 */

	private PaginationRequest pagination;

	/**
	 * Dynamic filters status, department, employmentType, etc.
	 */
	private List<FilterRequest> filters;

	/**
	 * Pagination
	 *//*
		 * private Integer page = 0; private Integer size = 10;
		 */

	/**
	 * Sorting
	 */
	 private SortingRequest sorting;
	
}
