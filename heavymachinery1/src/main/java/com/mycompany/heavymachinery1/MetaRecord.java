package com.mycompany.heavymachinery1;

import java.util.ArrayList;

// A JSON "meta" resze: az uzleti szabalyok (engedelyezett ertekek, hatarok).
// A szabalyokat NEM a kodba irjuk be, hanem a data.json-bol olvassuk.
public class MetaRecord {
    int minimumValidYear;
    int maximumMachineHours;
    ArrayList<String> allowedMachineTypes;
    ArrayList<String> allowedStatuses;
    ArrayList<String> allowedPartConditions;
    ArrayList<String> allowedCategories;
}