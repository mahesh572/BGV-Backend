package com.org.bgv.vendor.entity;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "vendor_user_category_mapping",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {
                "vendor_user_id",
                "category_id"
            }
        )
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorUserCategoryMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_user_id")
    private User vendorUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CheckCategory category;

    private Boolean active = true;
}
