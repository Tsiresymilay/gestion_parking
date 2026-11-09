package com.example.parking.service;

import com.example.parking.dto.response.OccupancyResponse;
import com.example.parking.dto.response.RevenueResponse;

import java.time.LocalDate;

public interface StatisticsService {

    OccupancyResponse occupancy();

    RevenueResponse revenue(LocalDate from, LocalDate to);
}
