package com.example.parking.service.impl;

import com.example.parking.entity.AppUser;
import com.example.parking.entity.ParkingSpace;
import com.example.parking.entity.SpaceType;
import com.example.parking.entity.SubscriptionType;
import com.example.parking.service.PricingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class PricingServiceImpl implements PricingService {

    private static final Map<SpaceType, BigDecimal> HOURLY_RATE = Map.of(
            SpaceType.STANDARD, new BigDecimal("1000"),
            SpaceType.PMR, new BigDecimal("1000"),
            SpaceType.VIP, new BigDecimal("2000"),
            SpaceType.ABONNE, new BigDecimal("1500")
    );

    private static final Map<SubscriptionType, BigDecimal> SUBSCRIPTION_PRICE = Map.of(
            SubscriptionType.JOURNALIER, new BigDecimal("3000"),
            SubscriptionType.HEBDOMADAIRE, new BigDecimal("15000"),
            SubscriptionType.MENSUEL, new BigDecimal("50000"),
            SubscriptionType.ANNUEL, new BigDecimal("500000")
    );

    private static final BigDecimal OVERTIME_FACTOR = new BigDecimal("1.5");
    private static final BigDecimal SUBSCRIBER_DISCOUNT = new BigDecimal("0.80");

    @Override
    public BigDecimal computeCharge(ParkingSpace space, AppUser user, long actualMinutes,
                                    Integer expectedHours, boolean subscriber) {
        BigDecimal rate = hourlyRate(space.getType(), space.getZone());
        long actualHours = Math.max(1, (long) Math.ceil(actualMinutes / 60.0));

        BigDecimal charge;
        if (expectedHours != null && expectedHours > 0 && actualHours > expectedHours) {
            long overtimeHours = actualHours - expectedHours;
            charge = rate.multiply(BigDecimal.valueOf(expectedHours))
                    .add(rate.multiply(OVERTIME_FACTOR).multiply(BigDecimal.valueOf(overtimeHours)));
        } else {
            charge = rate.multiply(BigDecimal.valueOf(actualHours));
        }

        if (subscriber) {
            charge = charge.multiply(SUBSCRIBER_DISCOUNT);
        }
        return charge.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal subscriptionPrice(SubscriptionType type) {
        return SUBSCRIPTION_PRICE.get(type);
    }

    private BigDecimal hourlyRate(SpaceType type, String zone) {
        BigDecimal base = HOURLY_RATE.getOrDefault(type, new BigDecimal("1000"));
        return base.multiply(zoneFactor(zone));
    }

    private BigDecimal zoneFactor(String zone) {
        if (zone == null) {
            return BigDecimal.ONE;
        }
        return switch (zone.toUpperCase()) {
            case "VIP" -> new BigDecimal("1.5");
            case "A" -> new BigDecimal("1.2");
            case "C" -> new BigDecimal("0.8");
            default -> BigDecimal.ONE;
        };
    }
}
