package com.example.parking.dto.response;

public class OccupancyResponse {

    private long total;
    private long libre;
    private long reservee;
    private long occupee;
    private long horsService;
    private double tauxOccupation;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getLibre() {
        return libre;
    }

    public void setLibre(long libre) {
        this.libre = libre;
    }

    public long getReservee() {
        return reservee;
    }

    public void setReservee(long reservee) {
        this.reservee = reservee;
    }

    public long getOccupee() {
        return occupee;
    }

    public void setOccupee(long occupee) {
        this.occupee = occupee;
    }

    public long getHorsService() {
        return horsService;
    }

    public void setHorsService(long horsService) {
        this.horsService = horsService;
    }

    public double getTauxOccupation() {
        return tauxOccupation;
    }

    public void setTauxOccupation(double tauxOccupation) {
        this.tauxOccupation = tauxOccupation;
    }
}
