private VerificationCheckResponseDTO buildVerificationCheckResponse(VerificationCaseCheck check,
			VerificationCase verificationCase, Candidate candidate) {
		
		List<Button> buttons = new ArrayList<>();
		
		 addSendNotificationButton(buttons, check, check.getAssignedVendorUser().getUserId());

		return VerificationCheckResponseDTO.builder()
				.caseId(String.valueOf(verificationCase.getCaseId()))
				.caseRef(getCaseReference(verificationCase))
				.checkId(String.valueOf(check.getCaseCheckId()))
				.checkRef(check.getCheckRef())
				//.checkType(check.getCategory().getName())
				.checkType(CheckCategoryEnum.fromName(check.getCategory().getName()))
				.checkName(check.getCategory().getName())
				.status(check.getStatus().name())
				.candidate(mapCandidateInfo(candidate))
				// .sendNotification(check.getStatus().name().equalsIgnoreCase(CaseCheckStatus.ACTION_REQUIRED.name()))
				.buttons(buttons)
				// .audit(buildAudit(check))
				 .actions(resolveCheckActions(check.getStatus())) 
				 
				.build();
	}
	
	
	package com.org.bgv.ui;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Button {

    private String code;
    private String label;

    private boolean visible;
    private boolean enabled;

    private ButtonStyle style;
    private ButtonColor color;

    private String icon;

    private ButtonActionTypes action;
    
    private String tooltip; 
}


package com.org.bgv.ui;

public enum ButtonStyle {
    CONTAINED,
    OUTLINED,
    TEXT
}

package com.org.bgv.ui;

public enum ButtonColor {
    PRIMARY,
    SUCCESS,
    ERROR,
    WARNING,
    INFO
}


package com.org.bgv.ui;

public enum ButtonActionTypes {
	SEND_NOTIFICATION
}
