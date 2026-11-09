package com.example.parking.dto.request;

import com.example.parking.entity.SpaceStatus;
import com.example.parking.entity.SpaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SpaceRequest {

    @NotBlank(message = "Le numero de place est obligatoire")
    @Size(max = 20, message = "Le numero ne doit pas depasser 20 caracteres")
    private String number;

    @NotBlank(message = "La zone est obligatoire")
    @Size(max = 30, message = "La zone ne doit pas depasser 30 caracteres")
    private String zone;

    @NotNull(message = "Le type de place est obligatoire")
    private SpaceType type;

    @NotNull(message = "Le statut de la place est obligatoire")
    private SpaceStatus status = SpaceStatus.LIBRE;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public SpaceType getType() {
        return type;
    }

    public void setType(SpaceType type) {
        this.type = type;
    }

    public SpaceStatus getStatus() {
        return status;
    }

    public void setStatus(SpaceStatus status) {
        this.status = status;
    }
}
