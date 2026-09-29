package com.mycompany.heavymachinery1;

public class Tractor extends Machine {

    public Tractor(String id, String manufacturer, String model, int year,
                   double purchasePrice, double estimatedSellingPrice, String status, int hours) {
        super(id, manufacturer, model, year, purchasePrice, estimatedSellingPrice, status, hours);
    }

    @Override
    public String getMachineType() {
        return "TRACTOR";
    }
}