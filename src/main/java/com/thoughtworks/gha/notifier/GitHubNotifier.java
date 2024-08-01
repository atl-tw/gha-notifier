package com.thoughtworks.gha.notifier;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.thoughtworks.gha.notifier.controller.ConfigurationController;
import com.thoughtworks.gha.notifier.service.MonitorService;
import com.thoughtworks.gha.notifier.view.impl.ConfigurationWindowImpl;
import com.thoughtworks.gha.notifier.view.impl.LightDark;
import com.thoughtworks.gha.notifier.view.impl.ToasterImpl;
import com.thoughtworks.gha.notifier.view.impl.TrayImpl;
import javafx.embed.swing.JFXPanel;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class GitHubNotifier {

  public static final String GIT_HUB_NOTIFIER = "GitHub Actions Notifier";
  @SuppressWarnings("FieldCanBeLocal")
  private final ConfigurationController controller;

  private GitHubNotifier() {
    MonitorService monitorService = new MonitorService();
    ConfigurationWindowImpl configurationWindow = new ConfigurationWindowImpl();
    if (!monitorService.checkGitHub()) {
      configurationWindow.showMessageDialog("GitHub CLI not found. Please select the GitHub icon and locate it on your system.");
    }
    ToasterImpl toaster = new ToasterImpl();
    TrayImpl tray = new TrayImpl();
    this.controller = new ConfigurationController(monitorService, configurationWindow, tray, toaster);
  }

  public static void main(String[] args) throws InterruptedException {
    // Don't show in dock
    System.setProperty("apple.awt.UIElement", "true");
    System.setProperty("apple.awt.application.appearance", "system");
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
    LightDark.init();
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