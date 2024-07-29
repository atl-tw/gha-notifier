package com.thoughtworks.gha.notifier;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.thoughtworks.gha.notifier.controller.ConfigurationController;
import com.thoughtworks.gha.notifier.gh.GitHubException;
import com.thoughtworks.gha.notifier.model.Configuration;
import com.thoughtworks.gha.notifier.model.Repository;
import com.thoughtworks.gha.notifier.model.Workflow;
import com.thoughtworks.gha.notifier.service.MonitorService;
import com.thoughtworks.gha.notifier.view.ConfigurationWindow;
import com.thoughtworks.gha.notifier.view.Toaster;
import com.thoughtworks.gha.notifier.view.Tray;
import javafx.embed.swing.JFXPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static java.util.Optional.ofNullable;


public class GitHubNotifier {

  public static final String GIT_HUB_NOTIFIER = "GitHub Actions Notifier";
  private final MonitorService monitorService = new MonitorService();
  private final Toaster toaster = new Toaster();
  private ConfigurationWindow configurationWindow = new ConfigurationWindow();
  private final Tray tray = new Tray();
  private final ConfigurationController controller;

  @SuppressWarnings("unchecked")
  private GitHubNotifier() {
    if (!monitorService.checkGitHub()) {
      JOptionPane.showMessageDialog(configurationWindow, "GitHub CLI not found. Please select the GitHub icon and locate it on your system.");
    }

    this.controller = new ConfigurationController(monitorService, configurationWindow, tray, toaster);
  }

  public static void main(String[] args) throws InterruptedException {
    // Don't show in dock
    System.setProperty("apple.awt.UIElement", "true");
    try (var configFile = GitHubNotifier.class.getResourceAsStream("/logging.properties")) {
      LogManager.getLogManager().readConfiguration(configFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    FlatLightLaf.setup();
    try {
      UIManager.setLookAndFeel(new FlatDarkLaf());
    } catch (UnsupportedLookAndFeelException e) {
      Logger.getAnonymousLogger().log(Level.WARNING, "Failed to load LAF", e);
    }
    final CountDownLatch latch = new CountDownLatch(1);
    // Initialize JavaFX for toaster.
    SwingUtilities.invokeLater(() -> {
      new JFXPanel();
      latch.countDown();
    });
    latch.await();
    SwingUtilities.invokeLater(GitHubNotifier::new);
  }



}