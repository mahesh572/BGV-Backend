package com.org.bgv.global.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.global.entity.UserAddress;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    /* =========================
       BASIC USER QUERIES
       ========================= */

    List<UserAddress> findByUser_UserId(Long userId);

    List<UserAddress> findByUser_UserIdAndStatus(Long userId, String status);

    Optional<UserAddress> findByIdAndUser_UserId(Long addressId, Long userId);

    /* =========================
       DEFAULT / PRIMARY ADDRESS
       ========================= */

    Optional<UserAddress> findByUser_UserIdAndDefaultAddressTrue(Long userId);

    /* =========================
       ADDRESS TYPE
       ========================= */

    List<UserAddress> findByUser_UserIdAndAddressType(Long userId, Enum<?> addressType);

    /* =========================
       CURRENT RESIDENCE
       ========================= */

    List<UserAddress> findByUser_UserIdAndCurrentlyResidingAtThisAddressTrue(Long userId);

    /* =========================
       PERMANENT ADDRESS
       ========================= */

    Optional<UserAddress> findByUser_UserIdAndIsMyPermanentAddressTrue(Long userId);

    /* =========================
       SOFT DELETE / ARCHIVE
       ========================= */

    default void archiveAddress(UserAddress address) {
        address.setStatus("archived");
        save(address);
    }
}

