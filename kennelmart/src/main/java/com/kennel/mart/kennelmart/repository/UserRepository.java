package com.kennel.mart.kennelmart.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.AccountStatus;
import com.kennel.mart.kennelmart.enums.VerificationStatus;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByVerificationStatus(VerificationStatus status);

    Page<User> findByVerificationStatus(VerificationStatus status, Pageable pageable);

    Page<User> findByAccountStatus(AccountStatus status, Pageable pageable);

    Page<User> findByVerificationStatusAndAccountStatus(VerificationStatus vStatus, AccountStatus aStatus, Pageable pageable);

    /**
     * Search active users by name or email (case-insensitive, partial match).
     * Only returns users with AccountStatus.ACTIVE.
     */
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndAccountStatus(String name, String email, AccountStatus status, Pageable pageable);

    Optional<User> findByIdAndAccountStatus(UUID id, AccountStatus status);

}