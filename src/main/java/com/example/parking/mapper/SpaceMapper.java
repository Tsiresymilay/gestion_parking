package com.example.parking.mapper;

import com.example.parking.dto.request.SpaceRequest;
import com.example.parking.dto.response.SpaceResponse;
import com.example.parking.entity.ParkingSpace;

public final class SpaceMapper {

    private SpaceMapper() {
    }

    public static SpaceResponse toResponse(ParkingSpace space) {
        SpaceResponse response = new SpaceResponse();
        response.setId(space.getId());
        response.setNumber(space.getNumber());
        response.setZone(space.getZone());
        response.setType(space.getType());
        response.setStatus(space.getStatus());
        return response;
    }

    public static ParkingSpace toEntity(SpaceRequest request) {
        ParkingSpace space = new ParkingSpace();
        apply(request, space);
        return space;
    }

    public static void apply(SpaceRequest request, ParkingSpace space) {
        space.setNumber(request.getNumber());
        space.setZone(request.getZone());
        space.setType(request.getType());
        if (request.getStatus() != null) {
            space.setStatus(request.getStatus());
        }
    }
}
