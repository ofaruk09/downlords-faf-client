package com.faforever.client.util;

import com.faforever.client.i18n.I18n;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.TimeZone;

public class TimeServiceImpl implements TimeService {

  @Resource
  I18n i18n;

  @Resource
  Locale locale;

  @Override
  public String timeAgo(Instant instant) {
    if (instant == null) {
      return "";
    }

    Duration ago = Duration.between(instant, Instant.now());

    if (ago.toMinutes() < 1) {
      return i18n.get("secondsAgo", ago.getSeconds());
    }
    if (ago.toMinutes() == 1) {
      return i18n.get("minuteAgo");
    }
    if (ago.toHours() < 1) {
      return i18n.get("minutesAgo", ago.toMinutes());
    }
    if (ago.toHours() == 1) {
      return i18n.get("hourAgo");
    }
    if (ago.toDays() < 1) {
      return i18n.get("hoursAgo", ago.toHours());
    }
    if (ago.toDays() == 1) {
      return i18n.get("dayAgo");
    }
    if (ago.toDays() / 7 < 1) {
      return i18n.get("daysAgo", ago.toDays());
    }
    if (ago.toDays() / 7 == 1) {
      return i18n.get("weekAgo");
    }
    if (ago.toDays() / 30 < 1) {
      return i18n.get("weeksAgo", ago.toDays() / 7);
    }
    if (ago.toDays() / 30 == 1) {
      return i18n.get("monthAgo");
    }
    if (ago.toDays() / 365 < 1) {
      return i18n.get("monthsAgo", ago.toDays() / 30);
    }
    if (ago.toDays() / 365 == 1) {
      return i18n.get("yearAgo");
    }

    return i18n.get("yearsAgo", ago.toDays() / 365);
  }

  @Override
  public String lessThanOneDayAgo(Instant instant) {
    if (instant == null) {
      return "";
    }

    Duration ago = Duration.between(instant, Instant.now());

    if (ago.compareTo(Duration.ofDays(1)) <= 0) {
      return timeAgo(instant);
    }

    return asDate(instant);
  }

  @Override
  public String asDate(Instant instant) {
    return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(
        ZonedDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId())
    );
  }

  @Override
  public String asShortTime(Instant instant) {
    return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale).format(
        ZonedDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId())
    );
  }

  @Override
  public String shortDuration(Duration duration) {
    if (duration == null) {
      return "";
    }

    if (Duration.ofMinutes(1).compareTo(duration) > 0) {
      return i18n.get("duration.seconds", duration.getSeconds());
    }
    if (Duration.ofHours(1).compareTo(duration) > 0) {
      return i18n.get("duration.minutesSeconds", duration.toMinutes(), duration.getSeconds());
    }

    return i18n.get("duration.hourMinutes", duration.toMinutes() / 60, duration.toMinutes() % 60);
  }

  @Override
  public LocalDate getLocalDateFromInstant(Instant instant) {
    return LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId()).toLocalDate();
  }
}
