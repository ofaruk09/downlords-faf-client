package com.faforever.client.replay;

public enum ReplaySortingOption {
  DATE("replayVault.titlePane.date"),
  MOST_LIKED("replayVault.titlePane.likes"),
  MOST_DOWNLOADED("replayVault.titlePane.downloads"),
  HIGHEST_AVG_GLOB_RATING("replayVault.titlePane.avgGlobalRating"),
  HIGHEST_AVG_LADDER_RATING("replayVault.titlePane.avgLadderRating");

  private final String i18nKey;

  ReplaySortingOption(String i18nKey) {
    this.i18nKey = i18nKey;
  }

  public String getI18nKey() {
    return i18nKey;
  }

}