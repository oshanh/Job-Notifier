package org.oshanh.jobnotifier.repository;

import org.oshanh.jobnotifier.model.Preference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrefRepository extends JpaRepository<Preference, Long> {

    Preference findByUser_Id(Long id);

    @Query("SELECT p FROM Preference p JOIN p.websites w WHERE w.id = :websiteId")
    List<Preference> findPreferencesBySubscribedWebsite(@Param("websiteId") Long websiteId);
}
