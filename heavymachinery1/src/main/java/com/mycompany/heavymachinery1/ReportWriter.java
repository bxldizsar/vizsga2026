package com.mycompany.heavymachinery1;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

// A report.txt es az invalid_items.txt eloallitasa.
public class ReportWriter {

    // A teljes riport szovegkent (a GUI is ezt mutatja)
    public static String buildReport(Inventory inv, DataLoader loader) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== HEAVY MACHINERY INVENTORY REPORT =====\n");
        sb.append("Company: ").append(loader.getCompanyName()).append("\n\n");

        sb.append("--- 1. SUMMARY ---\n");
        sb.append("Valid machines: ").append(inv.getMachines().size()).append("\n");
        sb.append("Valid parts: ").append(inv.getParts().size()).append("\n");
        sb.append("Valid salvage estimates: ").append(inv.getEstimates().size()).append("\n");
        sb.append("Rejected records: ").append(loader.getRejected().size()).append("\n");
        sb.append("Warnings: ").append(loader.getWarnings().size()).append("\n");
        sb.append("Inventory objects created (static counter): ").append(InventoryItem.getCreatedCount()).append("\n\n");

        sb.append("--- 2. INVENTORY VALUE (EUR) ---\n");
        ArrayList<Sellable> all = inv.getAllSellables();
        ArrayList<Sellable> machinesOnly = new ArrayList<Sellable>(inv.getMachines());
        ArrayList<Sellable> partsOnly = new ArrayList<Sellable>(inv.getParts());
        sb.append("Machines - purchase value: ").append(money(Inventory.totalPurchaseValue(machinesOnly)))
          .append(", estimated selling value: ").append(money(Inventory.totalSellingValue(machinesOnly))).append("\n");
        sb.append("Parts    - purchase value: ").append(money(Inventory.totalPurchaseValue(partsOnly)))
          .append(", selling value: ").append(money(Inventory.totalSellingValue(partsOnly))).append("\n");
        sb.append("TOTAL    - purchase value: ").append(money(Inventory.totalPurchaseValue(all)))
          .append(", selling value: ").append(money(Inventory.totalSellingValue(all))).append("\n\n");

        sb.append("--- 3. NEW vs USED PARTS ---\n");
        String[] conditions = {"NEW", "USED"};
        for (int i = 0; i < conditions.length; i++) {
            ArrayList<Sellable> list = inv.getPartsByCondition(conditions[i]);
            sb.append(conditions[i]).append(": ").append(list.size()).append(" part types, stock selling value ")
              .append(money(Inventory.totalSellingValue(list))).append("\n");
        }
        sb.append("\n");

        sb.append("--- 4. PARTS GROUPED BY CATEGORY ---\n");
        for (String category : inv.getCategories()) {
            ArrayList<Part> list = inv.getPartsInCategory(category);
            int pieces = 0;
            double value = 0;
            for (Part p : list) {
                pieces += p.getQuantity();
                value += p.getSellingValue();
            }
            sb.append(category).append(": ").append(list.size()).append(" part types, ")
              .append(pieces).append(" pieces, value ").append(money(value)).append("\n");
        }
        sb.append("\n");

        sb.append("--- 5. MACHINES FOR RESALE (sorted by resale profit, descending) ---\n");
        for (Machine m : inv.getResaleMachinesSorted()) {
            sb.append(m).append(" -> resale profit ").append(money(m.getResaleProfit())).append("\n");
        }
        sb.append("\n");

        sb.append("--- 6. DISMANTLING ANALYSIS (sorted by salvage profit, descending) ---\n");
        sb.append("Salvage profit = dismantling value - machine purchase price - labor - transport\n");
        sb.append("Worth dismantling = salvage profit > resale profit (selling the machine as a whole)\n\n");
        for (Machine m : inv.getDismantlingMachinesSorted()) {
            sb.append(m).append("\n");
            for (UsedPart u : inv.getPartsFromMachine(m.getId())) {
                sb.append("     part: ").append(u.getId()).append(" ").append(u.getName())
                  .append(" x").append(u.getQuantity()).append(" = ").append(money(u.getSellingValue())).append("\n");
            }
            sb.append("   dismantling value: ").append(money(inv.getDismantlingValue(m))).append("\n");
            sb.append("   purchase price:    ").append(money(m.getPurchasePrice())).append("\n");
            sb.append("   labor + transport: ").append(money(inv.getSalvageCost(m))).append("\n");
            sb.append("   SALVAGE PROFIT:    ").append(money(inv.getSalvageProfit(m))).append("\n");
            sb.append("   resale profit:     ").append(money(m.getResaleProfit())).append("\n");
            sb.append("   WORTH DISMANTLING: ").append(inv.isWorthDismantling(m) ? "YES" : "NO").append("\n\n");
        }

        sb.append("--- 7. COMPATIBLE PARTS PER MACHINE ---\n");
        for (Machine m : inv.getMachines()) {
            ArrayList<Part> compatible = inv.findCompatibleParts(m);
            sb.append(m.getId()).append(" ").append(m.getModel()).append(": ");
            if (compatible.size() == 0) {
                sb.append("no compatible parts");
            }
            for (int i = 0; i < compatible.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(compatible.get(i).getId());
            }
            sb.append("\n");
        }
        sb.append("\n");

        sb.append("--- 8. OUT OF STOCK PARTS ---\n");
        for (Part p : inv.getParts()) {
            if (p.isOutOfStock()) sb.append(p).append("\n");
        }
        sb.append("\n--- 9. PARTS SOLD BELOW PURCHASE PRICE ---\n");
        for (Part p : inv.getParts()) {
            if (p.isSoldBelowPurchasePrice()) sb.append(p).append(" (purchase ").append(p.getPurchasePrice()).append(")\n");
        }
        return sb.toString();
    }

    public static String buildInvalidReport(DataLoader loader) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== REJECTED RECORDS (").append(loader.getRejected().size()).append(") =====\n\n");
        for (String s : loader.getRejected()) sb.append(s).append("\n");
        sb.append("===== WARNINGS - accepted but suspicious (").append(loader.getWarnings().size()).append(") =====\n\n");
        for (String s : loader.getWarnings()) sb.append(s).append("\n");
        return sb.toString();
    }

    // Fajlba iras FileWriter-rel, try-catch-finally
    public static void writeToFile(String fileName, String text) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(fileName));
            pw.print(text);
            System.out.println("Written: " + fileName);
        } catch (IOException e) {
            System.out.println("Could not write " + fileName + ": " + e.getMessage());
        } finally {
            if (pw != null) pw.close(); // close nelkul ures maradhat a fajl
        }
    }

    private static String money(double value) {
        return String.format("%.2f EUR", value);
    }
}