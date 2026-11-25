
package com.campus.reservation.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.reservation.model.Reservation;
import com.campus.reservation.model.Reservation.Status;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    List<Reservation> findByStatus(Status status);

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByFacilityId(Long facilityId);

    @Query("""
        SELECT r FROM Reservation r
        WHERE r.facility.id = :facilityId
          AND r.status IN (
              com.campus.reservation.model.Reservation.Status.PENDING,
              com.campus.reservation.model.Reservation.Status.APPROVED
          )
          AND r.startTime < :end
          AND r.endTime > :start
        """)
    List<Reservation> findConflicts(
            @Param("facilityId") Long facilityId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
