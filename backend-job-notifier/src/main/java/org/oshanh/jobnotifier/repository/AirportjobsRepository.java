package org.oshanh.jobnotifier.repository;

import org.oshanh.jobnotifier.model.Airportjobs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportjobsRepository extends JpaRepository<Airportjobs, Long> {
}
