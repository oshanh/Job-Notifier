package org.oshanh.jobnotifier.repository;

import org.oshanh.jobnotifier.model.UniRuhunaJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniRuhunaJobRepository extends JpaRepository<UniRuhunaJob, Long> {
}
