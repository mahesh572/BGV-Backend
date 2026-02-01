package com.org.bgv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.org.bgv.entity.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    List<Role> findByType(Long type);
    boolean existsByName(String name);
    
 // Using native SQL query
    @Query(value = "SELECT r.* FROM roles r " +
                   "INNER JOIN user_roles ur ON r.id = ur.role_id " +
                   "INNER JOIN users u ON ur.user_id = u.id " +
                   "WHERE u.userId = :userId", 
           nativeQuery = true)
    List<Role> findRolesByUserIdNative(@Param("userId") Long userId);
    
    @Query("SELECT r FROM Role r " +
            "JOIN r.users ur " +
            "WHERE ur.user.userId = :userId")
     List<Role> findRolesByUserId(@Param("userId") Long userId);
}