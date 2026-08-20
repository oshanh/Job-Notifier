package org.oshanh.jobnotifier.repository;

import org.oshanh.jobnotifier.model.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findByEmail(String email);

    @Modifying
    @Transactional
    void deleteByEmail(String email);
}
