package com.org.bgv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {}
