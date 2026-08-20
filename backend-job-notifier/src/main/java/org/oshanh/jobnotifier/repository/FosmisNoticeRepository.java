package org.oshanh.jobnotifier.repository;

import org.oshanh.jobnotifier.model.FosmisNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FosmisNoticeRepository extends JpaRepository<FosmisNotice, Long> {

    @Query("SELECT f.link FROM FosmisNotice f WHERE f.link IN :links")
    Set<String> findExistingLinks(@Param("links") List<String> links);




}