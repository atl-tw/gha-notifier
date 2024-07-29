package com.thoughtworks.gha.notifier;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.sshtools.twoslices.Toast;
import com.sshtools.twoslices.ToastActionListener;
import com.sshtools.twoslices.ToastType;
import com.thoughtworks.gha.notifier.gh.GitHubException;
import com.thoughtworks.gha.notifier.model.Configuration;
import com.thoughtworks.gha.notifier.model.Repository;
import com.thoughtworks.gha.notifier.model.Workflow;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static java.util.Optional.ofNullable;




public class GitHubNotifier {

  public static final String GIT_HUB_NOTIFIER = "GitHub Actions Notifier";
  public static final String BUILD_PNG = "/build.png";
  private final MonitorService configurationService = new MonitorService();
  private final Timer timer = new Timer(true);
  private JFrame frame = new JFrame(GIT_HUB_NOTIFIER);
  private MainForm mainForm = new MainForm();
  private java.util.List<Workflow> selectedWorkflows;
  private final ActionListener notifyCheckedListener = e1 -> {
    if (selectedWorkflows == null) {
      return;
    }
    selectedWorkflows.forEach(workflow -> {
      if (mainForm.getNotify().isSelected()) {
        configurationService.addWorkflowToNotify(workflow);
      } else {
        configurationService.removeWorkflowToNotify(workflow);
      }
    });
  };
  private final ActionListener mainBranchListener = e1 -> {
    if (selectedWorkflows == null) {
      return;
    }
    selectedWorkflows.forEach(workflow -> {
      workflow.setMainBranch(mainForm.getMainBranch().getText());
      configurationService.updateWorkflow(workflow);
    });
  };
  private JTrayIcon trayIcon;

