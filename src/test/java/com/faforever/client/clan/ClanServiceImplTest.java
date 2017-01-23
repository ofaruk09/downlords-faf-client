package com.faforever.client.clan;

import com.faforever.client.remote.FafService;
import com.faforever.client.test.AbstractPlainJavaFxTest;
import javafx.collections.FXCollections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


public class ClanServiceImplTest extends AbstractPlainJavaFxTest {
  private ClanServiceImpl instance;
  @Mock
  private FafService fafService;

  @Before
  public void setUp() throws Exception {
    instance = new ClanServiceImpl(fafService);
    Clan testClan = new Clan();
    testClan.setClanName("test1");
    testClan.setClanTag("XXX");
    when(fafService.getClans()).thenReturn(FXCollections.singletonObservableList(testClan));
    instance.init();
  }

  @Test
  public void testgetClanByTag() throws Exception {
    assertThat(instance.getClanByTag("XXX").getClanName(), is("test1"));
  }

  @Test
  public void testgetClanByTagIfTagEmpty() throws Exception {
    assertThat(instance.getClanByTag(""), nullValue());
  }
}