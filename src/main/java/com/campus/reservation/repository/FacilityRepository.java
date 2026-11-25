package com.campus.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.reservation.model.Facility;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
}
