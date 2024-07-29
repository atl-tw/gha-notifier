package com.thoughtworks.gha.notifier.util;

import java.io.File;

import static com.thoughtworks.gha.notifier.util.SystemUtil.OS.MAC_OS_X;
import static com.thoughtworks.gha.notifier.util.SystemUtil.OS.OS2;
import static com.thoughtworks.gha.notifier.util.SystemUtil.OS.UNKNOWN;
import static com.thoughtworks.gha.notifier.util.SystemUtil.OS.VMS;
import static com.thoughtworks.gha.notifier.util.SystemUtil.OS.WINDOWS_NT;

public class SystemUtil {
  public static final OS operatingSystem;

  static {
    String osName = System.getProperty("os.name");
    if (osName.contains("Windows 9")
        || osName.contains("Windows M")) {
      operatingSystem = OS.WINDOWS_9X;
    } else if (osName.contains("Windows")) {
      operatingSystem = WINDOWS_NT;
    } else if (osName.contains("VMS")) {
      operatingSystem = VMS;
    } else if (osName.contains("OS X")) {
      operatingSystem = MAC_OS_X;
    } else if (File.separatorChar == '/') {
      operatingSystem = OS.UNIX;
    } else if (osName.contains("OS/2")) {
      operatingSystem = OS2;
    } else {
      operatingSystem = UNKNOWN;
    }
  }

  public enum OS {
    WINDOWS_9X,
    WINDOWS_NT,
    VMS,
    MAC_OS_X,
    UNIX,
    OS2,
    UNKNOWN
  }
}
