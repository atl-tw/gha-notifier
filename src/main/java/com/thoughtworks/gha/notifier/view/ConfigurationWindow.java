package com.thoughtworks.gha.notifier.view;

import lombok.Getter;

import javax.swing.*;

import static com.thoughtworks.gha.notifier.GitHubNotifier.GIT_HUB_NOTIFIER;

public class ConfigurationWindow extends JFrame {

  @Getter
  private MainForm mainForm = new MainForm();

  public ConfigurationWindow(){
    super(GIT_HUB_NOTIFIER);
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    this.setSize(1024, 600);
    this.setLocationRelativeTo(null);
    this.setContentPane(mainForm);
    this.setAlwaysOnTop(true);
    mainForm.getWorkflowConfig().setVisible(false);
    mainForm.getDetailsPanel().setVisible(false);
  }
}
