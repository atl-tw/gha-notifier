package com.thoughtworks.gha.notifier.view.impl;

import com.thoughtworks.gha.notifier.util.SystemUtil;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class JTrayIcon extends TrayIcon implements PopupMenuListener, MouseListener {

  private final JDialog dialog;
  private final JPopupMenu menu;

  public JTrayIcon(Image image, String tooltip, JPopupMenu menu) {
    super(image, tooltip);
    this.menu = menu;

    this.addMouseListener(this);
    menu.addPopupMenuListener(this);

    dialog = new JDialog();
    dialog.setUndecorated(true);
    dialog.setAlwaysOnTop(true);
    dialog.setOpacity(0.05f);

    menu.addFocusListener(new FocusListener(){
      @Override
      public void focusGained(FocusEvent e) {
        var x =1 ;
      }

      @Override
      public void focusLost(FocusEvent e) {
        if (menu.isVisible()) {
          menu.setVisible(false);
          dialog.setVisible(false);
        }
      }
    });

  }

  @Override
  public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
  }

  @Override
  public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    dialog.setVisible(false);
  }

  @Override
  public void popupMenuCanceled(PopupMenuEvent e) {
    dialog.setVisible(false);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (
            (SystemUtil.operatingSystem == SystemUtil.OS.MAC_OS_X || e.getButton() == MouseEvent.BUTTON3) && menu != null) {
      Dimension size = menu.getPreferredSize();
      //dialog.setLocation(e.getX() - (int) getSize().getWidth(), e.getY() - size.height - 3);
      dialog.setLocation(0,0);
      dialog.setSize(Toolkit.getDefaultToolkit().getScreenSize());
      dialog.setVisible(true);
      menu.show(dialog.getContentPane(), e.getX() - (int) getSize().getWidth(), e.getY() - size.height - 3);
      menu.grabFocus();
    }

  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {

  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }
}
