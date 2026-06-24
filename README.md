
	
	private void addSendNotificationButton(
	        List<Button> buttons,
	        VerificationCaseCheck check,
	        Long vendorId) {

	    boolean isVendorAgent = userServiceUtil.hasRole(
	            vendorId,
	            RoleConstants.ROLE_VENDOR_AGENT
	    );

	    boolean isActionRequired =
	            CaseCheckStatus.ACTION_REQUIRED.name()
	                    .equalsIgnoreCase(check.getStatus().name());

	    if (isVendorAgent && isActionRequired) {
	        buttons.add(Button.builder()
	                .code("SEND_NOTIFICATION")
	                .label("Notify")
	                .visible(true)
	                .enabled(true)
	                .style(ButtonStyle.OUTLINED)
	                .color(ButtonColor.PRIMARY)
	                .icon("NotificationsActive")
	                .action(ButtonActionTypes.SEND_NOTIFICATION)
	                .tooltip("Notify candidate and employer about missing documents/information")
	                .build());
	    }
	}
