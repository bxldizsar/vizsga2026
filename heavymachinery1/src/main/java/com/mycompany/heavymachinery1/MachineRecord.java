package com.mycompany.heavymachinery1;

// "Nyers" rekord: a Gson ide tolti be a JSON-t (a mezonevek = JSON kulcsok).
// Wrapper tipusok (Integer, Double), hogy eszrevegyuk, ha egy mezo HIANYZIK (null marad).
// Ez meg NEM ellenorzott adat - a Validator csinal belole Machine objektumot.
public class MachineRecord {
    String id;
    String type;
    String manufacturer;
    String model;
    Integer year;
    Double purchasePrice;
    Double estimatedSellingPrice;
    String status;
    Integer hours;
}