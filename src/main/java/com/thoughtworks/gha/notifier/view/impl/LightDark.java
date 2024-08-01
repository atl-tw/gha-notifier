package com.thoughtworks.gha.notifier.view.impl;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.jthemedetecor.OsThemeDetector;
import com.thoughtworks.gha.notifier.GitHubNotifier;
import com.thoughtworks.gha.notifier.util.SystemUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LightDark {

  static final OsThemeDetector detector = OsThemeDetector.getDetector();

  static final java.util.List<JFrame> frames = new ArrayList<>();

  public static void init(){
    setTheme(detector.isDark());
    detector.registerListener(isDark-> {
      setTheme(isDark);
      frames.forEach(f->{
        SwingUtilities.updateComponentTreeUI(f);
        f.pack();
      });
    });
  }

  public static void register(String prefix, String postfix, int width, int height, Consumer<Image> consumer){

    var light = Toolkit.getDefaultToolkit().getImage(GitHubNotifier.class.getResource(prefix+"light"+postfix)).getScaledInstance(width, height, Image.SCALE_SMOOTH);
    var dark = Toolkit.getDefaultToolkit().getImage(GitHubNotifier.class.getResource(prefix+"dark"+postfix)).getScaledInstance(width, height, Image.SCALE_SMOOTH);
    detector.registerListener(isDark->
        consumer.accept(isDark ? dark : light));
    consumer.accept(detector.isDark() ? dark: light);
  }

  public static void listen(JFrame jFrame){
    frames.add(jFrame);
  }


  private static void setTheme(boolean isDark){
    try {
      if (isDark) {
        UIManager.setLookAndFeel(SystemUtil.operatingSystem == SystemUtil.OS.MAC_OS_X ?
            new FlatMacDarkLaf() : new FlatDarkLaf());
      } else {
        UIManager.setLookAndFeel(SystemUtil.operatingSystem == SystemUtil.OS.MAC_OS_X ?
            new FlatMacLightLaf() : new FlatLightLaf());
      }
    } catch (Exception e){
      Logger.getAnonymousLogger().log(Level.WARNING, "Failed to set theme to " + isDark, e);
    }
  }
}
