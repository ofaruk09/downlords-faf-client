package com.faforever.client.util;

import com.faforever.client.i18n.I18n;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

public class TimeServiceImplTest {

  private TimeServiceImpl timeService;

  @Mock
  private I18n i18n;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    timeService = new TimeServiceImpl();
    timeService.i18n = i18n;
  }

  @Test
  public void testTimeAgoNullReturnsEmptyString() throws Exception {
    assertThat(timeService.timeAgo(null), is(""));
  }

  @Test
  public void testTimeAgoSeconds() throws Exception {
    timeService.timeAgo(Instant.now().minusSeconds(59));
    verify(i18n).get("secondsAgo", 59L);
  }

  @Test
  public void testTimeAgoMinute() throws Exception {
    timeService.timeAgo(Instant.now().minusSeconds(2 * 60 - 1));
    verify(i18n).get("minuteAgo");
  }

  @Test
  public void testTimeAgoMinutes() throws Exception {
    timeService.timeAgo(Instant.now().minusSeconds(60 * 60 - 1));
    verify(i18n).get("minutesAgo", 59L);
  }

  @Test
  public void testTimeAgoHour() throws Exception {
    timeService.timeAgo(Instant.now().minusSeconds(2 * 60 * 60 - 1));
    verify(i18n).get("hourAgo");
  }

  @Test
  public void testTimeAgoHours() throws Exception {
    timeService.timeAgo(Instant.now().minusSeconds(24 * 60 * 60 - 1));
    verify(i18n).get("hoursAgo", 23L);
  }

  @Test
  public void testTimeAgoDay() throws Exception {
    timeService.timeAgo(Instant.now().minusSeconds(2 * 24 * 60 * 60 - 1));
    verify(i18n).get("dayAgo");
  }

  @Test
  public void testTimeAgoDays() throws Exception {
    timeService.timeAgo(Instant.now().minusSeconds(7 * 24 * 60 * 60 - 1));
    verify(i18n).get("daysAgo", 6L);
  }

  @Test
  public void testTimeAgoWeek() throws Exception {
    timeService.timeAgo(Instant.now().minusSeconds(2 * 7 * 24 * 60 * 60 - 1));
    verify(i18n).get("weekAgo");
  }

  @Test
  public void testTimeAgoWeeks() throws Exception {
    timeService.timeAgo(Instant.now().minusSeconds(30 * 24 * 60 * 60 - 1));
    verify(i18n).get("weeksAgo", 4L);
  }

  @Test
  public void testTimeAgoMonth() throws Exception {
    timeService.timeAgo(Instant.now().minusSeconds(2 * 30 * 24 * 60 * 60 - 1));
    verify(i18n).get("monthAgo");
  }

  @Test
  public void testTimeAgoMonths() throws Exception {
    timeService.timeAgo(Instant.now().minusSeconds(365 * 24 * 60 * 60 - 1));
    verify(i18n).get("monthsAgo", 12L);
  }

  @Test
  public void testTimeAgoYear() throws Exception {
    timeService.timeAgo(Instant.now().minusSeconds(2 * 365 * 24 * 60 * 60L - 1));
    verify(i18n).get("yearAgo");
  }

  @Test
  public void testTimeAgoYears() throws Exception {
    timeService.timeAgo(Instant.now().minusSeconds(2 * 365 * 24 * 60 * 60));
    verify(i18n).get("yearsAgo", 2L);
  }
}
