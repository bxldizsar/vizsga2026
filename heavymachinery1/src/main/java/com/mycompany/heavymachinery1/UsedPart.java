package com.mycompany.heavymachinery1;

import java.util.ArrayList;

// Hasznalt alkatresz. Lehet, hogy tudjuk, melyik gepbol szereltuk ki (sourceMachineId),
// de lehet, hogy nem (pl. mashonnan vettuk) - ez nem hiba.
public class UsedPart extends Part {

    private String sourceMachineId; // null, ha ismeretlen a forras

    // CONSTRUCTOR OVERLOADING 1: ismert forrasgep
    public UsedPart(String id, String partNumber, String name, String category,
                    double purchasePrice, double sellingPrice, int quantity,
                    ArrayList<String> compatibleModels, String sourceMachineId) {
        super(id, partNumber, name, category, purchasePrice, sellingPrice, quantity, compatibleModels);
        this.sourceMachineId = sourceMachineId;
    }

    // CONSTRUCTOR OVERLOADING 2: ismeretlen forras (a JSON-ban nincs sourceMachineId)
    public UsedPart(String id, String partNumber, String name, String category,
                    double purchasePrice, double sellingPrice, int quantity,
                    ArrayList<String> compatibleModels) {
        this(id, partNumber, name, category, purchasePrice, sellingPrice, quantity, compatibleModels, null);
    }

    @Override
    public String getCondition() {
        return "USED";
    }

    public boolean hasSourceMachine() {
        return sourceMachineId != null;
    }

    public String getSourceMachineId() {
        return sourceMachineId;
    }

    @Override
    public String toString() {
        String source = hasSourceMachine() ? sourceMachineId : "unknown";
        return super.toString() + ", from: " + source;
    }
}