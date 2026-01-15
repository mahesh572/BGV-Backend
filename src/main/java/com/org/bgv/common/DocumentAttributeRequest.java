package com.org.bgv.common;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentAttributeRequest {

	// -----------------------------
    // Attribute Master (optional)
    // -----------------------------

    private Long attributeId;   // If present → use existing attribute

    private String code;        // FRONT, BACK (required if attributeId is null)
    private String label;       // Front Page
    private String type;        // SIDE / PAGE
    private Boolean active;     // default true

    // -----------------------------
    // DocumentType-specific rules
    // -----------------------------

    private Boolean mandatory;  // true / false
    private Integer maxFiles;   // e.g. 1
    private Integer sequence;   // display order
}
