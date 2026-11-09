package com.example.parking.mapper;

import com.example.parking.dto.response.SubscriptionResponse;
import com.example.parking.entity.Subscription;
import com.example.parking.entity.SubscriptionStatus;

import java.time.LocalDate;

public final class SubscriptionMapper {

    private SubscriptionMapper() {
    }

    public static SubscriptionResponse toResponse(Subscription subscription) {
        SubscriptionResponse response = new SubscriptionResponse();
        response.setId(subscription.getId());
        if (subscription.getUser() != null) {
            response.setUserId(subscription.getUser().getId());
            response.setUserName(subscription.getUser().getFullName());
        }
        if (subscription.getVehicle() != null) {
            response.setVehicleId(subscription.getVehicle().getId());
            response.setPlateNumber(subscription.getVehicle().getPlateNumber());
        }
        response.setType(subscription.getType());
        response.setStartDate(subscription.getStartDate());
        response.setEndDate(subscription.getEndDate());
        response.setStatus(subscription.getStatus());
        response.setValid(subscription.getStatus() == SubscriptionStatus.ACTIF
                && !subscription.getEndDate().isBefore(LocalDate.now()));
        return response;
    }
}
