package com.org.bgv.vendor.verification.methods.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.notifications.service.NotificationDispatcher;
import com.org.bgv.vendor.dto.EmploymentVerificationData;
import com.org.bgv.vendor.entity.VerificationMethodExecution;
import com.org.bgv.vendor.entity.VerificationMethodExecutionField;
import com.org.bgv.vendor.repository.VerificationMethodExecutionFieldRepository;
import com.org.bgv.vendor.repository.VerificationMethodExecutionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailToHrHandler implements VerificationMethodHandler {

    private final VerificationMethodExecutionRepository executionRepository;
    private final VerificationMethodExecutionFieldRepository fieldRepository;
    private final NotificationDispatcher notificationDispatcher;

    @Override
    public void execute(
            VerificationMethodExecution execution,
            VerificationContext context) {

        log.info(
                "📨 Starting EmailToHrHandler | executionId={} | checkId={}",
                execution.getExecutionId(),
                execution.getVerificationCheck().getCaseCheckId()
        );

        String hrEmail = getFieldValue(execution, "email");
        String hrName = getFieldValue(execution, "contactName");
        String hrCompanyName = getFieldValue(execution, "companyName");

        log.info(
                "👤 HR Details | email={} | name={} | company={}",
                hrEmail,
                hrName,
                hrCompanyName
        );

        EmploymentVerificationData employmentData =
                (EmploymentVerificationData) context.getVerificationData();

        String employmentPeriod = employmentData.getEmploymentPeriod();
        String department = employmentData.getDepartment();
        String designation = employmentData.getDesignation();
        String employeeId = employmentData.getEmployeeId();

        log.info(
                "💼 Employment Details | employeeId={} | designation={} | department={} | period={}",
                employeeId,
                designation,
                department,
                employmentPeriod
        );

        log.info(
                "👤 Candidate | candidateId={} | candidateName={}",
                context.getCandidate() != null
                        ? context.getCandidate().getCandidateId()
                        : null,
                context.getCandidate() != null
                        ? context.getCandidate().getFirstName()
                        : null
        );

        try {

            log.info(
                    "📤 Sending employment verification request email to HR | executionId={} | hrEmail={}",
                    execution.getExecutionId(),
                    hrEmail
            );

            notificationDispatcher
                    .dispatchEmploymentVerificationEmailRequested(
                            
                            context.getCandidate(),
                            hrEmail,
                            hrName,
                            employeeId,
                            designation,
                            department,
                            employmentPeriod,
                            null,
                            null,
                            null,
                            null
                    );

            log.info(
                    "✅ Employment verification email sent successfully | executionId={} | hrEmail={}",
                    execution.getExecutionId(),
                    hrEmail
            );

            execution.setStatus(
                    VerificationExecutionStatus.WAITING_FOR_RESPONSE);

            executionRepository.save(execution);

            log.info(
                    "✅ Execution status updated to WAITING_FOR_RESPONSE | executionId={}",
                    execution.getExecutionId()
            );

        } catch (Exception ex) {

            log.error(
                    "❌ Failed to send employment verification email | executionId={} | hrEmail={}",
                    execution.getExecutionId(),
                    hrEmail,
                    ex
            );

            throw ex;
        }
    }

    private String buildEmailBody(
            VerificationMethodExecution execution) {

        return "Employment verification request...";
    }

    private String getFieldValue(
            VerificationMethodExecution execution,
            String fieldName) {
    	
    	List<VerificationMethodExecutionField> fields =
    	        fieldRepository.findByExecutionExecutionId(
    	                execution.getExecutionId());

    	log.info(
    	        "Found {} fields for executionId={}",
    	        fields.size(),
    	        execution.getExecutionId());

    	fields.forEach(f -> log.info(
    	        "Field => id={} name='{}' value='{}'",
    	        f.getId(),
    	        f.getFieldName(),
    	        f.getFieldValue()));

        String value = fieldRepository
                .findByExecutionExecutionIdAndFieldName(
                        execution.getExecutionId(),
                        fieldName)
                .map(VerificationMethodExecutionField::getFieldValue)
                .orElse(null);

        log.info(
                "🔍 Field value resolved | executionId={} | field={} | value={}",
                execution.getExecutionId(),
                fieldName,
                value
        );

        return value;
    }
}