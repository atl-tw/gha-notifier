package com.thoughtworks.gha.notifier.view;

import com.thoughtworks.gha.notifier.util.SystemUtil;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class JTrayIcon extends TrayIcon implements PopupMenuListener, MouseListener {

  private JDialog mDialog;
  private JPopupMenu mMenu;

  public JTrayIcon(Image image, String tooltip, JPopupMenu menu) {
    super(image, tooltip);
    mMenu = menu;

    this.addMouseListener(this);
    menu.addPopupMenuListener(this);

    mDialog = new JDialog();
    mDialog.setUndecorated(true);
    mDialog.setAlwaysOnTop(true);
  }

  @Override
  public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
  }

  @Override
  public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    mDialog.setVisible(false);
  }

  @Override
  public void popupMenuCanceled(PopupMenuEvent e) {
    mDialog.setVisible(false);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (SystemUtil.operatingSystem == SystemUtil.OS.MAC_OS_X || e.getButton() == MouseEvent.BUTTON3 && mMenu != null) {
      Dimension size = mMenu.getPreferredSize();
      mDialog.setLocation(e.getX() - (int) getSize().getWidth(), e.getY() - size.height - 3);
      mDialog.setSize(size);
      mDialog.setVisible(true);
      mMenu.show(mDialog.getContentPane(), 0, 0);
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }
}
