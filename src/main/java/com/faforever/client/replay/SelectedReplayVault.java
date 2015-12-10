package com.faforever.client.replay;

public enum SelectedReplayVault {
  LOCAL("replayVault.localReplays"),
  ONLINE("replayVault.onlineReplays"),
  LIVE("replayVault.liveReplays");

  private final String i18nKey;

  SelectedReplayVault(String i18nKey) {
    this.i18nKey = i18nKey;
  }

  public String getI18nKey() {
    return i18nKey;
  }
}