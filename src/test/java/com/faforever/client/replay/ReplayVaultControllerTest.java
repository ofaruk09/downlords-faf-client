package com.faforever.client.replay;

import com.faforever.client.i18n.I18n;
import com.faforever.client.notification.NotificationService;
import com.faforever.client.task.TaskService;
import com.faforever.client.test.AbstractPlainJavaFxTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class ReplayVaultControllerTest extends AbstractPlainJavaFxTest {

  private ReplayVaultController instance;
  @Mock
  private I18n i18n;
  @Mock
  private ApplicationContext applicationContext;
  @Mock
  private TaskService taskService;
  @Mock
  private NotificationService notificationService;
  @Mock
  private ReplayService replayService;

  @Before
  public void setUp() throws Exception {
    instance = loadController("replay_vault.fxml");
    instance.i18n = i18n;
    instance.applicationContext = applicationContext;
    instance.taskService = taskService;
    instance.notificationService = notificationService;
    instance.replayService = replayService;

  }

  @Test
  public void testGetRoot() throws Exception {
    assertThat(instance.getRoot(), is(instance.replayVaultRoot));
    assertThat(instance.getRoot().getParent(), is(nullValue()));
  }
}