  @SuppressWarnings("unchecked")
  private GitHubNotifier() {
    if (!configurationService.checkGitHub()) {
      JOptionPane.showMessageDialog(frame, "GitHub CLI not found. Please select the GitHub icon and locate it on your system.");
    }
    mainForm.getAdd().addActionListener(this::onAddRepository);
    mainForm.getRemove().addActionListener(this::onRemoveRepositories);
    mainForm.getGh().addActionListener(this::onGitHubSelect);
    mainForm.getRepositories().addListSelectionListener(this::onRepositoriesSelected);
    mainForm.getWorkflows().addListSelectionListener(this::onWorkflowSelected);
    mainForm.getRepositories().setListData(configurationService.getRepositories().toArray(new Repository[0]));

    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.setSize(1024, 600);
    frame.setLocationRelativeTo(null);
    frame.setContentPane(mainForm);
    frame.setAlwaysOnTop(true);
    mainForm.getWorkflowConfig().setVisible(false);
    setupTray();

    configurationService.addPropertyChangeListener("repositories", this::onRepositoriesChange);
    configurationService.addPropertyChangeListener("notify", this::notifyOnStateChange);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        frame.setVisible(false);
      }
    });
    mainForm.getDetailsPanel().setVisible(false);
    var closeable = Toast.toast(ToastType.INFO, Objects.requireNonNull(GitHubNotifier.class.getResource(BUILD_PNG)).toString(), GIT_HUB_NOTIFIER, "Started");
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        Platform.runLater(() -> {
          try {
            closeable.close();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
      }
    }, 3000);
  }

  public static void main(String[] args) throws InterruptedException {
    System.setProperty("apple.awt.UIElement", "true");
    try (var configFile = GitHubNotifier.class.getResourceAsStream("/logging.properties")) {
      LogManager.getLogManager().readConfiguration(configFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    FlatLightLaf.setup();
    try {
      UIManager.setLookAndFeel(new FlatDarkLaf());
    } catch (UnsupportedLookAndFeelException e) {
      Logger.getAnonymousLogger().log(Level.WARNING, "Failed to load LAF", e);
    }
    final CountDownLatch latch = new CountDownLatch(1);
    SwingUtilities.invokeLater(() -> {
      new JFXPanel();
      latch.countDown();
    });
    latch.await();
    SwingUtilities.invokeLater(GitHubNotifier::new);
  }

  private void onAddRepository(ActionEvent e) {
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
    int option = fileChooser.showOpenDialog(frame);
    if (option == JFileChooser.APPROVE_OPTION) {
      File selectedDirectory = fileChooser.getSelectedFile();
      if (!ofNullable(selectedDirectory.list((dir, name) -> ".git".equals(name))).map(files -> files.length > 0).orElse(false)) {
        JOptionPane.showMessageDialog(frame, "Selected directory is not a git repository: " + selectedDirectory.getAbsolutePath());
        return;
      }
      try {
        configurationService.addRepository(selectedDirectory);
      } catch (GitHubException exception) {
        JOptionPane.showMessageDialog(frame, exception.getMessage());
      }
    }
  }

  private void onRemoveRepositories(ActionEvent e) {
    var selected = mainForm.getRepositories().getSelectedValuesList();
    configurationService.removeRepositories(selected);
  }

  private void onWorkflowSelected(ListSelectionEvent e) {
    mainForm.getNotify().removeActionListener(this.notifyCheckedListener);
    mainForm.getMainBranch().removeActionListener(this.mainBranchListener);
    if (e.getValueIsAdjusting()) {
      return;
    }
    var selected = mainForm.getWorkflows().getSelectedValuesList();
    if (selected == null) {
      mainForm.getWorkflowConfig().setVisible(false);
      return;
    }
    mainForm.getWorkflowConfig().setVisible(true);
    var notify = selected.stream().map(configurationService::workflowNotified).reduce((a, b) -> a || b).orElse(false);
    mainForm.getNotify().setSelected(notify);
    if (selected.size() == 1) {
      mainForm.getMainBranch().setText(selected.get(0).getMainBranch());
    }
    mainForm.getNotify().addActionListener(this.notifyCheckedListener);
    this.selectedWorkflows = selected;
  }

  private void onGitHubSelect(ActionEvent event) {
    try {
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
      int option = fileChooser.showOpenDialog(frame);
      if (option == JFileChooser.APPROVE_OPTION) {
        File selectedDirectory = fileChooser.getSelectedFile();
        configurationService.setGitHubExecutable(selectedDirectory);

      }
    } catch (Exception exception) {
      JOptionPane.showMessageDialog(frame, exception.getMessage());
    }

  }

  private void onRepositoriesSelected(ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) {
      return;
    }
    var selected = mainForm.getRepositories().getSelectedValuesList();
    if (selected == null || selected.size() > 1) {
      mainForm.getDetailsPanel().setVisible(false);
      return;
    }
    var repository = (Repository) selected.get(0);
    mainForm.getDetailsPanel().setVisible(true);
    mainForm.getRepositoryPath().setText("Workflows: " + repository.getPath().substring(repository.getPath().lastIndexOf('/') + 1));
    mainForm.getWorkflows().setListData(repository.getWorkflows().toArray(new Workflow[0]));
    mainForm.getWorkflowConfig().setVisible(false);
  }

  private void onRepositoriesChange(PropertyChangeEvent e) {
    SwingUtilities.invokeLater(() -> {
      mainForm.getRepositories().setListData(configurationService.getRepositories().toArray(new Repository[0]));
      mainForm.getWorkflowConfig().setVisible(false);
    });
  }

  private void notifyOnStateChange(PropertyChangeEvent e) {
    @SuppressWarnings("unchecked") var workflows = (Set<Workflow>) e.getNewValue();
    workflows.forEach(w -> {
      var state = configurationService.lastState(w);
      var repository = configurationService.findRepository(w);
      if (state == Configuration.State.SUCCESS) {
        @SuppressWarnings("resource") var success = Toast.toast(ToastType.INFO, Objects.requireNonNull(GitHubNotifier.class.getResource("/green-check@24.png")).toString(), GIT_HUB_NOTIFIER, "Workflow " + w.getName() + " on " + repository.getPath().substring(repository.getPath().lastIndexOf('/') + 1) + " is now successful.");
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            Platform.runLater(() -> {
              try {
                success.close();
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });

          }
        }, 3000);
      } else {
        //noinspection resource
        Toast.builder()
            .type(ToastType.ERROR)
            .image(Objects.requireNonNull(GitHubNotifier.class.getResource("/red-circle@24.png")).toString())
            .title(GIT_HUB_NOTIFIER).content("Workflow " + w.getName() + " on " + repository.getPath().substring(repository.getPath().lastIndexOf('/') + 1) + " is failing.")
            .action("Review", () -> configurationService.browse(repository))
            .timeout(240)
            .toast();
      }
    });
  }
  private void showWindow() {
    frame.setVisible(true);
    frame.setExtendedState(JFrame.NORMAL);
    frame.toFront();
    frame.repaint();
  }

  private void setupTray() {
    if (SystemTray.isSupported()) {
      SystemTray tray = SystemTray.getSystemTray();
      Image image = Toolkit.getDefaultToolkit().getImage(GitHubNotifier.class.getResource(BUILD_PNG));
      JPopupMenu popup = new JPopupMenu();
      this.trayIcon = new JTrayIcon(image, "System Tray Example", popup);
      trayIcon.setImageAutoSize(true);
      JMenuItem restoreItem = new JMenuItem("Show Configuration");
      restoreItem.setPreferredSize(new Dimension(200, 20));
      restoreItem.addActionListener(e -> SwingUtilities.invokeLater(this::showWindow));


      JMenuItem exitItem = new JMenuItem("Quit");
      exitItem.addActionListener(e -> System.exit(0));
      restoreItem.setPreferredSize(new Dimension(200, 20));
      popup.add(restoreItem);
      popup.add(exitItem);

      trayIcon.addActionListener(e -> showWindow());

      try {
        tray.add(trayIcon);
      } catch (AWTException ex) {
        ex.printStackTrace();
      }
    } else {
      System.err.println("System tray not supported!");
    }
  }
}