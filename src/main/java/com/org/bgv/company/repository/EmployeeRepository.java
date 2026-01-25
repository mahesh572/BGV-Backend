package com.org.bgv.company.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.company.entity.Employee;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Find employee by user + company (most common lookup)
    Optional<Employee> findByUserUserIdAndCompanyId(
            Long userId,
            Long companyId
    );

    // Check if employee already exists in company
    boolean existsByUserUserIdAndCompanyId(
            Long userId,
            Long companyId
    );

    // List all employees for a company
    List<Employee> findAllByCompanyId(Long companyId);

    // List active employees for a company
    List<Employee> findAllByCompanyIdAndStatus(
            Long companyId,
            String status
    );

    // Find employee by company + email
    Optional<Employee> findByCompanyIdAndEmailAddress(
            Long companyId,
            String emailAddress
    );

    // Search by employee code inside company
    Optional<Employee> findByCompanyIdAndEmployeeCode(
            Long companyId,
            String employeeCode
    );
}
