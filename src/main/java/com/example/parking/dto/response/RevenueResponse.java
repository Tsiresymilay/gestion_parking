package com.example.parking.dto.response;

import java.math.BigDecimal;

public class RevenueResponse {

    private BigDecimal total;
    private long nombreSessions;
    private BigDecimal revenuMoyen;

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public long getNombreSessions() {
        return nombreSessions;
    }

    public void setNombreSessions(long nombreSessions) {
        this.nombreSessions = nombreSessions;
    }

    public BigDecimal getRevenuMoyen() {
        return revenuMoyen;
    }

    public void setRevenuMoyen(BigDecimal revenuMoyen) {
        this.revenuMoyen = revenuMoyen;
    }
}
