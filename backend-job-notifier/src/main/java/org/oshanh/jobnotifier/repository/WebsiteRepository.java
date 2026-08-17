package org.oshanh.jobnotifier.repository;

import org.oshanh.jobnotifier.model.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebsiteRepository extends JpaRepository<Website, Integer> {
    Website findByBaseURL(String website);
    Website findWebsiteByBaseURLContainsIgnoreCase(String website);
}
