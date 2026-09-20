package com.org.bgv.vendor.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.enums.ComparisonStatus;
import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.enums.VerificationObjectStatus;
import com.org.bgv.enums.VerificationOutcome;
import com.org.bgv.vendor.entity.VerificationFieldComparison;
import com.org.bgv.vendor.entity.VerificationMethodExecution;
import com.org.bgv.vendor.entity.VerificationObject;

@Component
public class VerificationStatusCalculator {

    /* ===========================================================
     * OBJECT
     * ===========================================================
     */

    public void calculateObjectStatus(
            VerificationObject object,
            List<VerificationMethodExecution> executions,
            List<VerificationFieldComparison> comparisons) {

        VerificationObjectStatus status = getObjectWorkflowStatus(executions);

        object.setStatus(status);

        if (status != VerificationObjectStatus.COMPLETED) {
            object.setOutcome(null);
            return;
        }

        object.setOutcome(getObjectOutcome(executions, comparisons));
    }

    /* ===========================================================
     * CHECK
     * ===========================================================
     */

    public void calculateCheckStatus(
            VerificationCaseCheck check,
            List<VerificationObject> objects) {

        boolean allCompleted = objects.stream()
                .allMatch(o -> o.getStatus() == VerificationObjectStatus.COMPLETED);

        check.setStatus(allCompleted
                ? CaseCheckStatus.COMPLETED
                : CaseCheckStatus.IN_PROGRESS);

        if (!allCompleted) {
            check.setOutcome(null);
            return;
        }

        check.setOutcome(calculateOutcome(
                objects.stream()
                        .map(VerificationObject::getOutcome)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        ));
    }

    /* ===========================================================
     * CASE
     * ===========================================================
     */

    public void calculateCaseStatus(
            VerificationCase verificationCase,
            List<VerificationCaseCheck> checks) {

        boolean allCompleted = checks.stream()
                .allMatch(c -> c.getStatus() == CaseCheckStatus.COMPLETED);

        verificationCase.setStatus(allCompleted
                ? CaseStatus.COMPLETED
                : CaseStatus.IN_PROGRESS);

        if (!allCompleted) {
            verificationCase.setOutcome(null);
            return;
        }

        verificationCase.setOutcome(calculateOutcome(
                checks.stream()
                        .map(VerificationCaseCheck::getOutcome)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        ));
    }

    /* ===========================================================
     * OBJECT WORKFLOW STATUS
     * ===========================================================
     */

    private VerificationObjectStatus getObjectWorkflowStatus(
            List<VerificationMethodExecution> executions) {

        if (executions == null || executions.isEmpty()) {
            return VerificationObjectStatus.PENDING;
        }

        boolean allDraft = executions.stream()
                .allMatch(e -> e.getStatus() == VerificationExecutionStatus.DRAFT);

        if (allDraft) {
            return VerificationObjectStatus.PENDING;
        }

        boolean allCompleted = executions.stream()
                .allMatch(e -> e.getStatus() == VerificationExecutionStatus.COMPLETED);

        if (allCompleted) {
            return VerificationObjectStatus.COMPLETED;
        }

        return VerificationObjectStatus.IN_PROGRESS;
    }

    /* ===========================================================
     * OBJECT OUTCOME
     * ===========================================================
     */

    private VerificationOutcome getObjectOutcome(
            List<VerificationMethodExecution> executions,
            List<VerificationFieldComparison> comparisons) {

        if (executions == null || executions.isEmpty()) {
            return null;
        }

        if (executions.stream()
                .anyMatch(e -> e.getOutcome() == VerificationOutcome.DISCREPANCY)) {

            return VerificationOutcome.DISCREPANCY;
        }

        if (comparisons != null &&
                comparisons.stream()
                        .anyMatch(c -> c.getResult() == ComparisonStatus.MISMATCH)) {

            return VerificationOutcome.DISCREPANCY;
        }

        boolean allVerified = executions.stream()
                .allMatch(e -> e.getOutcome() == VerificationOutcome.VERIFIED);

        boolean allMatched = comparisons == null ||
                comparisons.isEmpty() ||
                comparisons.stream()
                        .allMatch(c -> c.getResult() == ComparisonStatus.MATCH);

        if (allVerified && allMatched) {
            return VerificationOutcome.VERIFIED;
        }

        boolean allUnable = executions.stream()
                .allMatch(e -> e.getOutcome() == VerificationOutcome.UNABLE_TO_VERIFY);

        if (allUnable) {
            return VerificationOutcome.UNABLE_TO_VERIFY;
        }

        boolean allNoResponse = executions.stream()
                .allMatch(e -> e.getOutcome() == VerificationOutcome.NO_RESPONSE);

        if (allNoResponse) {
            return VerificationOutcome.NO_RESPONSE;
        }

        return VerificationOutcome.PARTIALLY_VERIFIED;
    }

    /* ===========================================================
     * COMMON OUTCOME CALCULATION
     * ===========================================================
     */

    private VerificationOutcome calculateOutcome(
            List<VerificationOutcome> outcomes) {

        if (outcomes == null || outcomes.isEmpty()) {
            return null;
        }

        if (outcomes.stream()
                .anyMatch(o -> o == VerificationOutcome.DISCREPANCY)) {

            return VerificationOutcome.DISCREPANCY;
        }

        if (outcomes.stream()
                .allMatch(o -> o == VerificationOutcome.VERIFIED)) {

            return VerificationOutcome.VERIFIED;
        }

        if (outcomes.stream()
                .allMatch(o -> o == VerificationOutcome.UNABLE_TO_VERIFY)) {

            return VerificationOutcome.UNABLE_TO_VERIFY;
        }

        if (outcomes.stream()
                .allMatch(o -> o == VerificationOutcome.NO_RESPONSE)) {

            return VerificationOutcome.NO_RESPONSE;
        }

        return VerificationOutcome.PARTIALLY_VERIFIED;
    }

}