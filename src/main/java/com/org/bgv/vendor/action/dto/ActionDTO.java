package com.org.bgv.vendor.action.dto;


import com.org.bgv.vendor.dto.ActionLevel;
import com.org.bgv.vendor.dto.ActionType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionDTO {

    private ActionType code;      // REQUEST_INFO, VERIFY, REJECT
    private String label;         // "Request Info", "Verify"
    private ActionLevel level;    // CHECK / OBJECT / DOCUMENT / FILE

    /* ---------- UI / Behavior ---------- */
    private boolean requiresReason;
    private boolean requiresRemarks;
    private boolean requiresEvidence;

    /* ---------- Optional ---------- */
   // private boolean destructive;   // true for REJECT / FAIL
    private boolean enabled;       // backend can disable dynamically
}

