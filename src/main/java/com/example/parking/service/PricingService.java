package com.example.parking.service;

import com.example.parking.entity.AppUser;
import com.example.parking.entity.ParkingSpace;
import com.example.parking.entity.SubscriptionType;

import java.math.BigDecimal;

public interface PricingService {

    BigDecimal computeCharge(ParkingSpace space, AppUser user, long actualMinutes,
                             Integer expectedHours, boolean subscriber);

    BigDecimal subscriptionPrice(SubscriptionType type);
}
