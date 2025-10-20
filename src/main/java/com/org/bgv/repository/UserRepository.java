package com.org.bgv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.org.bgv.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
	
	
	    Optional<User> findByEmail(String email);
	    boolean existsByEmail(String email);
	    boolean existsByEmailAndUserIdNot(String email, Long userId);
	    Page<User> findByUserType(String userType, Pageable pageable);
	
	    // New method with JOIN FETCH to eagerly load roles
	    @Query("SELECT DISTINCT u FROM User u " +
	           "LEFT JOIN FETCH u.roles ur " +
	           "LEFT JOIN FETCH ur.role " +
	           "WHERE u.email = :email")
	    Optional<User> findByEmailWithRoles(@Param("email") String email);
	    
	    @Query("SELECT u FROM User u WHERE " +
	            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	            "LOWER(u.userType) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	     List<User> searchUsers(@Param("keyword") String keyword);
	     
	     @Query("SELECT u FROM User u WHERE " +
	            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	            "LOWER(u.userType) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	     Page<User> searchUsersWithPagination(@Param("keyword") String keyword, Pageable pageable);
	    
}
