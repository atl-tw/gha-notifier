package com.thoughtworks.gha.notifier;

import lombok.Getter;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.MouseEvent.BUTTON1;
import static java.awt.event.MouseEvent.BUTTON3;

public class JTrayIcon extends TrayIcon {
  private JDialog parent;
  @Getter
  private JPopupMenu menu;
  private MouseListener mouseListener;
  private PopupMenuListener popupMenuListener;

  public JTrayIcon(Image image, String tooltip) {
    super(image, tooltip, null);

  }

  public static boolean isPopupTrigger(MouseEvent evt) {
    if (SystemUtil.operatingSystem == SystemUtil.OS.MAC_OS_X)
    {
      if(evt.getButton() == BUTTON1)
        return true;
      else
        return evt.getButton() == BUTTON3;
    }
    else
      return evt.getButton() == BUTTON3;
  }

  public void setMenu(JPopupMenu menu) {
    if (menu == null) {

      if (mouseListener != null) {
        removeMouseListener(mouseListener);
        mouseListener = null;
      }
      if (popupMenuListener != null) {
        this.menu.removePopupMenuListener(popupMenuListener);
        popupMenuListener = null;
      }
      parent = null;
    } else {
      parent = new JDialog((Frame) null);
      parent.setUndecorated(true);
      parent.setAlwaysOnTop(true);
      if (mouseListener == null) {
        mouseListener = new MyMouseListener();
        addMouseListener(mouseListener);
      }
      popupMenuListener = new MyPopupMenuListener();
      menu.addPopupMenuListener(popupMenuListener);
    }
    this.menu = menu;
  }
private class MyMouseListener extends MouseAdapter {
    @Override
    public void mouseClicked(MouseEvent e) {
      if (isPopupTrigger(e)) {
        parent.setLocation(e.getX(), e.getY() - menu.getPreferredSize().height);
        parent.setVisible(true);
        menu.show(parent, 0, 0);
      }
    }
  }

  private class MyPopupMenuListener implements PopupMenuListener {
    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
      parent.setVisible(false);
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
      parent.setVisible(false);
    }
  }
}