package com.mycompany.heavymachinery1;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

// A data.json beolvasasa. HAROM szint:
// 1. szintaktikailag jo JSON-e?           (JsonParser)       -> ha nem, a fajl hasznalhatatlan
// 2. beleillik-e a Java osztalyba?         (gson.fromJson)    -> JsonSyntaxException = PARSING hiba
// 3. megfelel-e az uzleti szabalyoknak?    (Validator)        -> DomainValidationException = VALIDATION hiba
// Minden rekord kulon try-ban van, igy egy hibas rekord nem allitja le a programot.
public class DataLoader {

    private Gson gson = new Gson();

    private ArrayList<Machine> machines = new ArrayList<Machine>();
    private ArrayList<Part> parts = new ArrayList<Part>();
    private ArrayList<SalvageEstimate> estimates = new ArrayList<SalvageEstimate>();
    private ArrayList<String> rejected = new ArrayList<String>(); // elutasitott rekordok
    private ArrayList<String> warnings = new ArrayList<String>(); // elfogadott, de gyanus rekordok
    private String companyName = "";

    public boolean load(String fileName) {
        FileReader reader = null;
        try {
            reader = new FileReader(fileName);
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            companyName = root.getAsJsonObject("company").get("name").getAsString();
            String inventoryDate = root.get("inventoryDate").getAsString();       // "2026-09-28"
            int inventoryYear = Integer.parseInt(inventoryDate.substring(0, 4));  // 2026

            MetaRecord meta = gson.fromJson(root.get("meta"), MetaRecord.class);
            Validator validator = new Validator(meta, inventoryYear);

            // A sorrend fontos: eloszor a gepek, mert az alkatreszek es becslesek rajuk hivatkoznak
            loadMachines(root.getAsJsonArray("machines"), validator);
            loadParts(root.getAsJsonArray("parts"), validator);
            loadEstimates(root.getAsJsonArray("salvageEstimates"), validator);
            return true;

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        } catch (JsonParseException e) {
            // a teljes fajl nem ervenyes JSON (pl. hianyzo zarojel)
            System.out.println("The file is not valid JSON: " + e.getMessage());
        } catch (IllegalStateException e) {
            // pl. a "machines" nem lista vagy nem objektum a gyoker
            System.out.println("Unexpected JSON structure: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid inventoryDate: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Could not close file: " + e.getMessage());
                }
            }
        }
        return false;
    }

    private void loadMachines(JsonArray array, Validator validator) {
        for (int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            String recordId = readId(element, i);
            try {
                MachineRecord record = gson.fromJson(element, MachineRecord.class);   // parsing
                Machine machine = validator.createMachine(record);                    // validation

                if (machines.contains(machine)) {                                     // equals (id)
                    throw new DomainValidationException("duplicate machine id " + machine.getId());
                }
                machines.add(machine);

                if (machine.getEstimatedSellingPrice() < machine.getPurchasePrice()) {
                    addWarning("machine " + recordId, "estimated selling price is lower than purchase price");
                }
                if (machine.getYear() < 1990) {
                    addWarning("machine " + recordId, "very old machine (" + machine.getYear() + "), but still valid");
                }
            } catch (JsonSyntaxException e) {
                addRejected("machine " + recordId, "PARSING: wrong data type - " + e.getMessage());
            } catch (DomainValidationException e) {
                addRejected("machine " + recordId, "VALIDATION: " + e.getMessage());
            }
        }
    }

    private void loadParts(JsonArray array, Validator validator) {
        for (int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            String recordId = readId(element, i);
            try {
                PartRecord record = gson.fromJson(element, PartRecord.class);         // parsing
                Part part = validator.createPart(record);                             // validation

                // Duplikalt cikkszam: az equals a partNumber-t hasonlitja
                if (parts.contains(part)) {
                    Part first = parts.get(parts.indexOf(part));
                    throw new DomainValidationException("duplicate partNumber " + part.getPartNumber()
                            + " (already used by " + first.getId() + ")");
                }
                parts.add(part);

                checkPartWarnings(part, recordId);
            } catch (JsonSyntaxException e) {
                addRejected("part " + recordId, "PARSING: wrong data type - " + e.getMessage());
            } catch (DomainValidationException e) {
                addRejected("part " + recordId, "VALIDATION: " + e.getMessage());
            }
        }
    }

    // Szokatlan, de nem feltetlenul hibas adatok: elfogadjuk, de figyelmeztetunk
    private void checkPartWarnings(Part part, String recordId) {
        if (part instanceof UsedPart) {
            UsedPart used = (UsedPart) part;
            if (used.hasSourceMachine() && Validator.findMachine(machines, used.getSourceMachineId()) == null) {
                // Hibas hivatkozas: a part megmarad keszleten, de bontasi szamitasba nem kerul be
                addWarning("part " + recordId, "INVALID REFERENCE: source machine "
                        + used.getSourceMachineId() + " does not exist (part kept, excluded from dismantling value)");
            }
        }
        if (part.isSoldBelowPurchasePrice()) {
            addWarning("part " + recordId, "selling price (" + part.getSellingPrice()
                    + ") is lower than purchase price (" + part.getPurchasePrice() + ")");
        }
        if (part.isOutOfStock()) {
            addWarning("part " + recordId, "out of stock (quantity 0), still valid");
        }
        if (part.getCompatibleModels().size() == 0) {
            addWarning("part " + recordId, "no compatible models listed");
        }
    }

    private void loadEstimates(JsonArray array, Validator validator) {
        for (int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            try {
                JsonObject o = element.getAsJsonObject();
                SalvageEstimate e = new SalvageEstimate(o.get("machineId").getAsString(),
                        o.get("estimatedLaborCost").getAsDouble(),
                        o.get("estimatedTransportCost").getAsDouble());
                validator.validateEstimate(e, machines);
                estimates.add(e);
            } catch (DomainValidationException e) {
                addRejected("salvageEstimate #" + (i + 1), "VALIDATION: " + e.getMessage());
            } catch (RuntimeException e) {
                // hianyzo mezo (NullPointerException) vagy rossz tipus (NumberFormatException, IllegalStateException)
                addRejected("salvageEstimate #" + (i + 1), "PARSING: " + e);
            }
        }
    }

    // Az id kiolvasasa a hibauzenethez (ha nincs, a sorszamot hasznaljuk)
    private String readId(JsonElement element, int index) {
        try {
            return element.getAsJsonObject().get("id").getAsString();
        } catch (RuntimeException e) {
            return "#" + (index + 1);
        }
    }

    private void addRejected(String record, String reason) {
        rejected.add("Record: " + record + "\nReason: " + reason + "\nAction: REJECTED\n");
    }

    private void addWarning(String record, String reason) {
        warnings.add("Record: " + record + "\nReason: " + reason + "\nAction: ACCEPTED WITH WARNING\n");
    }

    public ArrayList<Machine> getMachines() { return machines; }
    public ArrayList<Part> getParts() { return parts; }
    public ArrayList<SalvageEstimate> getEstimates() { return estimates; }
    public ArrayList<String> getRejected() { return rejected; }
    public ArrayList<String> getWarnings() { return warnings; }
    public String getCompanyName() { return companyName; }
}
