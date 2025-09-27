package com.org.bgv.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.entity.Permission;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.RolePermission;



public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
	
	boolean existsByRoleAndPermission(Role role, Permission permission);
}
