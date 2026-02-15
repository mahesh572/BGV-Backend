package com.org.bgv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.org.bgv.entity.Role;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
	
	List<UserRole> findByRole(Role role);
	
    
 // Find UserRole entities by user and role IDs
    @Query("SELECT ur FROM UserRole ur WHERE ur.user.userId = :userId AND ur.role.id IN :roleIds")
    List<UserRole> findByUserAndRoleIds(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
	
	@Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.role = :role")
    Integer countByRole(@Param("role") Role role);
    
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.role.id = :roleId")
    Integer countByRoleId(@Param("roleId") Long roleId);
    
 // Find UserRole entities by user and role IDs
    @Query("SELECT ur FROM UserRole ur WHERE ur.user = :user AND ur.role.id IN :roleIds")
    List<UserRole> findByUserAndRoleIdIn(@Param("user") User user, @Param("roleIds") List<Long> roleIds);


    // Find all roles for a user
    @Query("SELECT ur.role FROM UserRole ur WHERE ur.user.userId = :userId")
    List<Role> findRolesByUserId(@Param("userId") Long userId);

    // Find UserRole entities with role details for a user
    @Query("SELECT ur FROM UserRole ur JOIN FETCH ur.role WHERE ur.user.userId = :userId")
    List<UserRole> findByUserIdWithRole(@Param("userId") Long userId);

    // Count users with a specific role
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.role.id = :roleId")
    Long countUsersWithRole(@Param("roleId") Long roleId);
    
 
    
    @Query("SELECT ur FROM UserRole ur WHERE ur.user.userId = :userId AND ur.role.id IN :roleIds")
    List<UserRole> findByUserIdAndRoleIds(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
    
 // Use custom queries with explicit field names
    @Query("SELECT COUNT(ur) > 0 FROM UserRole ur WHERE ur.user.userId = :userId AND ur.role.id = :roleId")
    boolean existsByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    
    
    @Query("""
            SELECT COUNT(DISTINCT ur.user.userId)
            FROM UserRole ur
            JOIN Employee e ON e.user.userId = ur.user.userId
            WHERE ur.role = :role
            AND e.company.Id = :companyId
        """)
        Integer countByRoleAndCompany(
            @Param("role") Role role,
            @Param("companyId") Long companyId
        );

       
	
}
