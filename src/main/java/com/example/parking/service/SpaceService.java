package com.example.parking.service;

import com.example.parking.dto.request.SpaceRequest;
import com.example.parking.dto.response.OccupancyResponse;
import com.example.parking.dto.response.SpaceResponse;
import com.example.parking.entity.SpaceStatus;
import com.example.parking.entity.SpaceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SpaceService {

    SpaceResponse create(SpaceRequest request);

    SpaceResponse update(Long id, SpaceRequest request);

    SpaceResponse findById(Long id);

    Page<SpaceResponse> search(String keyword, SpaceStatus status, SpaceType type, String zone, Pageable pageable);

    List<SpaceResponse> findAll();

    List<SpaceResponse> findAvailable();

    SpaceResponse changeStatus(Long id, SpaceStatus status);

    void delete(Long id);

    OccupancyResponse occupancy();
}
