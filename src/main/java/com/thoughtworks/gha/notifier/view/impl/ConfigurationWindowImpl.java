package com.thoughtworks.gha.notifier.view.impl;

import com.thoughtworks.gha.notifier.view.ConfigurationWindow;
import com.thoughtworks.gha.notifier.view.MainForm;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.function.Consumer;

import static com.thoughtworks.gha.notifier.GitHubNotifier.GIT_HUB_NOTIFIER;

public class ConfigurationWindowImpl extends JFrame implements ConfigurationWindow {


  private final MainFormImpl mainForm = new MainFormImpl();

  public ConfigurationWindowImpl(){
    super(GIT_HUB_NOTIFIER);
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    this.setSize(1024, 600);
    this.setLocationRelativeTo(null);
    this.setContentPane(mainForm);
    this.setAlwaysOnTop(true);
    mainForm.hideWorkflowConfig();
    mainForm.hideDetailsPane();
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        setVisible(false);
      }
    });
    LightDark.listen(this);
  }

  public MainForm getMainForm(){
    return mainForm;
  }

  public void showMessageDialog(String s) {
    JOptionPane.showMessageDialog(this, s);
  }

  public void selectGitHubRepository(Consumer<File> consumer){
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
    int option = fileChooser.showOpenDialog(this);
    if (option == JFileChooser.APPROVE_OPTION) {
      File selectedDirectory = fileChooser.getSelectedFile();
      consumer.accept(selectedDirectory);
    }
  }

  public void selectGitHubExecutable(Consumer<File> consumer){
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
    int option = fileChooser.showOpenDialog(this);
    if (option == JFileChooser.APPROVE_OPTION) {
      File selectedDirectory = fileChooser.getSelectedFile();
      consumer.accept(selectedDirectory);
    }
  }

  public void showWindow() {
    this.setVisible(true);
    this.setExtendedState(Frame.NORMAL);
    this.toFront();
    this.repaint();
  }
}
