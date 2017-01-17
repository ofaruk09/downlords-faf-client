package com.faforever.client.preferences;


public enum LanguageInfo {
  AUTO("auto", null, "System"), EN("en", "UK", "English"), DE("de", "DE", "Deutsch"), RU("ru", "RU", "ру́сский");

  private final String languageCode;
  private final String countryCode;
  private final String dispayName;

  LanguageInfo(String languageCode, String countryCode, String displayName) {
    this.countryCode = countryCode;
    this.dispayName = displayName;
    this.languageCode = languageCode;
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


}
