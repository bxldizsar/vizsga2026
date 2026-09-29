package com.mycompany.heavymachinery1;

// Interface: valami, ami egy gepmodellhez illeszkedhet.
// Az alkatreszek implementaljak; az Inventory.findCompatibleParts() ezt hasznalja.
public interface Compatible {

    boolean isCompatibleWith(String model);
}