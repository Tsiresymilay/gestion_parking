package com.example.parking.entity;

public enum SubscriptionType {
    JOURNALIER(1),
    HEBDOMADAIRE(7),
    MENSUEL(30),
    ANNUEL(365);

    private final int days;

    SubscriptionType(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }
}
