package com.example.parking.dto.request;

import com.example.parking.entity.SubscriptionType;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class SubscriptionRequest {

    @NotNull(message = "L'utilisateur est obligatoire")
    private Long userId;

    @NotNull(message = "Le vehicule est obligatoire")
    private Long vehicleId;

    @NotNull(message = "Le type d'abonnement est obligatoire")
    private SubscriptionType type;

    @NotNull(message = "La date de debut est obligatoire")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public SubscriptionType getType() {
        return type;
    }

    public void setType(SubscriptionType type) {
        this.type = type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}
