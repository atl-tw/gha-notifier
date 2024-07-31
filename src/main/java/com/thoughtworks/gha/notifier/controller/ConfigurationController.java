package com.thoughtworks.gha.notifier.controller;

import com.thoughtworks.gha.notifier.gh.GitHubException;
import com.thoughtworks.gha.notifier.model.Configuration;
import com.thoughtworks.gha.notifier.model.Workflow;
import com.thoughtworks.gha.notifier.service.MonitorService;
import com.thoughtworks.gha.notifier.view.ConfigurationWindow;
import com.thoughtworks.gha.notifier.view.Toaster;
import com.thoughtworks.gha.notifier.view.Tray;

import java.beans.PropertyChangeEvent;
import java.util.Set;
import java.util.List;

import static java.util.Optional.ofNullable;

public class ConfigurationController {

  private final MonitorService monitorService;
  private final ConfigurationWindow configurationWindow;
  private final Tray tray;
  private final Toaster toaster;

  private List<Workflow> selectedWorkflows;
  public ConfigurationController(MonitorService configurationService, ConfigurationWindow configurationWindow, Tray tray, Toaster toaster) {
    this.monitorService = configurationService;
    this.configurationWindow = configurationWindow;
    this.tray = tray;
    this.toaster = toaster;
    configurationWindow.getMainForm().setOnAddRepository(this::onAddRepository);
    configurationWindow.getMainForm().setOnRemoveRepositories(this::onRemoveRepositories);
    configurationWindow.getMainForm().setOnGitHubSelect(this::onGitHubSelect);
    configurationWindow.getMainForm().setOnRepositoriesSelected(this::onRepositoriesSelected);
    configurationWindow.getMainForm().setOnWorkflowSelected(this::onWorkflowSelected);
    configurationWindow.getMainForm().setRepositories(monitorService.getRepositories());
    monitorService.addPropertyChangeListener("repositories", this::onRepositoriesChange);
    monitorService.addPropertyChangeListener("notify", this::notifyOnStateChange);
    monitorService.addPropertyChangeListener("running", this::onRunning);
    tray.setOnRepositorySelected(configurationService::browse);

    toaster.toastStarted();
    tray.setShowWindow(this::showWindow);
  }

  private void onMainBranchChanged(String branch){
    if (selectedWorkflows == null) {
      return;
    }
    selectedWorkflows.forEach(workflow -> {
      workflow.setMainBranch(branch);
      monitorService.updateWorkflow(workflow);
    });
  }

  private void onNotifyChecked() {
    if (selectedWorkflows == null) {
      return;
    }
    selectedWorkflows.forEach(workflow -> {
      if (configurationWindow.getMainForm().isNotifyChecked()) {
        monitorService.addWorkflowToNotify(workflow);
      } else {
        monitorService.removeWorkflowToNotify(workflow);
      }
    });
  }

  private void onRunning(PropertyChangeEvent propertyChangeEvent) {
    tray.refreshing();
  }
  private void onAddRepository() {
      configurationWindow.selectGitHubRepository(selectedDirectory ->{
      if (!ofNullable(selectedDirectory.list((dir, name) -> ".git".equals(name))).map(files -> files.length > 0).orElse(false)) {
        configurationWindow.showMessageDialog("Selected directory is not a git repository: " + selectedDirectory.getAbsolutePath());
        return;
      }
      try {
        monitorService.addRepository(selectedDirectory);
      } catch (GitHubException exception) {
        configurationWindow.showMessageDialog(exception.getMessage());
      }
    });
  }

  private void onRemoveRepositories() {
    var selected = configurationWindow.getMainForm().getSelectedRepositories();
    monitorService.removeRepositories(selected);
  }

  private void onWorkflowSelected() {
    configurationWindow.getMainForm().setOnNotifyChanged(null);
    configurationWindow.getMainForm().setOnMainBranchChanged(null);
    var selected = configurationWindow.getMainForm().getSelectedWorkflows();
    if (selected == null || selected.isEmpty()) {
      configurationWindow.getMainForm().hideWorkflowConfig();
      return;
    }
    configurationWindow.getMainForm().showWorkflowConfig();
    var notify = selected.stream().map(monitorService::workflowNotified).reduce((a, b) -> a || b).orElse(false);
    configurationWindow.getMainForm().setNotifyChecked(notify);
    if (selected.size() == 1) {
      configurationWindow.getMainForm().setOnMainBranchChanged(this::onMainBranchChanged);
      configurationWindow.getMainForm().setMainBranch(selected.get(0).getMainBranch());
    }
    configurationWindow.getMainForm().setOnNotifyChanged(this::onNotifyChecked);
    this.selectedWorkflows = selected;
  }

  private void onGitHubSelect() {
    try {
      configurationWindow.selectGitHubExecutable(monitorService::setGitHubExecutable);
    } catch (Exception exception) {
      configurationWindow.showMessageDialog(exception.getMessage());
    }

  }

  private void onRepositoriesSelected() {
    var selected = configurationWindow.getMainForm().getSelectedRepositories();
    if (selected == null || selected.size() != 1) {
      configurationWindow.getMainForm().hideDetailsPane();
      return;
    }
    var repository = selected.get(0);
    configurationWindow.getMainForm().showDetailsPane();
    configurationWindow.getMainForm().setTitle("Workflows: " + repository.getPath().substring(repository.getPath().lastIndexOf('/') + 1));
    configurationWindow.getMainForm().setWorkflows(repository.getWorkflows());
    configurationWindow.getMainForm().hideWorkflowConfig();
  }

  private void onRepositoriesChange(PropertyChangeEvent e) {
      configurationWindow.getMainForm().setRepositories(monitorService.getRepositories());
      configurationWindow.getMainForm().hideWorkflowConfig();
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
    tray.setFailingRepositories(monitorService.getFailingRepositories());
  }

  private void showWindow() {
    configurationWindow.showWindow();
  }

}
