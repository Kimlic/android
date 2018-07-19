package com.kimlic.quorum.crypto;

import org.web3j.crypto.LinuxSecureRandom;

import java.security.SecureRandom;

public final class SecureRandomTools {

  // Constants

  private static int isAndroid = -1;
  private static final SecureRandom SECURE_RANDOM;

  // Variables

  static {
    if (isAndroidRuntime())
      new LinuxSecureRandom();

    SECURE_RANDOM = new SecureRandom();
  }

  // Public

  public static SecureRandom secureRandom() {
    return SECURE_RANDOM;
  }

  // Private

  private SecureRandomTools() {}

  private static boolean isAndroidRuntime() {
    if (isAndroid == -1) {
      final String runtime = System.getProperty("java.runtime.name");
      isAndroid = (runtime != null && runtime.equals("Android Runtime")) ? 1 : 0;
    }

    return isAndroid == 1;
  }
}
