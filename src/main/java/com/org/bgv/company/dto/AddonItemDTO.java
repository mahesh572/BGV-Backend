package com.org.bgv.company.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AddonItemDTO {
 private Long selectionId;
 private BigDecimal price;
}