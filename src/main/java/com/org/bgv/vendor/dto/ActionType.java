package com.org.bgv.vendor.dto;

public enum ActionType {
	REQUEST_INFO,
	INSUFFICIENT,   // fixable → candidate action needed
    REJECT,         // blocked → resubmission needed
    FAIL,           // terminal → audit required
    VERIFY,
    VIEW,
    DOWNLOAD,
    APPROVE,
    REVERIFY,
    ESCALATE
}