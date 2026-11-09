package com.example.parking.mapper;

import com.example.parking.dto.response.ReservationResponse;
import com.example.parking.entity.Reservation;

public final class ReservationMapper {

    private ReservationMapper() {
    }

    public static ReservationResponse toResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        if (reservation.getUser() != null) {
            response.setUserId(reservation.getUser().getId());
            response.setUserName(reservation.getUser().getFullName());
        }
        if (reservation.getVehicle() != null) {
            response.setVehicleId(reservation.getVehicle().getId());
            response.setPlateNumber(reservation.getVehicle().getPlateNumber());
        }
        if (reservation.getSpace() != null) {
            response.setSpaceId(reservation.getSpace().getId());
            response.setSpaceNumber(reservation.getSpace().getNumber());
        }
        response.setExpectedArrival(reservation.getExpectedArrival());
        response.setExpectedDeparture(reservation.getExpectedDeparture());
        response.setStatus(reservation.getStatus());
        return response;
    }
}
