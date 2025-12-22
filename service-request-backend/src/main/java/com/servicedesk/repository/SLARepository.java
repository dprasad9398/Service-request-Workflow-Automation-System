package com.servicedesk.repository;

import com.servicedesk.entity.SLA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for SLA entity
 */
@Repository
public interface SLARepository extends JpaRepository<SLA, Long> {

    Optional<SLA> findByName(String name);
}
