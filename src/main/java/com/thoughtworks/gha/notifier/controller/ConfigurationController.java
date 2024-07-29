package com.thoughtworks.gha.notifier.controller;

import com.thoughtworks.gha.notifier.gh.GitHubException;
import com.thoughtworks.gha.notifier.model.Configuration;
import com.thoughtworks.gha.notifier.model.Repository;
import com.thoughtworks.gha.notifier.model.Workflow;
import com.thoughtworks.gha.notifier.service.MonitorService;
import com.thoughtworks.gha.notifier.view.ConfigurationWindow;
import com.thoughtworks.gha.notifier.view.Toaster;
import com.thoughtworks.gha.notifier.view.Tray;

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
import java.util.Set;

import static java.util.Optional.ofNullable;

public class ConfigurationController {

  private MonitorService monitorService;
  private ConfigurationWindow configurationWindow;
  private final Tray tray;
  private final Toaster toaster;

  private java.util.List<Workflow> selectedWorkflows;
  private final ActionListener notifyCheckedListener = e1 -> {
    if (selectedWorkflows == null) {
      return;
    }
    selectedWorkflows.forEach(workflow -> {
      if (configurationWindow.getMainForm().getNotify().isSelected()) {
        monitorService.addWorkflowToNotify(workflow);
      } else {
        monitorService.removeWorkflowToNotify(workflow);
      }
    });
  };
  private final ActionListener mainBranchListener = e1 -> {
    if (selectedWorkflows == null) {
      return;
    }
    selectedWorkflows.forEach(workflow -> {
      workflow.setMainBranch(configurationWindow.getMainForm().getMainBranch().getText());
      monitorService.updateWorkflow(workflow);
    });
  };

