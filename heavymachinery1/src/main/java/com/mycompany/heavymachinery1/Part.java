package com.mycompany.heavymachinery1;

import java.util.ArrayList;

// ABSTRACT: minden alkatresz kozos adatai. NewPart es UsedPart orokol belole.
// Business key: partNumber, mert ez azonositja egyertelmuen az alkatreszt
// (az id csak a sor sorszama, a P-002 es P-092 kulonbozo id-val ugyanaz a cikkszam).
public abstract class Part extends InventoryItem implements Compatible {

    private String partNumber;
    private String name;
    private String category;
    private double sellingPrice;
    private int quantity;
    private ArrayList<String> compatibleModels;

    public Part(String id, String partNumber, String name, String category,
                double purchasePrice, double sellingPrice, int quantity,
                ArrayList<String> compatibleModels) {
        super(id, purchasePrice);
        this.partNumber = partNumber;
        this.name = name;
        this.category = category;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        this.compatibleModels = compatibleModels;
    }

    // NEW vagy USED - a leszarmazott mondja meg
    public abstract String getCondition();

    @Override
    public boolean isCompatibleWith(String model) {
        return compatibleModels.contains(model);
    }

    // Keszlet eladasi erteke: egysegar * darabszam
    @Override
    public double getSellingValue() {
        return sellingPrice * quantity;
    }

    // OVERLOADING: ugyanaz a nev, mas parameter. Kedvezmennyel (szazalek) szamolt ertek.
    public double getSellingValue(double discountPercent) {
        return getSellingValue() * (100 - discountPercent) / 100;
    }

    @Override
    public double getPurchaseValue() {
        return purchasePrice * quantity;
    }

    public boolean isOutOfStock() {
        return quantity == 0;
    }

    public boolean isSoldBelowPurchasePrice() {
        return sellingPrice < purchasePrice;
    }

    @Override
    public String businessKey() {
        return partNumber;
    }

    // Ket alkatresz akkor egyenlo, ha ugyanaz a cikkszamuk (partNumber)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Part)) return false;
        Part other = (Part) o;
        return businessKey().equals(other.businessKey());
    }

    @Override
    public int hashCode() {
        return businessKey().hashCode();
    }

    @Override
    public String toString() {
        return id + " " + partNumber + " " + name + " [" + getCondition() + ", " + category
                + "] qty=" + quantity + ", sell=" + sellingPrice + " EUR";
    }

    public String getPartNumber() { return partNumber; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getSellingPrice() { return sellingPrice; }
    public int getQuantity() { return quantity; }
    public ArrayList<String> getCompatibleModels() { return compatibleModels; }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}