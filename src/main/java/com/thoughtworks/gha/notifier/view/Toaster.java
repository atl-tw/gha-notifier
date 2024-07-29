package com.thoughtworks.gha.notifier.view;

import com.sshtools.twoslices.Toast;
import com.sshtools.twoslices.ToastType;
import com.thoughtworks.gha.notifier.GitHubNotifier;
import com.thoughtworks.gha.notifier.model.Repository;
import com.thoughtworks.gha.notifier.model.Workflow;

import java.util.Objects;

import static com.thoughtworks.gha.notifier.GitHubNotifier.GIT_HUB_NOTIFIER;
import static com.thoughtworks.gha.notifier.view.Tray.BUILD_PNG;

@SuppressWarnings("resource")
public class Toaster {

   public Toaster() {

  }


  public void toastSuccess(Repository repository, Workflow w) {
    Toast.builder()
        .type(ToastType.INFO)
        .icon(Objects.requireNonNull(GitHubNotifier.class.getResource("/green-check@24.png")).toString())
        .title(GIT_HUB_NOTIFIER).content(repository.getPath().substring(repository.getPath().lastIndexOf('/') + 1) + " workflow " + w.getName() + " is now successful.")
        .timeout(3)
        .toast();
  }

  public void toastFailure(Repository repository, Workflow w, Runnable action) {
    Toast.builder()
        .type(ToastType.ERROR)
        .image(Objects.requireNonNull(GitHubNotifier.class.getResource("/red-circle@24.png")).toString())
        .title(GIT_HUB_NOTIFIER).content(repository.getPath().substring(repository.getPath().lastIndexOf('/') + 1) + " workflow " + w.getName() + " is failing.")
        .action("Review", ()->action.run())
        .timeout(240)
        .toast();
  }

  public void toastStarted() {
    Toast.builder()
        .type(ToastType.INFO)
        .icon(Objects.requireNonNull(GitHubNotifier.class.getResource(BUILD_PNG)).toString())
        .title(GIT_HUB_NOTIFIER).content("Started")
        .timeout(3)
        .toast();
  }
}
