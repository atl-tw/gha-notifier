package com.thoughtworks.gha.notifier.view;

import com.thoughtworks.gha.notifier.GitHubNotifier;
import com.thoughtworks.gha.notifier.model.Repository;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

import static com.thoughtworks.gha.notifier.GitHubNotifier.GIT_HUB_NOTIFIER;

public class Tray {
  public static final String BUILD_PNG = "/build.png";
  public static final String FAILING_WORKFLOWS = "Failing Workflows";

  private final Image failureImage = Toolkit.getDefaultToolkit().getImage(GitHubNotifier.class.getResource("/red-circle.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
  private final Image successImage = Toolkit.getDefaultToolkit().getImage(GitHubNotifier.class.getResource("/green-check.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
  private final Image refeshImage = Toolkit.getDefaultToolkit().getImage(GitHubNotifier.class.getResource("/orange-cycle.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
  private JTrayIcon trayIcon;
  private final java.util.List<JMenuItem> repositoryItems = new ArrayList<>();
  private final JPopupMenu popup = new JPopupMenu();
  @Setter
  private Consumer<Repository> onRepositorySelected;
  JMenuItem label = new JMenuItem(FAILING_WORKFLOWS);

  @Setter
  private Runnable showWindow;

  public Tray() {
    if (SystemTray.isSupported()) {
      SystemTray tray = SystemTray.getSystemTray();
      Image image = Toolkit.getDefaultToolkit().getImage(GitHubNotifier.class.getResource(BUILD_PNG));

      this.trayIcon = new JTrayIcon(image, GIT_HUB_NOTIFIER, popup);
      trayIcon.setImageAutoSize(true);
      JMenuItem restoreItem = new JMenuItem("Show Configuration");
      restoreItem.setPreferredSize(new Dimension(200, 20));
      restoreItem.addActionListener(e -> SwingUtilities.invokeLater(showWindow));


      JMenuItem exitItem = new JMenuItem("Quit");
      exitItem.addActionListener(e -> System.exit(0));
      restoreItem.setPreferredSize(new Dimension(200, 20));
      popup.add(restoreItem);
      popup.add(exitItem);
      popup.add(new JPopupMenu.Separator());


      label.setEnabled(false);
      label.setPreferredSize(new Dimension(200, 20));
      popup.add(label);
      trayIcon.addActionListener(e -> showWindow.run());

      try {
        tray.add(trayIcon);
      } catch (AWTException ex) {
        ex.printStackTrace();
      }
    } else {
      System.err.println("System tray not supported!");
    }
  }

  public void setFailingRepositories(java.util.List<Repository> failures){
    SwingUtilities.invokeLater(()->{
    this.repositoryItems.forEach(popup::remove);
    if(failures == null || failures.isEmpty()){
      label.setText("No failures! Good job!");
    } else {
      label.setText(FAILING_WORKFLOWS);
    }
      failures.stream().map(
          w -> {
            JMenuItem item = new JMenuItem(w.getPath().substring(w.getPath().lastIndexOf('/') + 1));
            item.setPreferredSize(new Dimension(200, 20));
            item.setIcon(new ImageIcon(failureImage));
            item.addActionListener(e -> onRepositorySelected.accept(w));
            return item;
          }
      ).forEach(i->{
        repositoryItems.add(i);
        popup.add(i);
        popup.repaint();
      });
    });
  }


  public void setFailures(boolean anyFailures) {
    SwingUtilities.invokeLater(() -> {
      trayIcon.setImage(anyFailures ? failureImage : successImage);
      trayIcon.setToolTip(anyFailures ? "There are failing workflows" : "All workflows are successful");
    });
  }

  public void refreshing() {
    SwingUtilities.invokeLater(() -> {
      trayIcon.setToolTip("Checking workflows");
      trayIcon.setImage(refeshImage);
    });
  }
}
