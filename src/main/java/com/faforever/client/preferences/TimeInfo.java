package com.faforever.client.preferences;

import com.faforever.client.i18n.I18n;

import java.util.Locale;

public enum TimeInfo {
  AUTO("system", null), MILLITARY_TIME("24", new Locale("de", "DE")), UK_TIME("12", new Locale("en", "UK"));
  private final Locale usedLocale;
  private final String displayNameKey;

  TimeInfo(String displayNameKey, Locale usedLocale) {
    this.displayNameKey = displayNameKey;
    this.usedLocale = usedLocale;
  }

  public Locale getUsedLocale() {
    return usedLocale;
  }

  public String getDisplayName(I18n i18n) {
    return i18n.get("settings.time." + displayNameKey);
  }


}

