package org.oshanh.jobnotifier.repository;

import org.oshanh.jobnotifier.model.FosmisUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FosmisUserRepository extends JpaRepository<FosmisUser,Long> {
}
