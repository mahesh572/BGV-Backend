createAction()

 → save action
 → attach evidence

 → if DOCUMENT:
       updateDocumentStatus ✅
       recalcCheckStatus ✅
            → if ACTION_REQUIRED (new) → notify + sync ✅

 → if SECTION:
       only notification logic ✅



@Transactional
public Long createAction(VerificationActionRequest req) {

    VerificationAction action = buildBaseAction(req);
    action = verificationActionRepository.save(action);

    linkEvidences(req, action);

    boolean documentChanged = false;

    switch (req.getActionLevel()) {

        case DOCUMENT -> {
            updateDocumentStatus(req, action);
            documentChanged = true; // ✅ important
        }

        case SECTION -> {
            handleSectionAction(req, action); // only side effects
        }
    }

    // ✅ ONLY recalc when document changed
    if (documentChanged) {
        recalculateAndUpdateCheckStatus(req.getCheckId(), action);
    }

    return action.getId();
}
private void handleSectionAction(
        VerificationActionRequest req,
        VerificationAction action
) {

    if (req.getActionType() == ActionType.REQUEST_INFO
            || req.getActionType() == ActionType.INSUFFICIENT) {

        try {
            Candidate candidate = candidateRepository
                    .findById(action.getCandidateId())
                    .orElseThrow();

            User user = userRepository
                    .findById(candidate.getUser().getUserId())
                    .orElseThrow();

            VerificationCaseCheck check =
                    verificationCaseCheckRepository.getReferenceById(req.getCheckId());

            Company company = companyRepository
                    .findById(check.getVerificationCase().getCompanyId())
                    .orElseThrow();

            int insufficientCount = (int) documentRepository
                    .findByVerificationCaseCheck_CaseCheckId(check.getCaseCheckId())
                    .stream()
                    .filter(doc ->
                            doc.getStatus() == DocumentStatus.INSUFFICIENT
                                    || doc.getStatus() == DocumentStatus.REQUEST_INFO)
                    .count();

            notificationDispatcher.dispatchVerificationCheckActionRequired(
                    company,
                    candidate,
                    user,
                    check.getCategory().getName(),
                    check.getVerificationCase().getCaseId(),
                    check.getCaseCheckId(),
                    insufficientCount,
                    action.getRemarks()
            );

        } catch (Exception e) {
            log.error("Error sending section action notification", e);
        }
    }
}
``private void updateDocumentStatus(
        VerificationActionRequest req,
        VerificationAction action
) {

    if (req.getDocumentId() == null) {
        throw new IllegalArgumentException(
                "DocumentId is required for DOCUMENT level action"
        );
    }

    Document document = documentRepository.findById(req.getDocumentId())
            .orElseThrow(() -> new EntityNotFoundException(
                    "Document not found: " + req.getDocumentId()));

    DocumentStatus newStatus = resolveDocumentStatus(
            document.getStatus(), // ✅ pass current state (future state machine ready)
            req.getActionType()
    );

    document.setStatus(newStatus);
    document.setLastAction(action);
    document.setUpdatedAt(LocalDateTime.now());
}
private DocumentStatus resolveDocumentStatus(
        DocumentStatus current,
        ActionType actionType
) {

    return switch (current) {

        case PENDING -> switch (actionType) {
            case REQUEST_INFO -> DocumentStatus.REQUEST_INFO;
            case INSUFFICIENT -> DocumentStatus.INSUFFICIENT;
            case VERIFY, APPROVE -> DocumentStatus.VERIFIED;
            case REJECT -> DocumentStatus.REJECTED;
            default -> throw invalid(actionType, current);
        };

        case REQUEST_INFO, INSUFFICIENT -> switch (actionType) {
            case VERIFY -> DocumentStatus.VERIFIED;
            case REJECT -> DocumentStatus.REJECTED;
            default -> throw invalid(actionType, current);
        };

        case VERIFIED, REJECTED ->
                throw new IllegalStateException("Final state reached: " + current);
    };
}

private RuntimeException invalid(ActionType action, DocumentStatus state) {
    return new IllegalStateException(
            "Invalid action " + action + " for state " + state
    );
}private void recalculateAndUpdateCheckStatus(
        Long checkId,
        VerificationAction action
) {

    VerificationCaseCheck check =
            verificationCaseCheckRepository.getReferenceById(checkId);

    CaseCheckStatus oldStatus = check.getStatus();

    CaseCheckStatus newStatus =
            caseCheckStatusService.recalculateCheckStatus(checkId);

    check.setStatus(newStatus);
    check.setLastAction(action);
    check.setUpdatedAt(LocalDateTime.now());

    verificationCaseCheckRepository.save(check);

    // ✅ trigger ONLY when status changes to ACTION_REQUIRED
    if (!CaseCheckStatus.ACTION_REQUIRED.equals(oldStatus)
            && CaseCheckStatus.ACTION_REQUIRED.equals(newStatus)) {

        triggerActionRequiredSync(check, action);
    }
}



private void triggerActionRequiredSync(
        VerificationCaseCheck check,
        VerificationAction action
) {
    try {
        checkSyncService.markSectionActionRequired(
                action.getCandidateId(),
                check.getVerificationCase().getCaseId(),
                check.getCategory().getName()
        );
    } catch (JsonProcessingException e) {
        log.error("Error syncing ACTION_REQUIRED state", e);
    }
}


