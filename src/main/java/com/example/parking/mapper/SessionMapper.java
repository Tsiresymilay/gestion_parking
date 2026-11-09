package com.example.parking.mapper;

import com.example.parking.dto.response.SessionResponse;
import com.example.parking.entity.ParkingSession;

public final class SessionMapper {

    private SessionMapper() {
    }

    public static SessionResponse toResponse(ParkingSession session) {
        SessionResponse response = new SessionResponse();
        response.setId(session.getId());
        if (session.getVehicle() != null) {
            response.setVehicleId(session.getVehicle().getId());
            response.setPlateNumber(session.getVehicle().getPlateNumber());
        }
        if (session.getSpace() != null) {
            response.setSpaceId(session.getSpace().getId());
            response.setSpaceNumber(session.getSpace().getNumber());
        }
        if (session.getUser() != null) {
            response.setUserId(session.getUser().getId());
            response.setUserName(session.getUser().getFullName());
        }
        response.setEntryTime(session.getEntryTime());
        response.setExitTime(session.getExitTime());
        response.setExpectedDurationHours(session.getExpectedDurationHours());
        response.setAmount(session.getAmount());
        response.setStatus(session.getStatus());
        return response;
    }
}
