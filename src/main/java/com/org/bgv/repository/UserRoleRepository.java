package com.org.bgv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.org.bgv.entity.Role;
import com.org.bgv.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
	
	List<UserRole> findByRole(Role role);
	
	@Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.role = :role")
    Integer countByRole(@Param("role") Role role);
    
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.role.id = :roleId")
    Integer countByRoleId(@Param("roleId") Long roleId);
	
}
