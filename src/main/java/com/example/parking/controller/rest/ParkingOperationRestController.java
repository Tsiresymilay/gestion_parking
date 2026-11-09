package com.example.parking.controller.rest;

import com.example.parking.dto.request.EntryRequest;
import com.example.parking.dto.request.ExitRequest;
import com.example.parking.dto.response.OccupancyResponse;
import com.example.parking.dto.response.RevenueResponse;
import com.example.parking.dto.response.SessionResponse;
import com.example.parking.entity.SessionStatus;
import com.example.parking.service.ParkingService;
import com.example.parking.service.StatisticsService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/parking")
public class ParkingOperationRestController {

    private final ParkingService parkingService;
    private final StatisticsService statisticsService;

    public ParkingOperationRestController(ParkingService parkingService,
                                          StatisticsService statisticsService) {
        this.parkingService = parkingService;
        this.statisticsService = statisticsService;
    }

    @PostMapping("/entries")
    public ResponseEntity<SessionResponse> enter(@Valid @RequestBody EntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingService.enter(request));
    }

    @PostMapping("/exits")
    public SessionResponse exit(@Valid @RequestBody ExitRequest request) {
        return parkingService.exit(request);
    }

    @GetMapping("/sessions")
    public Page<SessionResponse> sessions(@RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) SessionStatus status,
                                          @PageableDefault(size = 10, sort = "entryTime") Pageable pageable) {
        return parkingService.search(keyword, status, pageable);
    }

    @GetMapping("/occupancy")
    public OccupancyResponse occupancy() {
        return statisticsService.occupancy();
    }

    @GetMapping("/revenue")
    public RevenueResponse revenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return statisticsService.revenue(from, to);
    }
}
