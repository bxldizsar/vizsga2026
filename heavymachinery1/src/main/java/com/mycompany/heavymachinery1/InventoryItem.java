package com.mycompany.heavymachinery1;

// ABSTRACT alaposztaly: minden keszleten levo dolog (gep vagy alkatresz) kozos resze.
// Azert abstract, mert "altalanos keszletelem" onmagaban nem letezik,
// mindig vagy gep, vagy alkatresz. new InventoryItem() nem irhato.
public abstract class InventoryItem implements Sellable {

    // STATIC szamlalo: az OSZTALYE, nem egy objektume.
    // Megszamolja, hany ervenyes keszletelemet hoztunk letre osszesen (a reportban kiirjuk).
    private static int createdCount = 0;

    protected String id;
    protected double purchasePrice;

    public InventoryItem(String id, double purchasePrice) {
        this.id = id;
        this.purchasePrice = purchasePrice;
        createdCount++;
    }

    // Minden leszarmazott megmondja, mi az uzleti kulcsa
    public abstract String businessKey();

    public static int getCreatedCount() {
        return createdCount;
    }

    public String getId() {
        return id;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
}