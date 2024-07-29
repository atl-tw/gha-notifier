package com.thoughtworks.gha.notifier.view;

import com.thoughtworks.gha.notifier.GitHubNotifier;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

import static com.thoughtworks.gha.notifier.GitHubNotifier.GIT_HUB_NOTIFIER;

public class Tray {
  public static final String BUILD_PNG = "/build.png";

  private final Image failureImage = Toolkit.getDefaultToolkit().getImage(GitHubNotifier.class.getResource("/red-circle.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
  private final Image successImage = Toolkit.getDefaultToolkit().getImage(GitHubNotifier.class.getResource("/green-check.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
  private final Image refeshImage = Toolkit.getDefaultToolkit().getImage(GitHubNotifier.class.getResource("/orange-cycle.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
  private JTrayIcon trayIcon;


  @Setter
  private Runnable showWindow;

  public Tray() {
    if (SystemTray.isSupported()) {
      SystemTray tray = SystemTray.getSystemTray();
      Image image = Toolkit.getDefaultToolkit().getImage(GitHubNotifier.class.getResource(BUILD_PNG));
      JPopupMenu popup = new JPopupMenu();
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
