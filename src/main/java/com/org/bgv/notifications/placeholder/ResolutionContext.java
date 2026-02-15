package com.org.bgv.notifications.placeholder;

import java.util.Map;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.company.entity.CompanyEmailSettings;
import com.org.bgv.company.entity.Employee;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.PlatformConfig;
import com.org.bgv.entity.PlatformEmailSettings;
import com.org.bgv.notifications.dto.NotificationPlaceholder;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class ResolutionContext {
	
	 private NotificationPlaceholder currentPlaceholder;

    private Company company;
    private CompanyEmailSettings companyEmailSettings;

    private PlatformConfig platformConfig;
    private PlatformEmailSettings platformEmailSettings;

    private Employee employee;
    private Candidate candidate;

    private Map<String, Object> runtimeValues; // caseId, links, passwords
}

