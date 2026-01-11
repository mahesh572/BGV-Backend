package com.org.bgv.vendor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.org.bgv.vendor.dto.ActionLevel;
import com.org.bgv.vendor.dto.ActionType;
import com.org.bgv.vendor.dto.ReasonLevel;
import com.org.bgv.vendor.entity.ActionReason;

public interface ActionReasonRepository
extends JpaRepository<ActionReason, Long> {
	
	Optional<ActionReason> findByCode(String code);
	
	@Query("SELECT ar FROM ActionReason ar WHERE ar.code IN :codes")
    List<ActionReason> findAllByCodes(@Param("codes") List<String> codes);
	
	
	@Query("""
			SELECT car.reason
			FROM CategoryActionReason car
			WHERE car.category.id = :categoryId
			  AND car.reason.actionType = :actionType
			  AND car.reason.level = :level
			  AND car.active = true
			  AND car.reason.active = true
			ORDER BY car.reason.sortOrder
			""")
			List<ActionReason> findByCategoryAndActionAndLevel(
			    Long categoryId,
			    ActionType actionType,
			    ActionLevel level
			);
	
	
	@Query("""
			SELECT ar
			FROM ActionReason ar
			WHERE ar.actionType = :actionType
			  AND ar.level = :level
			  AND ar.active = true
			  AND (ar.category IS NULL OR ar.category.id = :categoryId)
			ORDER BY ar.sortOrder
			""")
			List<ActionReason> findApplicableReasons(
			    ActionType actionType,
			    ActionLevel level,
			    Long categoryId
			);


}
