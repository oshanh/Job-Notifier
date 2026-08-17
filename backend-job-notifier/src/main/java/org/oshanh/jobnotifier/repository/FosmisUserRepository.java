package org.oshanh.jobnotifier.repository;

import org.oshanh.jobnotifier.model.FosmisUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FosmisUserRepository extends JpaRepository<FosmisUser, Long> {
    Optional<FosmisUser> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT f.email FROM FosmisUser f WHERE f.isEnabled=true")
    List<String> findAllEnabledEmails();


}
