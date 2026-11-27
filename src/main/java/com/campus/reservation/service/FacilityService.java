
package com.campus.reservation.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.reservation.model.Facility;
import com.campus.reservation.repository.FacilityRepository;

@Service
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public FacilityService(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    public Facility getFacilityById(Long id) {
        return facilityRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Facility not found."));
    }

    @Transactional
    public Facility createFacility(Facility facility) {

        if (facility.getName() == null
                || facility.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Facility name is required.");
        }

        if (facility.getCapacity() <= 0) {
            throw new IllegalArgumentException(
                    "Facility capacity must be greater than zero.");
        }

        facility.setAvailable(true);

        return facilityRepository.save(facility);
    }

    @Transactional
    public Facility updateFacility(Long id, Facility updatedFacility) {

        Facility facility = getFacilityById(id);

        if (updatedFacility.getName() == null
                || updatedFacility.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Facility name is required.");
        }

        if (updatedFacility.getCapacity() <= 0) {
            throw new IllegalArgumentException(
                    "Facility capacity must be greater than zero.");
        }

        facility.setName(updatedFacility.getName());
        facility.setDescription(updatedFacility.getDescription());
        facility.setType(updatedFacility.getType());
        facility.setLocation(updatedFacility.getLocation());
        facility.setCapacity(updatedFacility.getCapacity());
        facility.setAvailable(updatedFacility.isAvailable());

        return facilityRepository.save(facility);
    }

    @Transactional
    public void deleteFacility(Long id) {

        Facility facility = getFacilityById(id);

        if (!facilityRepository.existsById(id)) {
            throw new IllegalArgumentException("Facility not found.");
        }

        facilityRepository.delete(facility);
    }
}
