package com.mycompany.heavymachinery1;

// ABSTRACT: minden gep kozos adatai. Tractor es Bulldozer orokol belole.
// Business key: id, mert a gepeket az id (pl. "M-002") azonositja egyertelmuen,
// es az alkatreszek sourceMachineId mezoje is erre hivatkozik.
public abstract class Machine extends InventoryItem {

    public static final String FOR_RESALE = "FOR_RESALE";
    public static final String FOR_DISMANTLING = "FOR_DISMANTLING";

    private String manufacturer;
    private String model;
    private int year;
    private double estimatedSellingPrice;
    private String status;
    private int hours;

    public Machine(String id, String manufacturer, String model, int year,
                   double purchasePrice, double estimatedSellingPrice, String status, int hours) {
        super(id, purchasePrice);
        this.manufacturer = manufacturer;
        this.model = model;
        this.year = year;
        this.estimatedSellingPrice = estimatedSellingPrice;
        this.status = status;
        this.hours = hours;
    }

    // Minden gepfajta megmondja a sajat tipusat (TRACTOR / BULLDOZER)
    public abstract String getMachineType();

    public boolean isForDismantling() {
        return FOR_DISMANTLING.equals(status);
    }

    // Mennyit keresnenk, ha egyben eladnank a gepet
    public double getResaleProfit() {
        return estimatedSellingPrice - purchasePrice;
    }

    @Override
    public double getSellingValue() {
        return estimatedSellingPrice;
    }

    @Override
    public double getPurchaseValue() {
        return purchasePrice;
    }

    @Override
    public String businessKey() {
        return id;
    }

    // Ket gep akkor egyenlo, ha ugyanaz az id-juk (business key)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Machine)) return false;
        Machine other = (Machine) o;
        return businessKey().equals(other.businessKey());
    }

    // equals-szal osszhangban: ugyanabbol a kulcsbol szamoljuk
    @Override
    public int hashCode() {
        return businessKey().hashCode();
    }

    @Override
    public String toString() {
        return id + " " + getMachineType() + " " + manufacturer + " " + model
                + " (" + year + ", " + hours + " h, " + status + ")";
    }

    public String getManufacturer() { return manufacturer; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public double getEstimatedSellingPrice() { return estimatedSellingPrice; }
    public String getStatus() { return status; }
    public int getHours() { return hours; }

    public void setEstimatedSellingPrice(double estimatedSellingPrice) {
        this.estimatedSellingPrice = estimatedSellingPrice;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
