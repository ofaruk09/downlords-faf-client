package com.faforever.client.preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Locale;

public enum TimeInfo {
  AUTO("System", null, 0), MILLITARY_TIME("24h", new Locale("de", "DE"), 1), UK_TIME("12h", new Locale("en", "UK"), 2);
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final Locale usedLocale;
  private final String dispayName;
  private final int position;

  TimeInfo(String displayName, Locale usedLocale, int position) {
    this.dispayName = displayName;
    this.position = position;
    this.usedLocale = usedLocale;
  }
  public static TimeInfo getTimeInfoByDisplayName(String code) {
    for (TimeInfo LI : TimeInfo.values()) {
      if (LI.getDisplayName().equals(code)) {
        return LI;
      }
    }
    logger.warn("No matching TimeInfo found for DisplayCode: " + code);
    return null;
  }
  public static String[] getAllDisplayNames() {
    String[] displayNames = new String[TimeInfo.values().length];
    for (TimeInfo LI : TimeInfo.values()) {
      displayNames[LI.getPosition()] = LI.getDisplayName();
    }
    return displayNames;
  }
  public static int getIndexByDisplayName(String code) {
    for (TimeInfo LI : TimeInfo.values()) {
      if (LI.getDisplayName().equals(code)) {
        return LI.getPosition();
      }
    }
    logger.warn("No matching TimeInfo found for LanguageCode: " + code);
    return 0;
  }
  public Locale getUsedLocale() {
    return usedLocale;
  }
  public String getDisplayName() {
    return dispayName;
  }
  public int getPosition() {
    return position;
  }


}

