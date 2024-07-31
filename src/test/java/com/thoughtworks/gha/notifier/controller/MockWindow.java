package com.thoughtworks.gha.notifier.controller;

import com.thoughtworks.gha.notifier.view.ConfigurationWindow;
import com.thoughtworks.gha.notifier.view.MainForm;

import java.io.File;
import java.util.function.Consumer;

public class MockWindow implements ConfigurationWindow {
  @Override
  public MainForm getMainForm() {
    return null;
  }

  @Override
  public void showMessageDialog(String s) {

  }

  @Override
  public void selectGitHubRepository(Consumer<File> consumer) {

  }

  @Override
  public void selectGitHubExecutable(Consumer<File> consumer) {

  }

  @Override
  public void showWindow() {

  }
}
