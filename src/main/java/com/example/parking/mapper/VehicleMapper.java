package com.example.parking.mapper;

import com.example.parking.dto.response.VehicleResponse;
import com.example.parking.entity.Vehicle;

public final class VehicleMapper {

    private VehicleMapper() {
    }

    public static VehicleResponse toResponse(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();
        response.setId(vehicle.getId());
        response.setPlateNumber(vehicle.getPlateNumber());
        response.setBrand(vehicle.getBrand());
        response.setModel(vehicle.getModel());
        response.setYear(vehicle.getYear());
        response.setType(vehicle.getType());
        if (vehicle.getOwner() != null) {
            response.setOwnerId(vehicle.getOwner().getId());
            response.setOwnerName(vehicle.getOwner().getFullName());
        }
        return response;
    }
}
