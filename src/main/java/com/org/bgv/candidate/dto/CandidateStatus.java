package com.org.bgv.candidate.dto;

public enum CandidateStatus {
    DRAFT,        // saved, no email
    CREATED,     // employer has created candidate
    INVITED,      // invite sent
    REGISTERED,   // user accepted invite
    CONSENTED,    // consent given
    IN_PROGRESS,  // checks running
    COMPLETED
}

