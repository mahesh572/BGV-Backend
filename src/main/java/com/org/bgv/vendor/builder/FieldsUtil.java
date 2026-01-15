package com.org.bgv.vendor.builder;

import com.org.bgv.vendor.dto.ObjectFieldDTO;

public class FieldsUtil {

	
	public static ObjectFieldDTO text(String code, String label, Object value, boolean mandatory) {
	    return ObjectFieldDTO.builder()
	            .code(code)
	            .label(label)
	            .type("TEXT")
	            .value(value)
	            .mandatory(mandatory)
	            .readOnly(false)
	            .build();
	}

	public static ObjectFieldDTO date(String code, String label, Object value, boolean mandatory) {
	    return ObjectFieldDTO.builder()
	            .code(code)
	            .label(label)
	            .type("DATE")
	            .value(value)
	            .mandatory(mandatory)
	            .readOnly(false)
	            .build();
	}

	public static ObjectFieldDTO bool(String code, String label, Object value, boolean mandatory) {
	    return ObjectFieldDTO.builder()
	            .code(code)
	            .label(label)
	            .type("BOOLEAN")
	            .value(value)
	            .mandatory(mandatory)
	            .readOnly(false)
	            .build();
	}
	
	public static ObjectFieldDTO number(
	        String code,
	        String label,
	        Object value,
	        boolean mandatory,
	        boolean readOnly
	) {
	    return ObjectFieldDTO.builder()
	            .code(code)
	            .label(label)
	            .type("NUMBER")
	            .value(value)
	            .mandatory(mandatory)
	            .readOnly(readOnly)
	            .build();
	}
	
}