  public ConfigurationController(MonitorService configurationService, ConfigurationWindow configurationWindow, Tray tray, Toaster toaster) {
    this.monitorService = configurationService;
    this.configurationWindow = configurationWindow;
    this.tray = tray;
    this.toaster = toaster;
    configurationWindow.getMainForm().getAdd().addActionListener(this::onAddRepository);
    configurationWindow.getMainForm().getRemove().addActionListener(this::onRemoveRepositories);
    configurationWindow.getMainForm().getGh().addActionListener(this::onGitHubSelect);
    configurationWindow.getMainForm().getRepositories().addListSelectionListener(this::onRepositoriesSelected);
    configurationWindow.getMainForm().getWorkflows().addListSelectionListener(this::onWorkflowSelected);
    configurationWindow.getMainForm().getRepositories().setListData(monitorService.getRepositories().toArray(new Repository[0]));



    monitorService.addPropertyChangeListener("repositories", this::onRepositoriesChange);
    monitorService.addPropertyChangeListener("notify", this::notifyOnStateChange);
    monitorService.addPropertyChangeListener("running", this::onRunning);
    configurationWindow.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        configurationWindow.setVisible(false);
      }
    });

    toaster.toastStarted();
    tray.setShowWindow(this::showWindow);
  }

  private void onRunning(PropertyChangeEvent propertyChangeEvent) {
    tray.refreshing();
  }
  private void onAddRepository(ActionEvent e) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fileChooser.addChoosableFileFilter(new FileFilter() {
      @Override
      public boolean accept(File f) {
        return f.isDirectory();
      }

      @Override
      public String getDescription() {
        return "Git Repositories";
      }
    });
    int option = fileChooser.showOpenDialog(configurationWindow);
    if (option == JFileChooser.APPROVE_OPTION) {
      File selectedDirectory = fileChooser.getSelectedFile();
      if (!ofNullable(selectedDirectory.list((dir, name) -> ".git".equals(name))).map(files -> files.length > 0).orElse(false)) {
        JOptionPane.showMessageDialog(configurationWindow, "Selected directory is not a git repository: " + selectedDirectory.getAbsolutePath());
        return;
      }
      try {
        monitorService.addRepository(selectedDirectory);
      } catch (GitHubException exception) {
        JOptionPane.showMessageDialog(configurationWindow, exception.getMessage());
      }
    }
  }

  private void onRemoveRepositories(ActionEvent e) {
    var selected = configurationWindow.getMainForm().getRepositories().getSelectedValuesList();
    monitorService.removeRepositories(selected);
  }

  private void onWorkflowSelected(ListSelectionEvent e) {
    configurationWindow.getMainForm().getNotify().removeActionListener(this.notifyCheckedListener);
    configurationWindow.getMainForm().getMainBranch().removeActionListener(this.mainBranchListener);
    if (e.getValueIsAdjusting()) {
      return;
    }
    var selected = configurationWindow.getMainForm().getWorkflows().getSelectedValuesList();
    if (selected == null) {
      configurationWindow.getMainForm().getWorkflowConfig().setVisible(false);
      return;
    }
    configurationWindow.getMainForm().getWorkflowConfig().setVisible(true);
    var notify = selected.stream().map(monitorService::workflowNotified).reduce((a, b) -> a || b).orElse(false);
    configurationWindow.getMainForm().getNotify().setSelected(notify);
    if (selected.size() == 1) {
      configurationWindow.getMainForm().getMainBranch().setText(selected.get(0).getMainBranch());
    }
    configurationWindow.getMainForm().getNotify().addActionListener(this.notifyCheckedListener);
    this.selectedWorkflows = selected;
  }

  private void onGitHubSelect(ActionEvent event) {
    try {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setAcceptAllFileFilterUsed(false);
      fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      fileChooser.addChoosableFileFilter(new FileFilter() {
        @Override
        public boolean accept(File f) {
          return f.isDirectory() || f.getName().equals("gh") || f.getName().equals("gh.exe");
        }

        @Override
        public String getDescription() {
          return "GitHub CLI Executable";
        }
      });
      int option = fileChooser.showOpenDialog(configurationWindow);
      if (option == JFileChooser.APPROVE_OPTION) {
        File selectedDirectory = fileChooser.getSelectedFile();
        monitorService.setGitHubExecutable(selectedDirectory);

      }
    } catch (Exception exception) {
      JOptionPane.showMessageDialog(configurationWindow, exception.getMessage());
    }

  }

  private void onRepositoriesSelected(ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) {
      return;
    }
    var selected = configurationWindow.getMainForm().getRepositories().getSelectedValuesList();
    if (selected == null || selected.size() > 1) {
      configurationWindow.getMainForm().getDetailsPanel().setVisible(false);
      return;
    }
    var repository = selected.get(0);
    configurationWindow.getMainForm().getDetailsPanel().setVisible(true);
    configurationWindow.getMainForm().getRepositoryPath().setText("Workflows: " + repository.getPath().substring(repository.getPath().lastIndexOf('/') + 1));
    configurationWindow.getMainForm().getWorkflows().setListData(repository.getWorkflows().toArray(new Workflow[0]));
    configurationWindow.getMainForm().getWorkflowConfig().setVisible(false);
  }

  private void onRepositoriesChange(PropertyChangeEvent e) {
    SwingUtilities.invokeLater(() -> {
      configurationWindow.getMainForm().getRepositories().setListData(monitorService.getRepositories().toArray(new Repository[0]));
      configurationWindow.getMainForm().getWorkflowConfig().setVisible(false);
    });
  }

  private void notifyOnStateChange(PropertyChangeEvent e) {
    @SuppressWarnings("unchecked") var workflows = (Set<Workflow>) e.getNewValue();

    workflows.forEach(w -> {
      var state = monitorService.lastState(w);
      var repository = monitorService.findRepository(w);
      if (state == Configuration.State.SUCCESS) {
        this.toaster.toastSuccess(repository, w);
      } else {
        this.toaster.toastFailure(repository, w, () -> monitorService.browse(repository));
      }
    });
    var anyFailures = monitorService.anyFailures();
    tray.setFailures(anyFailures);
  }

  private void showWindow() {
    configurationWindow.setVisible(true);
    configurationWindow.setExtendedState(Frame.NORMAL);
    configurationWindow.toFront();
    configurationWindow.repaint();
  }

}
