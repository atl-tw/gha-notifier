package com.thoughtworks.gha.notifier.view;

import java.io.File;
import java.util.function.Consumer;

public interface ConfigurationWindow {

  MainForm getMainForm();

  void showMessageDialog(String s);

  void selectGitHubRepository(Consumer<File> consumer);

  void selectGitHubExecutable(Consumer<File> consumer);

  void showWindow();
}
