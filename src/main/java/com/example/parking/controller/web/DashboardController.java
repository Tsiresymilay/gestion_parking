package com.example.parking.controller.web;

import com.example.parking.service.ParkingService;
import com.example.parking.service.StatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class DashboardController {

    private final StatisticsService statisticsService;
    private final ParkingService parkingService;

    public DashboardController(StatisticsService statisticsService, ParkingService parkingService) {
        this.statisticsService = statisticsService;
        this.parkingService = parkingService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("occupancy", statisticsService.occupancy());
        model.addAttribute("revenue", statisticsService.revenue(LocalDate.now().withDayOfMonth(1), LocalDate.now()));
        model.addAttribute("activeSessions", parkingService.findActive());
        return "dashboard";
    }
}
