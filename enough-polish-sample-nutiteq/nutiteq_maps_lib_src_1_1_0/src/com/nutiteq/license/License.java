package com.nutiteq.license;

public class License {
  public static final License LICENSE_CHECKING = new License(64, "Checking...");
  public static final License LICENSE_INVALID_DATA = new License(-64, "Invalid license data!");
  public static final License LICENSE_NETWORK_ERROR = new License(-32, "Network error!");
  public static final License OFFLINE = new License(128, "Offline?");

  private final String message;
  private final int code;

  public License(final int code, final String message) {
    this.code = code;
    this.message = message;
  }

  public boolean isValid() {
    return code > 0;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof License)) {
      return false;
    }

    final License other = (License) obj;
    return code == other.code;
  }

  public int hashCode() {
    throw new RuntimeException("hashCode() not implemented!");
  }

  public String toString() {
    return new StringBuffer().append(code).append(": ").append(message).toString();
  }

  public String getMessage() {
    return message;
  }
}
