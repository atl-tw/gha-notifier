package com.thoughtworks.gha.notifier.view;

import com.thoughtworks.gha.notifier.model.Repository;
import com.thoughtworks.gha.notifier.model.Workflow;

import java.util.List;
import java.util.function.Consumer;

public interface MainForm {


  public void setOnAddRepository(Runnable onAddRepository);

  void setOnRemoveRepositories(Runnable onRemoveRepositories);

  void setOnGitHubSelect(Runnable onGitHubSelect);

  void setOnNotifyChanged(Runnable onNotifyChanged);

  void setOnMainBranchChanged(Consumer<String> onMainBranchChanged);

  void setOnRepositoriesSelected(Runnable onRepositoriesSelected);

  void setOnWorkflowSelected(Runnable onWorkflowSelected);

  boolean isNotifyChecked();

  void setRepositories(List<Repository> repositories);

  void showDetailsPane();

  void hideDetailsPane();

  void setTitle(String s);

  void hideWorkflowConfig();

  List<Repository> getSelectedRepositories();

  void setWorkflows(List<Workflow> workflows);

  void showWorkflowConfig();

  List<Workflow> getSelectedWorkflows();

  void setNotifyChecked(boolean notify);

  void setMainBranch(String mainBranch);
}
