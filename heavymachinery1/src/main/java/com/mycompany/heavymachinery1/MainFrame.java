package com.mycompany.heavymachinery1;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

// Egyszeru Swing GUI.
// - Condition szuro: a tablazat csak az ALL / NEW / USED alkatreszeket mutatja
// - Machine valaszto + "Compatible parts" gomb: a gephez illo alkatreszek (+ bontasi adatok)
// - "Show report" / "Show invalid items" gombok: a fajlokba irt szoveget mutatjak
public class MainFrame extends JFrame {

    private Inventory inventory;
    private DataLoader loader;

    private DefaultTableModel tableModel;
    private JTextArea textArea;
    private JComboBox conditionBox;   // Java 6: a JComboBox meg nem generikus
    private JComboBox machineBox;

    public MainFrame(Inventory inventory, DataLoader loader) {
        super("Heavy Machinery Parts & Dismantling");
        this.inventory = inventory;
        this.loader = loader;

        // ---- felso panel: vezerlok ----
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        conditionBox = new JComboBox();
        conditionBox.addItem("ALL");
        conditionBox.addItem("NEW");
        conditionBox.addItem("USED");
        top.add(new JLabel("Part condition:"));
        top.add(conditionBox);

        machineBox = new JComboBox();
        for (Machine m : inventory.getMachines()) {
            machineBox.addItem(m.getId() + " - " + m.getModel());
        }
        top.add(new JLabel("Machine:"));
        top.add(machineBox);

        JButton compatibleButton = new JButton("Compatible parts");
        JButton reportButton = new JButton("Show report");
        JButton invalidButton = new JButton("Show invalid items");
        top.add(compatibleButton);
        top.add(reportButton);
        top.add(invalidButton);

        // ---- tablazat az alkatreszeknek ----
        String[] columns = {"ID", "Part number", "Name", "Category", "Condition", "Qty", "Selling price", "Stock value"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        // ---- szovegdoboz az eredmenyeknek ----
        textArea = new JTextArea();
        textArea.setEditable(false);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(table), new JScrollPane(textArea));
        split.setDividerLocation(280);

        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        // ---- esemenykezelok (anonim osztaly, Java 6) ----
        conditionBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fillTable((String) conditionBox.getSelectedItem());
            }
        });

        compatibleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCompatibleParts();
            }
        });

        reportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setText(ReportWriter.buildReport(MainFrame.this.inventory, MainFrame.this.loader));
                textArea.setCaretPosition(0);
            }
        });

        invalidButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setText(ReportWriter.buildInvalidReport(MainFrame.this.loader));
                textArea.setCaretPosition(0);
            }
        });

        fillTable("ALL");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // A tablazat feltoltese a kivalasztott allapot szerint (szures)
    private void fillTable(String condition) {
        tableModel.setRowCount(0);
        for (Part p : inventory.getParts()) {
            if (condition.equals("ALL") || p.getCondition().equals(condition)) {
                Object[] row = {p.getId(), p.getPartNumber(), p.getName(), p.getCategory(),
                        p.getCondition(), p.getQuantity(), p.getSellingPrice(), p.getSellingValue()};
                tableModel.addRow(row);
            }
        }
    }

    private void showCompatibleParts() {
        int index = machineBox.getSelectedIndex();
        if (index < 0) {
            textArea.setText("No machine selected.");
            return;
        }
        Machine m = inventory.getMachines().get(index);
        StringBuilder sb = new StringBuilder();
        sb.append(m).append("\n\nCompatible parts:\n");
        ArrayList<Part> list = inventory.findCompatibleParts(m);
        if (list.size() == 0) sb.append("  none\n");
        for (Part p : list) sb.append("  ").append(p).append("\n");

        if (m.isForDismantling()) {
            sb.append("\nDismantling value: ").append(inventory.getDismantlingValue(m)).append(" EUR\n");
            sb.append("Salvage profit: ").append(inventory.getSalvageProfit(m)).append(" EUR\n");
            sb.append("Resale profit: ").append(m.getResaleProfit()).append(" EUR\n");
            sb.append("Worth dismantling: ").append(inventory.isWorthDismantling(m) ? "YES" : "NO").append("\n");
        } else {
            sb.append("\nResale profit: ").append(m.getResaleProfit()).append(" EUR\n");
        }
        textArea.setText(sb.toString());
        textArea.setCaretPosition(0);
    }
}