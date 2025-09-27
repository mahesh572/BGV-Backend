package com.org.bgv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.entity.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}