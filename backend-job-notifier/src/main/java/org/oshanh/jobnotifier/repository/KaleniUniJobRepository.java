package org.oshanh.jobnotifier.repository;

import org.oshanh.jobnotifier.model.KaleniUniJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KaleniUniJobRepository
        extends JpaRepository<KaleniUniJob, Long> {

    boolean existsByExternalId(String externalId);
}