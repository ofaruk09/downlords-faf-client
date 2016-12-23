package com.faforever.client.i18n;

import com.faforever.client.preferences.PreferencesService;
import org.springframework.context.MessageSource;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Locale;

public class I18nImpl implements I18n {

  @Inject
  MessageSource messageSource;

  @Inject
  Locale locale;
  @Inject
  ArrayList<String> languageInfo;
  @Inject
  PreferencesService preferencesService;

  Locale userSpecificLocale;

  @PostConstruct
  public void init()
  {
    if(!preferencesService.getPreferences().getLang().getLanguage().equals("auto")) {
      userSpecificLocale = new Locale(preferencesService.getPreferences().getLang().getLanguage(), getCountryFromLanguage(preferencesService.getPreferences().getLang().getLanguage()));
    }else {
      userSpecificLocale=locale;
    }
  }
  @Override
  public Locale getUserSpecificLocale()
  {
    return userSpecificLocale;
  }

  private String getCountryFromLanguage(String language) {
    for (int i=0; i!=languageInfo.size();i++)
    {
      if(languageInfo.get(i).equals(language))return languageInfo.get(i+1);

    }
    return"UK";
  }

  @Override
  public String get(String key, Object... args) {
    return messageSource.getMessage(key, args, userSpecificLocale);
  }

  @Override
  public String getQuantized(String singularKey, String pluralKey, long arg) {
    Object[] args = {arg};
    if (Math.abs(arg) == 1) {
      return messageSource.getMessage(singularKey, args, userSpecificLocale);
    }
    return messageSource.getMessage(pluralKey, args, userSpecificLocale);
  }

  @Override
  public Locale getLocale() {
    return locale;
  }

  @Override
  public String number(int number) {
    return String.format(userSpecificLocale, "%d", number);
  }
}
