package com.ProjectCountries.view;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private final JList<String> countryList;
    private final JLabel flagLabel;
    private final JTextArea countryDetails;

    public MainWindow() {
        setTitle("Europe Explorer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel listy krajów
        countryList = new JList<>();
        JScrollPane listScroll = new JScrollPane(countryList);
        listScroll.setPreferredSize(new Dimension(200, 0));
        add(listScroll, BorderLayout.WEST);

        // Panel szczegółów
        JPanel rightPanel = new JPanel(new BorderLayout());

        flagLabel = new JLabel("", SwingConstants.CENTER);
        flagLabel.setPreferredSize(new Dimension(100, 100));
        rightPanel.add(flagLabel, BorderLayout.NORTH);

        countryDetails = new JTextArea();
        countryDetails.setEditable(false);
        rightPanel.add(new JScrollPane(countryDetails), BorderLayout.CENTER);

        add(rightPanel, BorderLayout.CENTER);
    }

    public void showGUI() {
        setVisible(true);
    }

    public JList<String> getCountryList() {
        return countryList;
    }

    public JLabel getFlagLabel() {
        return flagLabel;
    }

    public JTextArea getCountryDetails() {
        return countryDetails;
    }
}
