package com.example.parking.service;

import com.example.parking.dto.request.EntryRequest;
import com.example.parking.dto.request.ExitRequest;
import com.example.parking.dto.response.SessionResponse;
import com.example.parking.entity.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ParkingService {

    SessionResponse enter(EntryRequest request);

    SessionResponse exit(ExitRequest request);

    SessionResponse findById(Long id);

    Page<SessionResponse> search(String keyword, SessionStatus status, Pageable pageable);

    List<SessionResponse> findActive();
}
