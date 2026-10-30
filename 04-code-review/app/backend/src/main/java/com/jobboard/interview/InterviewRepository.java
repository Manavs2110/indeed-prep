package com.jobboard.interview;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, String> {

    List<Interview> findByEmployerId(String employerId);

    List<Interview> findByCandidateId(String candidateId);
}
