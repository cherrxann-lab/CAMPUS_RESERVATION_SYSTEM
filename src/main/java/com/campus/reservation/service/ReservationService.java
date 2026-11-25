
package com.campus.reservation.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.reservation.exception.ReservationConflictException;
import com.campus.reservation.model.Facility;
import com.campus.reservation.model.Reservation;
import com.campus.reservation.model.User;
import com.campus.reservation.repository.FacilityRepository;
import com.campus.reservation.repository.ReservationRepository;
import com.campus.reservation.repository.UserRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final FacilityRepository facilityRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            FacilityRepository facilityRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.facilityRepository = facilityRepository;
    }

    private User getAuthorizedReviewer(Long reviewerId) {
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Reviewer not found."));

        User.Role role = reviewer.getRole();

        if (role != User.Role.ADMIN) {
            throw new IllegalArgumentException(
                    "Only admins can review reservations.");
        }

        return reviewer;
    }

    @Transactional
    public Reservation createReservation(
            Long userId,
            Long facilityId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String purpose) {

        if (startTime == null || endTime == null
                || !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException(
                    "End time must be after start time.");
        }

        if (!startTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Reservation start time must be in the future.");
        }

        if (purpose == null || purpose.isBlank()) {
            throw new IllegalArgumentException(
                    "Reservation purpose is required.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found."));

        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Facility not found."));

        if (!facility.isAvailable()) {
            throw new IllegalArgumentException(
                    "This facility is not currently available.");
        }

        boolean conflict = !reservationRepository.findConflicts(
                facilityId, startTime, endTime).isEmpty();

        if (conflict) {
            throw new ReservationConflictException(
                    "This facility already has a pending or approved reservation "
                    + "during that time.");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setFacility(facility);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setPurpose(purpose);
        reservation.setStatus(Reservation.Status.PENDING);

        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation approveReservation(
            Long reservationId,
            Long reviewerId) {

        getAuthorizedReviewer(reviewerId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Reservation not found."));

        if (reservation.getStatus() != Reservation.Status.PENDING) {
            throw new IllegalArgumentException(
                    "Only pending reservations can be approved.");
        }

        Facility facility = reservation.getFacility();

        if (!facility.isAvailable()) {
            throw new IllegalArgumentException(
                    "This facility is not available.");
        }

        boolean conflict = reservationRepository.findConflicts(
                facility.getId(),
                reservation.getStartTime(),
                reservation.getEndTime()
        ).stream()
                .anyMatch(r -> !r.getId().equals(reservationId));

        if (conflict) {
            throw new ReservationConflictException(
                    "This time slot conflicts with another pending or approved reservation.");
        }

        reservation.setStatus(Reservation.Status.APPROVED);

        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation rejectReservation(
            Long reservationId,
            Long reviewerId,
            String reason) {

        getAuthorizedReviewer(reviewerId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Reservation not found."));

        if (reservation.getStatus() != Reservation.Status.PENDING) {
            throw new IllegalArgumentException(
                    "Only pending reservations can be rejected.");
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "Please provide a reason for rejection.");
        }

        reservation.setStatus(Reservation.Status.REJECTED);
        reservation.setDecisionReason(reason.trim());

        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation cancel(Long reservationId, Long userId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Reservation not found."));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException(
                    "You can only cancel your own reservations.");
        }

        if (!reservation.getStartTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Reservations that have already started cannot be cancelled.");
        }

        if (reservation.getStatus() != Reservation.Status.PENDING
                && reservation.getStatus() != Reservation.Status.APPROVED) {
            throw new IllegalArgumentException(
                    "This reservation can no longer be cancelled.");
        }

        reservation.setStatus(Reservation.Status.CANCELLED);

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getByUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found.");
        }

        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    public List<Reservation> getByFacility(Long facilityId) {

        if (!facilityRepository.existsById(facilityId)) {
            throw new IllegalArgumentException("Facility not found.");
        }

        return reservationRepository.findByFacilityId(facilityId);
    }
}
