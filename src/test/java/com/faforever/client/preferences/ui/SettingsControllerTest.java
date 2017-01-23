package com.faforever.client.preferences.ui;

import com.faforever.client.i18n.I18n;
import com.faforever.client.notification.NotificationService;
import com.faforever.client.preferences.PreferencesService;
import com.faforever.client.theme.UiService;
import com.faforever.client.user.UserService;
import com.google.common.eventbus.EventBus;
import org.junit.Before;
import org.mockito.Mock;

public class SettingsControllerTest {

  private SettingsController instance;
  @Mock
  private UserService userService;
  @Mock
  private PreferencesService preferenceService;
  @Mock
  private UiService uiService;
  @Mock
  private EventBus eventBus;
  @Mock
  private I18n i18n;
  @Mock
  private NotificationService notificationService;

  @Before
  public void init() {
    instance = new SettingsController(userService, preferenceService, uiService, i18n, eventBus, notificationService);
  }
  //No test needed till now
}