package com.example.parking.service;

import com.example.parking.dto.request.ReservationRequest;
import com.example.parking.dto.response.ReservationResponse;
import com.example.parking.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReservationService {

    ReservationResponse create(ReservationRequest request);

    ReservationResponse update(Long id, ReservationRequest request);

    ReservationResponse findById(Long id);

    Page<ReservationResponse> search(String keyword, ReservationStatus status, Pageable pageable);

    List<ReservationResponse> findAll();

    ReservationResponse confirm(Long id);

    ReservationResponse cancel(Long id);

    void delete(Long id);
}
