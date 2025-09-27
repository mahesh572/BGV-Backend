package com.org.bgv.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.entity.Permission;


public interface PermissionRepository extends JpaRepository<Permission, Long> {
	Optional<Permission> findByName(String name);

}
