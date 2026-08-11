package org.oshanh.jobnotifier.repository;

import org.oshanh.jobnotifier.model.FosmisNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FosmisNoticeRepository extends JpaRepository<FosmisNotice, Long> {
    boolean existsByLink(String link);
}