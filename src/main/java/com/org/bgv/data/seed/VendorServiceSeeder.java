package com.org.bgv.data.seed;


import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.User;
import com.org.bgv.entity.VendorCheckMapping;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.VendorCheckMappingRepository;
import com.org.bgv.vendor.entity.VendorUserCategoryMapping;
import com.org.bgv.vendor.repository.VendorUserCategoryMappingRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VendorServiceSeeder implements CommandLineRunner {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CheckCategoryRepository checkCategoryRepository;
    private final VendorCheckMappingRepository vendorCheckMappingRepository;
    private final VendorUserCategoryMappingRepository vendorUserCategoryMappingRepository;

    @Override
    public void run(String... args) throws Exception {

        Long companyId = 6L;
        Long vendorUserId = 26L;

        Company company = companyRepository.findById(companyId)
                .orElseThrow();

        User vendorUser = userRepository.findById(vendorUserId)
                .orElseThrow();

        List<CheckCategory> categories =
                checkCategoryRepository.findAll();

        for (CheckCategory category : categories) {

            // Company → Category mapping
            boolean companyMappingExists =
                    vendorCheckMappingRepository
                            .existsByVendorCompanyAndCategoryAndIsActiveTrue(
                                    company,
                                    category);

            if (!companyMappingExists) {

                VendorCheckMapping companyMapping =
                        VendorCheckMapping.builder()
                                .vendorCompany(company)
                                .category(category)
                                .isActive(true)
                                .build();

                vendorCheckMappingRepository.save(companyMapping);
            }

            // Vendor User → Category mapping
            boolean userMappingExists =
                    vendorUserCategoryMappingRepository
                            .existsByVendorUserAndCategory(
                                    vendorUser,
                                    category);

            if (!userMappingExists) {

                VendorUserCategoryMapping userMapping =
                        VendorUserCategoryMapping.builder()
                                .vendorUser(vendorUser)
                                .category(category)
                                .active(true)
                                .build();

                vendorUserCategoryMappingRepository
                        .save(userMapping);
            }
        }

        System.out.println("Vendor mappings seeded successfully.");
    }
}