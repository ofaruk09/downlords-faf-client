package com.faforever.client.preferences;


import com.faforever.client.i18n.I18n;

public enum LanguageInfo {
  AUTO(null, null, "system"), EN("en", "UK", "english"), DE("de", "DE", "german"), RU("ru", "RU", "russian");

  private final String languageCode;
  private final String countryCode;
  private final String displayNameKey;

  LanguageInfo(String languageCode, String countryCode, String displayNameKey) {
    this.countryCode = countryCode;
    this.displayNameKey = displayNameKey;
    this.languageCode = languageCode;
  }


  public String getLanguageCode() {
    return languageCode;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public String getDisplayName(I18n i18n) {
    return i18n.get("settings.languages." + displayNameKey);
  }


}
