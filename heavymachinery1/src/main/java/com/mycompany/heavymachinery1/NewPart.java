package com.mycompany.heavymachinery1;

import java.util.ArrayList;

public class NewPart extends Part {

    public NewPart(String id, String partNumber, String name, String category,
                   double purchasePrice, double sellingPrice, int quantity,
                   ArrayList<String> compatibleModels) {
        super(id, partNumber, name, category, purchasePrice, sellingPrice, quantity, compatibleModels);
    }

    @Override
    public String getCondition() {
        return "NEW";
    }
}