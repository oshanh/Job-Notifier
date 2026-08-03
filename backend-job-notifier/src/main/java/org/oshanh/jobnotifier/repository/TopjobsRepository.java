package org.oshanh.jobnotifier.repository;

import org.oshanh.jobnotifier.model.Topjobs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TopjobsRepository extends JpaRepository<Topjobs, Long> {
    @Query(value = "SELECT ref_no from Topjobs ", nativeQuery = true)
    Set<Integer> getAllJobsRefNos();
}
