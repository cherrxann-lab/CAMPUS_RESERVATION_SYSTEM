
package com.campus.reservation.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.reservation.model.Reservation;
import com.campus.reservation.repository.ReservationRepository;
import com.campus.reservation.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    public ReservationController(
            ReservationRepository reservationRepository,
            ReservationService reservationService) {
        this.reservationRepository = reservationRepository;
        this.reservationService = reservationService;
    }

    @GetMapping("/pending")
    public List<Reservation> getPendingReservations() {
        return reservationRepository.findByStatus(
                Reservation.Status.PENDING
        );
    }

    @PostMapping
    public Reservation createReservation(
            @RequestParam Long userId,
            @RequestParam Long facilityId,
            @RequestBody ReservationRequest request) {

        return reservationService.createReservation(
                userId,
                facilityId,
                request.getStartTime(),
                request.getEndTime(),
                request.getPurpose()
        );
    }

    @PostMapping("/{id}/approve")
    public Reservation approveReservation(
            @PathVariable Long id,
            @RequestParam Long reviewerId) {

        return reservationService.approveReservation(id, reviewerId);
    }

    @PostMapping("/{id}/reject")
    public Reservation rejectReservation(
            @PathVariable Long id,
            @RequestParam Long reviewerId,
            @RequestBody RejectionRequest request) {

        return reservationService.rejectReservation(
                id,
                reviewerId,
                request.getReason()
        );
    }

    public static class ReservationRequest {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String purpose;

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }

        public LocalDateTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
        }

        public String getPurpose() {
            return purpose;
        }

        public void setPurpose(String purpose) {
            this.purpose = purpose;
        }
    }

    public static class RejectionRequest {
        private String reason;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
