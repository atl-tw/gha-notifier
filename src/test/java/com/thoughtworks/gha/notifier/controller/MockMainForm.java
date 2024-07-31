package com.thoughtworks.gha.notifier.controller;

import com.thoughtworks.gha.notifier.model.Repository;
import com.thoughtworks.gha.notifier.model.Workflow;
import com.thoughtworks.gha.notifier.view.MainForm;
import lombok.Data;

import java.util.List;
import java.util.function.Consumer;

@Data
public class MockMainForm implements MainForm {
  private Runnable onAddRepository;
  private Runnable onRemoveRepositories;
  private Runnable onGitHubSelect;
  private Runnable onNotifyChanged;
  private Consumer<String> onMainBranchChanged;
  private Runnable onRepositoriesSelected;
  private Runnable onWorkflowSelected;
  private List<Repository> repositories;
  private List<Workflow> workflows;
  private List<Repository> selectedRepositories;
  boolean notifyChecked;
  private boolean detailsPaneShowing;
  private String title;
  private boolean workflowConfigShowing;
  private List<Workflow> selectedWorkflows;
  private String mainBranch;


  @Override
  public void showDetailsPane() {
    this.detailsPaneShowing = true;
  }

  @Override
  public void hideDetailsPane() {
    this.detailsPaneShowing = false;
  }

  @Override
  public void showWorkflowConfig() {
    this.workflowConfigShowing = true;
  }

  @Override
  public void hideWorkflowConfig() {
    this.workflowConfigShowing =false;
  }
}
