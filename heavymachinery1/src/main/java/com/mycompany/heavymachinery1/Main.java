package com.mycompany.heavymachinery1;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // 1. beolvasas + validacio
        final DataLoader loader = new DataLoader();
        boolean ok = loader.load("data.json");
        if (!ok) {
            System.out.println("Data could not be loaded, program stops."); 
            return;
        }

        // 2. uzleti logika
        final Inventory inventory = new Inventory(loader.getMachines(), loader.getParts(), loader.getEstimates());

        // 3. fajlok irasa
        ReportWriter.writeToFile("report.txt", ReportWriter.buildReport(inventory, loader));
        ReportWriter.writeToFile("invalid_items.txt", ReportWriter.buildInvalidReport(loader));

        System.out.println("Valid machines: " + loader.getMachines().size()
                + ", valid parts: " + loader.getParts().size()
                + ", rejected: " + loader.getRejected().size()
                + ", warnings: " + loader.getWarnings().size());

        // 4. GUI (ha nincs kepernyo, pl. szerveren, kihagyjuk)
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame(inventory, loader).setVisible(true);
            }
        });
    }
}