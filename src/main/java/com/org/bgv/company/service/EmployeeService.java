package com.org.bgv.company.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.org.bgv.auth.service.ResetTokenService;
import com.org.bgv.common.ColumnMetadata;
import com.org.bgv.common.CommonUtils;
import com.org.bgv.common.FilterMetadata;
import com.org.bgv.common.FilterRequest;
import com.org.bgv.common.Option;
import com.org.bgv.common.PaginationMetadata;
import com.org.bgv.common.PaginationRequest;
import com.org.bgv.common.PaginationResponse;
import com.org.bgv.common.SortField;
import com.org.bgv.common.SortingMetadata;
import com.org.bgv.common.SortingRequest;
import com.org.bgv.common.UserDto;
import com.org.bgv.common.UserSearchRequest;
import com.org.bgv.company.dto.CreateEmployeeRequest;
import com.org.bgv.company.dto.EmployeeDTO;
import com.org.bgv.company.dto.EmployeeSearchRequest;
import com.org.bgv.company.dto.UpdateEmployeeRequest;
import com.org.bgv.company.entity.Employee;
import com.org.bgv.company.repository.EmployeeRepository;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserType;
import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.notifications.dto.NotificationPlaceholder;
import com.org.bgv.notifications.service.NotificationDispatcher;
import com.org.bgv.notifications.service.NotificationDispatcherService;
import com.org.bgv.notifications.service.NotificationPolicyService;
import com.org.bgv.notifications.service.SupportEmailResolver;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.service.EmailService;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
   // private final NotificationPolicyService notificationService;
    private final NotificationDispatcherService dispatcher;
    private final ResetTokenService resetTokenService;
    private final SupportEmailResolver supportEmailResolver;
    private final NotificationDispatcher notificationDispatcher;

   
    @Transactional
    public void createEmployee(Long companyId, CreateEmployeeRequest request) {

    	try {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        String tempPassword = CommonUtils.generateTempPassword();

        User user = userRepository.findByEmail(request.getEmailAddress())
                .orElseGet(() -> createNewUser(request, tempPassword));

        if (employeeRepository.existsByUserUserIdAndCompanyId(user.getUserId(), companyId)) {
            throw new IllegalStateException("Employee already exists for this company");
        }

        Employee employee = employeeRepository.save(
                Employee.builder()
                        .user(user)
                        .company(company)
                        .employeeCode(request.getEmployeeCode())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .emailAddress(request.getEmailAddress())
                        .phoneNumber(request.getPhoneNumber())
                        .designation(request.getDesignation())
                        .department(request.getDepartment())
                        .employmentType(request.getEmploymentType())
                        .status("ACTIVE")
                        .build()
        );

        notificationDispatcher.dispatchEmployeeCreatedNotification(company, employee, user, tempPassword);
    	}catch (Exception e) {
			e.printStackTrace();
		}
    }
   


    
    private User createNewUser(CreateEmployeeRequest request,String tempPassword) {
    	
    	

        User user = User.builder()
                .email(request.getEmailAddress())
                .userType(UserType.COMPANY.name()) // or COMPANY_USER
                .password(passwordEncoder.encode(tempPassword))
                .isActive(true)
                .isVerified(false)
                .passwordResetrequired(true) // force reset on first login
                .status("ACTIVE")
                .build();

        return userRepository.save(user);
    }

   
    public Employee updateEmployee(Long companyId, Long employeeId, UpdateEmployeeRequest request) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        // 🔒 Company boundary enforcement
        if (!employee.getCompany().getId().equals(companyId)) {
            throw new SecurityException("Unauthorized employee update attempt");
        }

        if (request.getFirstName() != null) employee.setFirstName(request.getFirstName());
        if (request.getLastName() != null) employee.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) employee.setPhoneNumber(request.getPhoneNumber());
        if (request.getDesignation() != null) employee.setDesignation(request.getDesignation());
        if (request.getDepartment() != null) employee.setDepartment(request.getDepartment());
        if (request.getEmploymentType() != null) employee.setEmploymentType(request.getEmploymentType());
        if (request.getStatus() != null) employee.setStatus(request.getStatus());

        return employeeRepository.save(employee);
    }

    
    public void deactivateEmployee(Long companyId, Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        if (!employee.getCompany().getId().equals(companyId)) {
            throw new SecurityException("Unauthorized employee removal attempt");
        }

        // ✅ Soft delete (recommended)
        employee.setStatus("INACTIVE");
        employeeRepository.save(employee);
    }
    
    public PaginationResponse<EmployeeDTO> searchEmployees(EmployeeSearchRequest searchRequest) {
        try {
            Pageable pageable = createPageable(searchRequest.getPagination(), searchRequest.getSorting());

            Specification<Employee> spec = buildEmployeeSearchSpecification(searchRequest);
            
            // Log the specification for debugging
            System.out.println("Executing search with spec: " + spec);
            
            Page<Employee> employeePage = employeeRepository.findAll(spec, pageable);

            return buildEmployeePaginationResponse(employeePage, searchRequest);
        } catch (Exception e) {
            // Log the full exception
            e.printStackTrace();
            throw new RuntimeException("Failed to search employees: " + e.getMessage(), e);
        }
    }
   
    private Specification<Employee> buildEmployeeSearchSpecification(
            EmployeeSearchRequest searchRequest) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            /* 🔒 Company is mandatory */
            predicates.add(
                cb.equal(root.get("company").get("id"),
                         searchRequest.getCompanyId())
            );

            /* 🔍 Global search */
            if (StringUtils.hasText(searchRequest.getSearch())) {

                String term = "%" + searchRequest.getSearch().toLowerCase() + "%";

                Predicate firstName =
                    cb.like(cb.lower(root.get("firstName")), term);

                Predicate lastName =
                    cb.like(cb.lower(root.get("lastName")), term);

                Predicate email =
                    cb.like(cb.lower(root.get("emailAddress")), term);

                Predicate phone =
                    cb.like(cb.lower(root.get("phoneNumber")), term);
               

                Predicate employeeCode =
                    cb.like(cb.lower(root.get("employeeCode")), term);

                predicates.add(
                    cb.or(firstName, lastName, email, phone,employeeCode)
                );
            }

            /* 🎛 Dynamic filters */
            if (searchRequest.getFilters() != null) {
                for (FilterRequest filter : searchRequest.getFilters()) {

                    if (Boolean.TRUE.equals(filter.getIsSelected())
                            && filter.getSelectedValue() != null) {

                        switch (filter.getField()) {

                            case "status":
                            case "department":
                            case "designation":
                            case "employmentType":
                                predicates.add(
                                    cb.equal(
                                        root.get(filter.getField()),
                                        filter.getSelectedValue()
                                    )
                                );
                                break;

                            case "gender":
                                predicates.add(
                                    cb.equal(root.get("gender"),
                                             filter.getSelectedValue())
                                );
                                break;

                            case "createdDate":
                                predicates.add(
                                    cb.equal(root.get("createdAt"),
                                             filter.getSelectedValue())
                                );
                                break;
                        }
                    }
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    private PaginationResponse<EmployeeDTO> buildEmployeePaginationResponse(
            Page<Employee> page,
            EmployeeSearchRequest request) {

        // Map Employee -> EmployeeDto
        List<EmployeeDTO> employeeDtos = page.getContent().stream()
                .map(this::mapToEmployeeDto)
                .toList();

        // Pagination metadata
        PaginationMetadata pagination = PaginationMetadata.builder()
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .allowedPageSizes(List.of(10, 25, 50))
                .build();

        // Sorting metadata
        SortingMetadata sorting = SortingMetadata.builder()
                .currentSort(SortField.builder()
                        .field(request.getSorting() != null ? request.getSorting().getSortBy() : "employeeId")
                        .displayName(getEmployeeDisplayNameForField(
                                request.getSorting() != null ? request.getSorting().getSortBy() : "employeeId"))
                        .direction(request.getSorting() != null ? request.getSorting().getSortDirection() : "asc")
                        .build())
                .sortableFields(getEmployeeSortableFields())
                .build();

        // Filters metadata
        List<FilterMetadata> filters = getEmployeeFilters(request);

        return PaginationResponse.<EmployeeDTO>builder()
                .content(employeeDtos)
                .pagination(pagination)
                .sorting(sorting)
                .filters(filters)
                .columns(getEmployeeColumnMetadata())
                .build();
    }

    private String getEmployeeDisplayNameForField(String field) {
        return switch (field) {
            case "employeeId" -> "Employee ID";
            case "firstName" -> "First Name";
            case "lastName" -> "Last Name";
            case "emailAddress" -> "Email";
            case "phoneNumber" -> "Phone Number";
            case "status" -> "Status";
            default -> field;
        };
    }
    private List<SortField> getEmployeeSortableFields() {
        return List.of(
            SortField.builder().field("employeeId").displayName("Employee ID").build(),
            SortField.builder().field("firstName").displayName("First Name").build(),
            SortField.builder().field("lastName").displayName("Last Name").build(),
            SortField.builder().field("emailAddress").displayName("Email").build(),
            SortField.builder().field("phoneNumber").displayName("Phone Number").build(),
            SortField.builder().field("status").displayName("Status").build()
        );
    }

    
    private Pageable createPageable(PaginationRequest pagination, SortingRequest sorting) {
        // ✅ Default pagination if null
    	// sorting.setSortBy("");
        int page = 0;
        int size = 10;

        if (pagination != null) {
            page = pagination.getPage() != null ? pagination.getPage() : 0;
            size = pagination.getSize() != null ? pagination.getSize() : 10;
        }

        // ✅ Default sorting if null or empty
        String sortBy = "employeeId";       // default sort field
        String sortDirection = "asc";       // default direction

        if (sorting != null) {
            if (sorting.getSortBy() != null && !sorting.getSortBy().isEmpty()) {
                sortBy = sorting.getSortBy();
            }
            if (sorting.getSortDirection() != null && !sorting.getSortDirection().isEmpty()) {
                sortDirection = sorting.getSortDirection();
            }
        }

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);

        return PageRequest.of(page, size, sort);
    }




    private List<FilterMetadata> getEmployeeFilters(EmployeeSearchRequest request) {
        List<FilterMetadata> filters = new ArrayList<>();

        // Status filter
        FilterMetadata statusFilter = FilterMetadata.builder()
                .field("status")
                .displayName("Status")
                .type("dropdown")
                .options(List.of(
                    Option.builder().label("Active").value("ACTIVE").build(),
                    Option.builder().label("Inactive").value("INACTIVE").build()
                ))
                .build();

        // Employment Type filter
        FilterMetadata typeFilter = FilterMetadata.builder()
                .field("employmentType")
                .displayName("Employment Type")
                .type("dropdown")
                .options(List.of(
                    Option.builder().label("Full Time").value("FULL_TIME").build(),
                    Option.builder().label("Contract").value("CONTRACT").build(),
                    Option.builder().label("Intern").value("INTERN").build()
                ))
                .build();

        // Set selected values if any
        if (request.getFilters() != null) {
            for (FilterRequest filter : request.getFilters()) {
                if (filter.getIsSelected() != null && filter.getIsSelected()) {
                    switch (filter.getField()) {
                        case "status" -> statusFilter.setSelectedValue(filter.getSelectedValue());
                        case "employmentType" -> typeFilter.setSelectedValue(filter.getSelectedValue());
                    }
                }
            }
        }

        filters.add(statusFilter);
        filters.add(typeFilter);

        return filters;
    }
    private EmployeeDTO mapToEmployeeDto(Employee employee) {
        return EmployeeDTO.builder()
                .employeeId(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .emailAddress(employee.getEmailAddress())
                .phoneNumber(employee.getPhoneNumber())
                .status(employee.getStatus())
                .designation(employee.getDesignation())
                .department(employee.getDepartment())
                .employmentType(employee.getEmploymentType())
                .dateOfBirth(employee.getDateOfBirth())
                .gender(employee.getGender())
                .nationality(employee.getNationality())
                .userId(employee.getUser().getUserId())
                .build();
    }
    private List<ColumnMetadata> getEmployeeColumnMetadata() {
        return List.of(
            ColumnMetadata.builder().field("employeeId").displayName("Employee ID").visible(false).build(),
            ColumnMetadata.builder().field("name").displayName("Name").visible(true).build(),
            ColumnMetadata.builder().field("emailAddress").displayName("Email").visible(true).build(),
            ColumnMetadata.builder().field("phoneNumber").displayName("Phone Number").visible(true).build(),
            ColumnMetadata.builder().field("designation").displayName("Designation").visible(true).build(),
            ColumnMetadata.builder().field("department").displayName("Department").visible(true).build(),
            ColumnMetadata.builder().field("employmentType").displayName("Employment Type").visible(true).build(),
            ColumnMetadata.builder().field("status").displayName("Status").visible(true).build(),
            ColumnMetadata.builder().field("gender").displayName("Gender").visible(true).build()
        );
    }


}
