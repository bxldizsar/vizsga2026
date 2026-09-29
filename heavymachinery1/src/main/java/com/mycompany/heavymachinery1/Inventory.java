package com.mycompany.heavymachinery1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

// BUSINESS LOGIC: minden szamitas itt van (keszletertek, kompatibilitas, bontas, profit).
public class Inventory {

    private ArrayList<Machine> machines;
    private ArrayList<Part> parts;
    private ArrayList<SalvageEstimate> estimates;

    public Inventory(ArrayList<Machine> machines, ArrayList<Part> parts, ArrayList<SalvageEstimate> estimates) {
        this.machines = machines;
        this.parts = parts;
        this.estimates = estimates;
    }

    // ---------- 1. KESZLETERTEK (polimorfizmus: Sellable lista) ----------

    // Gepek es alkatreszek EGY listaban, a kozos interface tipusaval
    public ArrayList<Sellable> getAllSellables() {
        ArrayList<Sellable> all = new ArrayList<Sellable>();
        for (Machine m : machines) all.add(m);
        for (Part p : parts) all.add(p);
        return all;
    }

    // Nem tudja, hogy gep vagy alkatresz: a konkret objektum sajat getPurchaseValue()-ja fut
    public static double totalPurchaseValue(ArrayList<Sellable> items) {
        double total = 0;
        for (Sellable s : items) total += s.getPurchaseValue();
        return total;
    }

    public static double totalSellingValue(ArrayList<Sellable> items) {
        double total = 0;
        for (Sellable s : items) total += s.getSellingValue();
        return total;
    }

    // ---------- 2. UJ / HASZNALT ALKATRESZEK SZETVALASZTASA (szures) ----------

    public ArrayList<Sellable> getPartsByCondition(String condition) {
        ArrayList<Sellable> result = new ArrayList<Sellable>();
        for (Part p : parts) {
            if (p.getCondition().equals(condition)) result.add(p);
        }
        return result;
    }

    public int countParts(String condition) {
        return getPartsByCondition(condition).size();
    }

    // ---------- 3. KOMPATIBILIS ALKATRESZEK (Compatible interface, overloading) ----------

    public ArrayList<Part> findCompatibleParts(String model) {
        ArrayList<Part> result = new ArrayList<Part>();
        for (Part p : parts) {
            Compatible c = p;                  // interface tipuskent kezeljuk
            if (c.isCompatibleWith(model)) result.add(p);
        }
        return result;
    }

    // OVERLOADING: ugyanaz a nev, gepet kap parameterkent
    public ArrayList<Part> findCompatibleParts(Machine machine) {
        return findCompatibleParts(machine.getModel());
    }

    // ---------- 4-6. BONTAS ----------

    // A gepbol kiszerelt (hasznalt) alkatreszek
    public ArrayList<UsedPart> getPartsFromMachine(String machineId) {
        ArrayList<UsedPart> result = new ArrayList<UsedPart>();
        for (Part p : parts) {
            if (p instanceof UsedPart) {
                UsedPart u = (UsedPart) p;
                if (machineId.equals(u.getSourceMachineId())) result.add(u);
            }
        }
        return result;
    }

    // Bontasi ertek = a gepbol kiszerelt alkatreszek eladasi ertekenek osszege (ar * db)
    public double getDismantlingValue(Machine machine) {
        double value = 0;
        for (UsedPart u : getPartsFromMachine(machine.getId())) {
            value += u.getSellingValue();
        }
        return value;
    }

    public SalvageEstimate findEstimate(String machineId) {
        for (SalvageEstimate e : estimates) {
            if (e.getMachineId().equals(machineId)) return e;
        }
        return null;
    }

    // Bontasi koltseg (munka + szallitas). Ha nincs becsles, 0.
    public double getSalvageCost(Machine machine) {
        SalvageEstimate e = findEstimate(machine.getId());
        if (e == null) return 0;
        return e.getTotalCost();
    }

    // Bontasi profit = bontasi ertek - gep vetelara - munkadij - szallitas
    public double getSalvageProfit(Machine machine) {
        return getDismantlingValue(machine) - machine.getPurchasePrice() - getSalvageCost(machine);
    }

    // Megeri bontani, ha a bontas tobb hasznot hoz, mint a gep egyben eladasa
    public boolean isWorthDismantling(Machine machine) {
        return getSalvageProfit(machine) > machine.getResaleProfit();
    }

    // ---------- SZURES + RENDEZES ----------

    public ArrayList<Machine> getMachinesByStatus(String status) {
        ArrayList<Machine> result = new ArrayList<Machine>();
        for (Machine m : machines) {
            if (m.getStatus().equals(status)) result.add(m);
        }
        return result;
    }

    // Bontasra szant gepek bontasi profit szerint CSOKKENO sorrendben
    public ArrayList<Machine> getDismantlingMachinesSorted() {
        ArrayList<Machine> list = getMachinesByStatus(Machine.FOR_DISMANTLING);
        Collections.sort(list, new Comparator<Machine>() {
            public int compare(Machine a, Machine b) {
                return Double.compare(getSalvageProfit(b), getSalvageProfit(a)); // b, a -> csokkeno
            }
        });
        return list;
    }

    // Eladasra szant gepek viszonteladasi profit szerint CSOKKENO sorrendben
    public ArrayList<Machine> getResaleMachinesSorted() {
        ArrayList<Machine> list = getMachinesByStatus(Machine.FOR_RESALE);
        Collections.sort(list, new Comparator<Machine>() {
            public int compare(Machine a, Machine b) {
                return Double.compare(b.getResaleProfit(), a.getResaleProfit());
            }
        });
        return list;
    }

    // ---------- CSOPORTOSITAS kategoria szerint ----------

    public ArrayList<String> getCategories() {
        ArrayList<String> categories = new ArrayList<String>();
        for (Part p : parts) {
            if (!categories.contains(p.getCategory())) categories.add(p.getCategory());
        }
        Collections.sort(categories);
        return categories;
    }

    public ArrayList<Part> getPartsInCategory(String category) {
        ArrayList<Part> result = new ArrayList<Part>();
        for (Part p : parts) {
            if (p.getCategory().equals(category)) result.add(p);
        }
        return result;
    }

    public ArrayList<Machine> getMachines() { return machines; }
    public ArrayList<Part> getParts() { return parts; }
    public ArrayList<SalvageEstimate> getEstimates() { return estimates; }
}