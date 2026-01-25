package com.org.bgv.company.service;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.auth.service.ResetTokenService;
import com.org.bgv.common.CommonUtils;
import com.org.bgv.company.dto.CreateEmployeeRequest;
import com.org.bgv.company.dto.UpdateEmployeeRequest;
import com.org.bgv.company.entity.Employee;
import com.org.bgv.company.repository.EmployeeRepository;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserType;
import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.notifications.dto.NotificationPlaceholder;
import com.org.bgv.notifications.service.NotificationDispatcherService;
import com.org.bgv.notifications.service.NotificationPolicyService;
import com.org.bgv.notifications.service.SupportEmailResolver;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.service.EmailService;

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

   
    @Transactional
    public void createEmployee(Long companyId, CreateEmployeeRequest request) {

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

        dispatchEmployeeCreatedNotification(company, employee, user, tempPassword);
    }
   
    private void dispatchEmployeeCreatedNotification(
            Company company,
            Employee employee,
            User user,
            String tempPassword
    ) {

        String resetLink = resetTokenService.generateResetLink(user.getUserId());

        NotificationContext context = NotificationContext.builder()
                .event(NotificationEvent.EMPLOYEE_ACCOUNT_CREATED)
                .companyId(company.getId())

                // Recipient
               // .userId(user.getUserId())
                .userEmailAddress(user.getEmail())

                // Business placeholders only
                .variables(Map.of(
                        NotificationPlaceholder.EMPLOYEE_NAME.key(),
                        employee.getFirstName() + " " + employee.getLastName(),

                        NotificationPlaceholder.EMPLOYEE_EMAIL.key(),
                        user.getEmail(),

                        NotificationPlaceholder.RESET_PASSWORD_LINK.key(),
                        resetLink,

                        NotificationPlaceholder.LINK_EXPIRY_DURATION.key(),
                        "24 hours",
                        NotificationPlaceholder.SUPPORT_EMAIL.key(),supportEmailResolver.resolve(company.getId()),
                        NotificationPlaceholder.TEMPORARY_PASSWORD.key(),tempPassword
                ))
                .build();

        dispatcher.dispatch(NotificationEvent.EMPLOYEE_ACCOUNT_CREATED, context);
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
}
