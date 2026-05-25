package com.kennel.mart.kennelmart.repository;

import com.kennel.mart.kennelmart.entity.VerifiedIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface VerifiedIdentityRepository extends JpaRepository<VerifiedIdentity, UUID> {
    boolean existsBySchoolId(String schoolId);
    boolean existsByEmail(String email);
    Optional<VerifiedIdentity> findBySchoolId(String schoolId);
}