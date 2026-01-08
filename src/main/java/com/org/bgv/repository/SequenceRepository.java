package com.org.bgv.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SequenceRepository {

    @Query(value = "SELECT nextval('candidate_ref_seq')", nativeQuery = true)
    Long nextCandidateSeq();

    @Query(value = "SELECT nextval('case_ref_seq')", nativeQuery = true)
    Long nextCaseSeq();
}
