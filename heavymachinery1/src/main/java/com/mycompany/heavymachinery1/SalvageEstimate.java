package com.mycompany.heavymachinery1;

// Egy bontasra szant gep becsult bontasi koltsegei (munkadij + szallitas).
public class SalvageEstimate {
    private String machineId;
    private double estimatedLaborCost;
    private double estimatedTransportCost;

    public SalvageEstimate(String machineId, double estimatedLaborCost, double estimatedTransportCost) {
        this.machineId = machineId;
        this.estimatedLaborCost = estimatedLaborCost;
        this.estimatedTransportCost = estimatedTransportCost;
    }

    public double getTotalCost() {
        return estimatedLaborCost + estimatedTransportCost;
    }

    public String getMachineId() { return machineId; }
    public double getEstimatedLaborCost() { return estimatedLaborCost; }
    public double getEstimatedTransportCost() { return estimatedTransportCost; }
}