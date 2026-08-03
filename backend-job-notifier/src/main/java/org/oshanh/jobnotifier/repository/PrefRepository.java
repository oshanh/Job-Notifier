package org.oshanh.jobnotifier.repository;

import org.oshanh.jobnotifier.model.Preference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrefRepository extends JpaRepository<Preference,Long> {

    Preference findByUser_Id(Long id);
}
