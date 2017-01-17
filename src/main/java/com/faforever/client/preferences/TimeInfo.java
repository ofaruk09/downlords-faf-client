package com.faforever.client.preferences;

import java.util.Locale;

public enum TimeInfo {
  AUTO("System", null), MILLITARY_TIME("24h", new Locale("de", "DE")), UK_TIME("12h", new Locale("en", "UK"));
  private final Locale usedLocale;
  private final String dispayName;

  TimeInfo(String displayName, Locale usedLocale) {
    this.dispayName = displayName;
    this.usedLocale = usedLocale;
  }

  public Locale getUsedLocale() {
    return usedLocale;
  }

  public String getDisplayName() {
    return dispayName;
  }


}

