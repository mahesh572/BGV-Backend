package com.org.bgv.ui;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
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
