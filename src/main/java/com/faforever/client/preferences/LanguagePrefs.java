package com.faforever.client.preferences;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LanguagePrefs {
  private StringProperty language;


  public LanguagePrefs() {
    language = new SimpleStringProperty("auto");
  }

  public String getLanguage() {
    return language.get();
  }

  public void setLanguage(String newLanguage) {
    this.language.set(newLanguage);
  }
  public StringProperty language() {
    return language;
  }


}