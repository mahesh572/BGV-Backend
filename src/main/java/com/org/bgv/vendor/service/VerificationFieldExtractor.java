package com.org.bgv.vendor.service;

import java.util.Map;

import com.org.bgv.dto.CheckCategoryEnum;

public interface VerificationFieldExtractor {
	
	CheckCategoryEnum getType();

    Map<String,Object> extract(Long sourceId);
}
