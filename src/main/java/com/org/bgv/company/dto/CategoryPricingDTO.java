package com.org.bgv.company.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;



@Data
public class CategoryPricingDTO {
 private String categoryName;
 private int baseCount;
 private List<AddonItemDTO> addonItems = new ArrayList();
 private BigDecimal categoryTotal = BigDecimal.ZERO;
 
 public CategoryPricingDTO(String categoryName) {
     this.categoryName = categoryName;
 }
 
 public void addBaseItem(Long selectionId) {
     this.baseCount++;
 }
 
 public void addAddonItem(Long selectionId, BigDecimal price) {
     this.addonItems.add(new AddonItemDTO(selectionId, price));
     this.categoryTotal = this.categoryTotal.add(price);
 }
}

