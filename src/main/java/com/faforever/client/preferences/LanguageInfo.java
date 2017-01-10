package com.faforever.client.preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public enum LanguageInfo {
  AUTO("auto", null, "System", 0), EN("en", "UK", "English", 1), DE("de", "DE", "Deutsch", 2), RU("ru", "RU", "ру́сский", 3);

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final String languageCode;
  private final String countryCode;
  private final String dispayName;
  private final int position;

  LanguageInfo(String languageCode, String countryCode, String displayName, int position) {
    this.countryCode = countryCode;
    this.dispayName = displayName;
    this.languageCode = languageCode;
    this.position = position;
  }
  public static LanguageInfo getLanuageInfoByLanguageCode(String code) {
    for (LanguageInfo LI : LanguageInfo.values()) {
      if (LI.getLanguageCode().equals(code)) {
        return LI;
      }
    }
    logger.warn("No matching LanguageInfo found for LangaugeCode: " + code);
    return null;
  }
  public static LanguageInfo getLanuageInfoByCountryCode(String code) {
    for (LanguageInfo LI : LanguageInfo.values()) {
      if (LI.getCountryCode().equals(code)) {
        return LI;
      }
    }
    logger.warn("No matching LanguageInfo found for CountryCode: " + code);
    return null;
  }
  public static LanguageInfo getLanuageInfoByDisplayName(String code) {
    for (LanguageInfo LI : LanguageInfo.values()) {
      if (LI.getDisplayName().equals(code)) {
        return LI;
      }
    }
    logger.warn("No matching LanguageInfo found for DisplayCode: " + code);
    return null;
  }
  public static String[] getAllDisplayNames() {
    String[] displayNames = new String[LanguageInfo.values().length];
    for (LanguageInfo LI : LanguageInfo.values()) {
      displayNames[LI.getPosition()] = LI.getDisplayName();
    }
    return displayNames;
  }
  public static int getIndexByLanguageCode(String code) {
    for (LanguageInfo LI : LanguageInfo.values()) {
      if (LI.getLanguageCode().equals(code)) {
        return LI.getPosition();
      }
    }
    logger.warn("No matching LanguageInfo found for LanguageCode: " + code);
    return 0;
  }
  public String getLanguageCode() {
    return languageCode;
  }
  public String getCountryCode() {
    return countryCode;
  }
  public String getDisplayName() {
    return dispayName;
  }
  public int getPosition() {
    return position;
  }


}
