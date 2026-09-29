package com.mycompany.heavymachinery1;

import java.util.ArrayList;

// VALIDATION: a mar beolvasott (parse-olt) rekordokat ellenorizzuk az uzleti szabalyok szerint.
// Ha valami rossz, DomainValidationException-t dobunk, benne az OSSZES talalt hibaval.
public class Validator {

    private MetaRecord meta;
    private int inventoryYear; // a leltar eve (2026) - ennel kesobbi gyartasi ev nem lehet

    public Validator(MetaRecord meta, int inventoryYear) {
        this.meta = meta;
        this.inventoryYear = inventoryYear;
    }

    // ---------------- GEP ----------------
    public Machine createMachine(MachineRecord r) throws DomainValidationException {
        String errors = "";

        if (isEmpty(r.id)) errors += "missing id; ";
        if (isEmpty(r.type)) {
            errors += "missing type; ";
        } else if (!meta.allowedMachineTypes.contains(r.type)) {
            errors += "unsupported machine type '" + r.type + "'; ";
        }
        if (isEmpty(r.manufacturer)) errors += "missing manufacturer; ";
        if (isEmpty(r.model)) errors += "missing model; ";

        if (r.year == null) {
            errors += "missing year; ";
        } else if (r.year < meta.minimumValidYear || r.year > inventoryYear) {
            errors += "invalid year " + r.year + " (allowed " + meta.minimumValidYear + "-" + inventoryYear + "); ";
        }
        if (r.purchasePrice == null || r.purchasePrice < 0) {
            errors += "invalid purchasePrice " + r.purchasePrice + "; ";
        }
        if (r.estimatedSellingPrice == null || r.estimatedSellingPrice < 0) {
            errors += "invalid estimatedSellingPrice " + r.estimatedSellingPrice + "; ";
        }
        if (isEmpty(r.status) || !meta.allowedStatuses.contains(r.status)) {
            errors += "invalid status '" + r.status + "'; ";
        }
        if (r.hours == null || r.hours < 0 || r.hours > meta.maximumMachineHours) {
            errors += "invalid hours " + r.hours + " (allowed 0-" + meta.maximumMachineHours + "); ";
        }

        if (errors.length() > 0) {
            throw new DomainValidationException(errors);
        }

        // Az ellenorzott adatbol a tipusnak megfelelo objektum keszul
        if (r.type.equals("TRACTOR")) {
            return new Tractor(r.id, r.manufacturer, r.model, r.year, r.purchasePrice,
                    r.estimatedSellingPrice, r.status, r.hours);
        } else {
            return new Bulldozer(r.id, r.manufacturer, r.model, r.year, r.purchasePrice,
                    r.estimatedSellingPrice, r.status, r.hours);
        }
    }

    // ---------------- ALKATRESZ ----------------
    public Part createPart(PartRecord r) throws DomainValidationException {
        String errors = "";

        if (isEmpty(r.id)) errors += "missing id; ";
        if (isEmpty(r.partNumber)) errors += "missing partNumber; ";
        if (isEmpty(r.name)) errors += "missing name; ";
        if (isEmpty(r.category) || !meta.allowedCategories.contains(r.category)) {
            errors += "invalid category '" + r.category + "'; ";
        }
        if (isEmpty(r.condition) || !meta.allowedPartConditions.contains(r.condition)) {
            errors += "invalid condition '" + r.condition + "'; ";
        }
        if (r.purchasePrice == null || r.purchasePrice < 0) {
            errors += "invalid purchasePrice " + r.purchasePrice + "; ";
        }
        if (r.sellingPrice == null || r.sellingPrice < 0) {
            errors += "invalid sellingPrice " + r.sellingPrice + "; ";
        }
        // 0 darab ervenyes (elfogyott), negativ nem
        if (r.quantity == null || r.quantity < 0) {
            errors += "invalid quantity " + r.quantity + "; ";
        }
        if (r.compatibleModels == null) {
            errors += "missing compatibleModels; ";
        }

        if (errors.length() > 0) {
            throw new DomainValidationException(errors);
        }

        if (r.condition.equals("NEW")) {
            return new NewPart(r.id, r.partNumber, r.name, r.category, r.purchasePrice,
                    r.sellingPrice, r.quantity, r.compatibleModels);
        } else if (r.sourceMachineId != null) {
            return new UsedPart(r.id, r.partNumber, r.name, r.category, r.purchasePrice,
                    r.sellingPrice, r.quantity, r.compatibleModels, r.sourceMachineId);
        } else {
            return new UsedPart(r.id, r.partNumber, r.name, r.category, r.purchasePrice,
                    r.sellingPrice, r.quantity, r.compatibleModels);
        }
    }

    // ---------------- BONTASI BECSLES ----------------
    public void validateEstimate(SalvageEstimate e, ArrayList<Machine> machines) throws DomainValidationException {
        Machine m = findMachine(machines, e.getMachineId());
        if (m == null) {
            throw new DomainValidationException("machine " + e.getMachineId() + " does not exist");
        }
        if (!m.isForDismantling()) {
            throw new DomainValidationException("machine " + e.getMachineId() + " is not FOR_DISMANTLING");
        }
        if (e.getEstimatedLaborCost() < 0 || e.getEstimatedTransportCost() < 0) {
            throw new DomainValidationException("negative cost");
        }
    }

    public static Machine findMachine(ArrayList<Machine> machines, String id) {
        for (Machine m : machines) {
            if (m.getId().equals(id)) {
                return m;
            }
        }
        return null;
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }
}