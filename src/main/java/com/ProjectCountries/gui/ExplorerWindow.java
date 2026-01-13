package com.ProjectCountries.gui;

import com.ProjectCountries.model.Country;
import com.ProjectCountries.model.CountryService;
import com.ProjectCountries.service.WikiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Layout;

import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ExplorerWindow extends JFrame {
    // Logger for debugging
    private static final Logger logger = LogManager.getLogger(ExplorerWindow.class);

    // GUI model and components
    private final DefaultListModel<Country> countryModel = new DefaultListModel<>();
    private final JList<Country> countryList = new JList<>(countryModel);
    private final JPanel detailPanel = new JPanel(new BorderLayout());

    // Main window setup
    public ExplorerWindow(){
        super("Europe Explorer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        //setJMenuBar(createMenuBar());

        initUI();   // Build UI components
        loadCountries();    // Load  country data
    }

    // Set up the main interface layout
    private void initUI(){
        countryList.setCellRenderer(new CountryCellRenderer());
        countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Loading country details after select
        countryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showDetails(countryList.getSelectedValue());
            }
        });

        // Adding scroll
        JScrollPane scrollPane = new JScrollPane(countryList);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(220, getHeight()));

        add(scrollPane, BorderLayout.WEST);
        add(detailPanel, BorderLayout.CENTER);
    }

    // Load country list from API in the background
    private void loadCountries(){
        SwingWorker<List<Country>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Country> doInBackground(){
                return new CountryService().fetchCountries();
            }

            @Override
            protected void done() {
                try {
                    List<Country> countries = get();
                    countries.sort(Comparator.comparing(Country::getName));
                    for (Country c : countries) {
                        countryModel.addElement(c);
                    }
                    logger.info("Countries loaded to GUI.");
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error during loading countries to GUI", e);
                }
            }
        };
        worker.execute();
    }

    // Display details of a selected country
    private void showDetails(Country country){
        detailPanel.removeAll();
        if (country == null) return;

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Load and show flag
        JLabel imageLabel = new JLabel("Loading image...");
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(160, 100)); // Mniejsza flaga
        topPanel.add(imageLabel, BorderLayout.WEST);

        // Display country info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        JLabel infoHeader = new JLabel("Country info");
        infoHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
        infoPanel.add(infoHeader);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(new JLabel("Name: " + country.getName()));
        infoPanel.add(new JLabel("Capital: " + country.getCapital()));
        infoPanel.add(new JLabel("Population: " + country.getPopulation()));
        infoPanel.add(new JLabel("Region: " + country.getRegion()));
        infoPanel.add(new JLabel("Subregion: " + country.getSubregion()));
        infoPanel.add(new JLabel("Languages: " + country.getLanguages()));
        infoPanel.add(new JLabel("Currencies: " + country.getCurrencies()));

        topPanel.add(infoPanel, BorderLayout.CENTER);

        // Bottom panel: Wiki info + button
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Area for Wiki text
        JTextArea wikiText = new JTextArea("Loading tourist info...");
        wikiText.setLineWrap(true);
        wikiText.setWrapStyleWord(true);
        wikiText.setEditable(false);
        wikiText.setBackground(Color.darkGray);
        wikiText.setForeground(Color.WHITE); // jeśli masz ciemny motyw
        wikiText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane scroll = new JScrollPane(wikiText);
        scroll.setPreferredSize(new Dimension(500, 150));
        bottomPanel.add(scroll, BorderLayout.CENTER);

        // Save button
        JButton saveButton = new JButton("Save to file");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        // File saving
        saveButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(country.getName() + ".txt"));
            int option = fileChooser.showSaveDialog(detailPanel);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    String content = String.format(
                            "Name: %s\nCapital: %s\nPopulation: %d\nRegion: %s\nSubregion: %s\nLanguages: %s\nCurrencies: %s\n\n%s",
                            country.getName(),
                            country.getCapital(),
                            country.getPopulation(),
                            country.getRegion(),
                            country.getSubregion(),
                            country.getLanguages(),
                            country.getCurrencies(),
                            wikiText.getText()
                    );
                    writer.write(content);
                    JOptionPane.showMessageDialog(detailPanel, "Saved to file:\n" + file.getAbsolutePath());
                    logger.info("Info about country saved to file: " + file.getAbsolutePath());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(detailPanel, "Error during saving file", "Error", JOptionPane.ERROR_MESSAGE);
                    logger.error("Error during saving file", ex);
                }
            }
        });

        // Add to main panel
        detailPanel.setLayout(new BorderLayout());
        detailPanel.add(topPanel, BorderLayout.NORTH);
        detailPanel.add(bottomPanel, BorderLayout.CENTER);
        //detailPanel.add(bottomPanel, BorderLayout.SOUTH);
        detailPanel.revalidate();
        detailPanel.repaint();

        // Get flag from the web
        SwingWorker<ImageIcon, Void> imageWorker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                String imageUrl = new WikiService().fetchCountryImageURL(country.getName());
                if (imageUrl != null) {
                    try {
                        var img = ImageIO.read(new URL(imageUrl));
                        Image scaled = img.getScaledInstance(160, 100, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaled);
                    } catch (IOException e) {
                        logger.warn("Error loading image for " + country.getName(), e);
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        imageLabel.setText("");
                        imageLabel.setIcon(icon);
                    } else {
                        imageLabel.setText("No image.");
                    }
                } catch (Exception e) {
                    imageLabel.setText("Error loading image.");
                }
            }
        };
        imageWorker.execute();

        // Get text from Wikipedia
        SwingWorker<String, Void> wikiWorker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return new WikiService().fetchIntroParagraph(country.getName());
            }

            @Override
            protected void done() {
                try {
                    String countryInfo = get();
                    String formatted = "Info about country: " + country.getName() + "\n\n" + countryInfo;
                    wikiText.setText(formatted);
                } catch (Exception e) {
                    wikiText.setText("Error loading data from internet.");
                    logger.error("Error fetching Wikipedia info for: " + country.getName(), e);
                }
            }
        };
        wikiWorker.execute();
    }

    // Render for the country list
    private static class CountryCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(4, 4, 4, 4)
            ));
            if (isSelected) {
                // label.setBackground(new Color(200, 220, 255));
                label.setBackground(new Color(60, 80, 110));
            }

            // Set text and flag icon
            if (value instanceof Country) {
                Country country = (Country) value;
                label.setText(country.getName());

                try {
                    URL url = new URL(country.getFlagURL());
                    BufferedImage img = ImageIO.read(url);
                    Image scaled = img.getScaledInstance(32, 20, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(scaled));
                } catch (Exception e) {
                    logger.warn("Error loading flag for " + country.getName(), e);
                }
            }
            return label;
        }
    }
}
