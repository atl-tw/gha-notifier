package com.thoughtworks.gha.notifier.controller;

import com.thoughtworks.gha.notifier.model.Repository;
import com.thoughtworks.gha.notifier.view.Tray;
import lombok.Data;

import java.util.List;
import java.util.function.Consumer;

@Data
public class MockTray implements Tray {
  private Consumer<Repository> onRepositorySelected;
  private Runnable showWindow;
  private List<Repository> failures;
  private boolean anyFailures;
  private boolean refreshing;


  @Override
  public void setOnRepositorySelected(Consumer<Repository> onRepositorySelected) {
    this.onRepositorySelected = onRepositorySelected;
  }

  @Override
  public void setShowWindow(Runnable showWindow) {
    this.showWindow = showWindow;
  }

  @Override
  public void setFailingRepositories(List<Repository> failures) {
    this.failures = failures;
  }

  @Override
  public void setFailures(boolean anyFailures) {
    this.anyFailures = anyFailures;

  }

  @Override
  public void refreshing() {
    this.refreshing = true;
  }
}
