package com.servicedesk.repository;

import com.servicedesk.entity.SLATracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for SLATracking entity
 */
@Repository
public interface SLATrackingRepository extends JpaRepository<SLATracking, Long> {

    Optional<SLATracking> findByRequestId(Long requestId);

    @Query("SELECT s FROM SLATracking s WHERE s.responseDueAt < :currentTime AND s.responseCompletedAt IS NULL AND s.isResponseBreached = false")
    List<SLATracking> findResponseSLABreaches(@Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT s FROM SLATracking s WHERE s.resolutionDueAt < :currentTime AND s.resolutionCompletedAt IS NULL AND s.isResolutionBreached = false")
    List<SLATracking> findResolutionSLABreaches(@Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT COUNT(s) FROM SLATracking s WHERE s.isResponseBreached = true")
    Long countResponseBreaches();

    @Query("SELECT COUNT(s) FROM SLATracking s WHERE s.isResolutionBreached = true")
    Long countResolutionBreaches();
}
