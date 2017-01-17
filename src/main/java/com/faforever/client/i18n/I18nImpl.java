package com.faforever.client.i18n;

import com.faforever.client.preferences.LanguageInfo;
import com.faforever.client.preferences.PreferencesService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Locale;

@Service
public class I18nImpl implements I18n {

  private MessageSource messageSource;
  private Locale locale;
  //Constructor injection causes circular reference exception
  @Inject
  private PreferencesService preferencesService;
  private Locale userSpecificLocale;

  @Inject
  public I18nImpl(Locale locale, MessageSource messageSource) {
    this.messageSource = messageSource;
    this.locale = locale;
  }

  @PostConstruct
  public void init() {
    LanguageInfo languageInfo = LanguageInfo.valueOf(preferencesService.getPreferences().getLanguagePrefs().getLanguage());
    String languageCode = languageInfo.getLanguageCode();
    if (!languageCode.equals(LanguageInfo.AUTO.getLanguageCode())) {
      userSpecificLocale = new Locale(languageCode, languageInfo.getCountryCode());
    }else {
      userSpecificLocale=locale;
    }
  }

  @Override
  public Locale getUserSpecificLocale() {
    return this.userSpecificLocale;
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
