package com.example.parking.service.impl;

import com.example.parking.dto.response.OccupancyResponse;
import com.example.parking.dto.response.RevenueResponse;
import com.example.parking.entity.ParkingSession;
import com.example.parking.repository.ParkingSessionRepository;
import com.example.parking.service.SpaceService;
import com.example.parking.service.StatisticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final ParkingSessionRepository sessionRepository;
    private final SpaceService spaceService;

    public StatisticsServiceImpl(ParkingSessionRepository sessionRepository, SpaceService spaceService) {
        this.sessionRepository = sessionRepository;
        this.spaceService = spaceService;
    }

    @Override
    public OccupancyResponse occupancy() {
        return spaceService.occupancy();
    }

    @Override
    public RevenueResponse revenue(LocalDate from, LocalDate to) {
        LocalDate start = from != null ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate end = to != null ? to : LocalDate.now();
        LocalDateTime fromDateTime = start.atStartOfDay();
        LocalDateTime toDateTime = end.plusDays(1).atStartOfDay();

        List<ParkingSession> sessions = sessionRepository.findCompletedBetween(fromDateTime, toDateTime);
        BigDecimal total = BigDecimal.ZERO;
        for (ParkingSession session : sessions) {
            if (session.getAmount() != null) {
                total = total.add(session.getAmount());
            }
        }

        RevenueResponse response = new RevenueResponse();
        response.setTotal(total);
        response.setNombreSessions(sessions.size());
        response.setRevenuMoyen(sessions.isEmpty() ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(sessions.size()), 2, RoundingMode.HALF_UP));
        return response;
    }
}
