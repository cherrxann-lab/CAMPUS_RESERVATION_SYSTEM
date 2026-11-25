
package com.campus.reservation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.campus.reservation.model.Facility;
import com.campus.reservation.repository.FacilityRepository;

@RestController
@RequestMapping("/api/facilities")
public class FacilityController {

    private final FacilityRepository facilityRepository;

    public FacilityController(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    @GetMapping
    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    @GetMapping("/{id}")
    public Facility getFacilityById(@PathVariable Long id) {
        return facilityRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Facility not found."));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Facility addFacility(@RequestBody Facility facility) {
        facility.setAvailable(true);
        return facilityRepository.save(facility);
    }

    @PutMapping("/{id}")
    public Facility updateFacility(
            @PathVariable Long id,
            @RequestBody Facility updatedFacility) {

        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Facility not found."));

        facility.setName(updatedFacility.getName());
        facility.setDescription(updatedFacility.getDescription());
        facility.setType(updatedFacility.getType());
        facility.setLocation(updatedFacility.getLocation());
        facility.setCapacity(updatedFacility.getCapacity());
        facility.setAvailable(updatedFacility.isAvailable());

        return facilityRepository.save(facility);
    }
}
