package com.example.parking.service;

import com.example.parking.dto.request.VehicleRequest;
import com.example.parking.dto.response.VehicleResponse;
import com.example.parking.entity.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehicleService {

    VehicleResponse create(VehicleRequest request);

    VehicleResponse update(Long id, VehicleRequest request);

    VehicleResponse findById(Long id);

    Page<VehicleResponse> search(String keyword, VehicleType type, Pageable pageable);

    List<VehicleResponse> findAll();

    void delete(Long id);
}
