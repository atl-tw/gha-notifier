package com.thoughtworks.gha.notifier.view;

import com.thoughtworks.gha.notifier.model.Repository;
import com.thoughtworks.gha.notifier.model.Workflow;

public interface Toaster {
 void toastSuccess(Repository repository, Workflow w);

  void toastFailure(Repository repository, Workflow w, Runnable action);

  void toastStarted();
}
