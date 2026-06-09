package com.org.bgv.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.vendor.entity.VendorUser;

@Repository
public interface VendorUserRepository
        extends JpaRepository<VendorUser, Long> {

}