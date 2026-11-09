package com.example.parking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class EntryRequest {

    @NotBlank(message = "L'immatriculation du vehicule est obligatoire")
    private String plateNumber;

    private Long userId;

    private Long spaceId;

    @Positive(message = "La duree prevue doit etre positive")
    private Integer expectedDurationHours;

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }

    public Integer getExpectedDurationHours() {
        return expectedDurationHours;
    }

    public void setExpectedDurationHours(Integer expectedDurationHours) {
        this.expectedDurationHours = expectedDurationHours;
    }
}
