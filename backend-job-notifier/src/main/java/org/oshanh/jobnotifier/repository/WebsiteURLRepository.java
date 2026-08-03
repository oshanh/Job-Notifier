package org.oshanh.jobnotifier.repository;

import org.oshanh.jobnotifier.model.WebsiteURL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebsiteURLRepository extends JpaRepository<WebsiteURL, Integer> {
}
