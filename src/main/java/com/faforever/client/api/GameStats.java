package com.faforever.client.api;

import com.faforever.client.replay.OnlineReplayInfoBean;
import com.google.api.client.util.Key;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GameStats {

  @Key
  private String id;
  @Key
  private String host;
  @Key
  private String validity;
  @Key
  private List<GamePlayerStats> players;
  @Key("game_mod")
  private String gameMod;
  @Key("game_name")
  private String gameName;
  @Key("map_name")
  private String mapName;
  @Key("start_time")
  private String startTime;
  @Key("victory_condition")
  private String victoryCondition;

  public OnlineReplayInfoBean toOnlineReplayInfoBean() {
    return new OnlineReplayInfoBean(this);
  }

  public LocalDateTime getStartTime() {
    return LocalDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(startTime));
  }

  public void setStartTime(LocalDateTime createTime) {
    this.startTime = DateTimeFormatter.ISO_DATE_TIME.format(createTime);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getValidity() {
    return validity;
  }

  public void setValidity(String validity) {
    this.validity = validity;
  }

  public List<GamePlayerStats> getPlayers() {
    return players;
  }

  public void setPlayers(List<GamePlayerStats> players) {
    this.players = players;
  }

  public String getGameMod() {
    return gameMod;
  }

  public void setGameMod(String gameMod) {
    this.gameMod = gameMod;
  }

  public String getGameName() {
    return gameName;
  }

  public void setGameName(String gameName) {
    this.gameName = gameName;
  }

  public String getMapName() {
    return mapName;
  }

  public void setMapName(String mapName) {
    this.mapName = mapName;
  }

  public String getVictoryCondition() {
    return victoryCondition;
  }

  public void setVictoryCondition(String victoryCondition) {
    this.victoryCondition = victoryCondition;
  }
}
