package com.org.bgv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.ReferenceSequence;

import jakarta.persistence.LockModeType;

@Repository
public interface ReferenceSequenceRepository
        extends JpaRepository<ReferenceSequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT rs FROM ReferenceSequence rs
        WHERE rs.seqType = :type AND rs.year = :year
    """)
    Optional<ReferenceSequence> lockByTypeAndYear(
        @Param("type") String type,
        @Param("year") int year
    );
}