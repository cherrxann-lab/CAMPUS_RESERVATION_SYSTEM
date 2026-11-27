package com.campus.reservation.dto;

import com.campus.reservation.model.Facility;

public class FacilityResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String location;
    private Integer capacity;
    private String type;
    private Boolean available;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    // Static factory method to convert from entity
    public static FacilityResponseDTO fromEntity(Facility facility) {
        FacilityResponseDTO dto = new FacilityResponseDTO();
        dto.setId(facility.getId());
        dto.setName(facility.getName());
        dto.setDescription(facility.getDescription());
        dto.setLocation(facility.getLocation());
        dto.setCapacity(facility.getCapacity());
        dto.setType(facility.getType());
        dto.setAvailable(facility.isAvailable());
        return dto;
    }
}