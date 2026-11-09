package com.example.parking.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ExitRequest {

    @NotBlank(message = "L'immatriculation du vehicule est obligatoire")
    private String plateNumber;

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }
}
