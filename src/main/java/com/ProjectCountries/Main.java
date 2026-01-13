package com.ProjectCountries;

import com.ProjectCountries.model.Country;
import com.ProjectCountries.model.CountryService;
import com.ProjectCountries.gui.ExplorerWindow;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import java.util.List;
import javax.swing.*;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;


public class Main {
    // Main Loop
    public static void main(String[] args) {
        // Try to set a dark mode
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize LaF");
        }

        // Initialize application
        SwingUtilities.invokeLater(() -> {
            new ExplorerWindow().setVisible(true);
        });
    }
}
