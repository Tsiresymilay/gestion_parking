package com.example.parking.service;

import com.example.parking.dto.request.SubscriptionRequest;
import com.example.parking.dto.response.SubscriptionResponse;
import com.example.parking.entity.SubscriptionStatus;
import com.example.parking.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubscriptionService {

    SubscriptionResponse create(SubscriptionRequest request);

    SubscriptionResponse findById(Long id);

    Page<SubscriptionResponse> search(String keyword, SubscriptionStatus status, Pageable pageable);

    List<SubscriptionResponse> findAll();

    SubscriptionResponse cancel(Long id);

    boolean isVehicleCovered(Vehicle vehicle);

    void refreshExpired();
}
