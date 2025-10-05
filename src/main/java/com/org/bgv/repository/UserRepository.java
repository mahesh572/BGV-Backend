package com.org.bgv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.org.bgv.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	    Optional<User> findByEmail(String email);
	    boolean existsByEmail(String email);
	    boolean existsByEmailAndUserIdNot(String email, Long userId);
	
	    // New method with JOIN FETCH to eagerly load roles
	    @Query("SELECT DISTINCT u FROM User u " +
	           "LEFT JOIN FETCH u.roles ur " +
	           "LEFT JOIN FETCH ur.role " +
	           "WHERE u.email = :email")
	    Optional<User> findByEmailWithRoles(@Param("email") String email);
	    
}
