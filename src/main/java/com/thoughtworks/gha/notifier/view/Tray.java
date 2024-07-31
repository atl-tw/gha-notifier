package com.thoughtworks.gha.notifier.view;

import com.thoughtworks.gha.notifier.model.Repository;

import java.util.List;
import java.util.function.Consumer;

public interface Tray {

  void setOnRepositorySelected(Consumer<Repository> onRepositorySelected);

  void setShowWindow(Runnable showWindow);

  void setFailingRepositories(List<Repository> failures);

  void setFailures(boolean anyFailures);

  void refreshing();
}
