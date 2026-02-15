package com.org.bgv.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActionMetadata {

    private String action;       // view | edit | delete
    private String label;        // View Details
    private String icon;         // eye | edit | trash
  //  private String api;          // backend endpoint
  //  private String method;       // GET | PUT | DELETE
    private Boolean requiresConfirm;
}

