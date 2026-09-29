package com.mycompany.heavymachinery1;

import java.util.ArrayList;

// Nyers alkatresz rekord (Gson tolti ki). Ha a compatibleModels nem lista,
// hanem szoveg (pl. P-093: "D6T"), a Gson JsonSyntaxException-t dob -> parsing hiba.
public class PartRecord {
    String id;
    String partNumber;
    String name;
    String category;
    String condition;
    Double purchasePrice;
    Double sellingPrice;
    Integer quantity;
    String sourceMachineId;
    ArrayList<String> compatibleModels;
}