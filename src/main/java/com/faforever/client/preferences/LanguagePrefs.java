package com.faforever.client.preferences;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class LanguagePrefs {
  private StringProperty language;


  public LanguagePrefs() {
    language = new SimpleStringProperty(LanguageInfo.AUTO.name());
  }

  public String getLanguage() {
    return language.get();
  }

  public void setLanguage(String language) {
    this.language.set(language);
  }

}