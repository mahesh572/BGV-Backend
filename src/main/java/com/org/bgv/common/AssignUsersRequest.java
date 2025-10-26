package com.org.bgv.common;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssignUsersRequest {
	 private List<Long> userIds;
}
