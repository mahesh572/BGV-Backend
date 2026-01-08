package com.org.bgv.data.seed;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.Vendor;
import com.org.bgv.entity.VendorCheckMapping;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.VendorCheckMappingRepository;
import com.org.bgv.repository.VendorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class VendorCheckMappingSeeder implements CommandLineRunner {

    private final VendorRepository vendorRepository;
    private final CheckCategoryRepository checkCategoryRepository;
    private final VendorCheckMappingRepository vendorCheckMappingRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🌱 VendorCheckMapping seeding started...");

        List<Vendor> vendors = vendorRepository.findAll();
        List<CheckCategory> categories = checkCategoryRepository.findAll();

        if (vendors.isEmpty() || categories.isEmpty()) {
            log.warn("⚠️ Vendors or Categories missing. Skipping seeding.");
            return;
        }

        for (Vendor vendor : vendors) {
            for (CheckCategory category : categories) {

                boolean exists =
                    vendorCheckMappingRepository
                        .existsByVendorAndCategoryAndIsActiveTrue(
                            vendor,
                            category
                        );

                if (!exists) {
                    VendorCheckMapping mapping = VendorCheckMapping.builder()
                        .vendor(vendor)
                        .category(category)
                        .isActive(true)
                        .build();

                    vendorCheckMappingRepository.save(mapping);

                    log.info("✅ Assigned Vendor [{}] -> Category [{}]",
                            vendor.getId(),
                            category.getName());
                }
            }
        }

        log.info("🌱 VendorCheckMapping seeding completed.");
    }
}
