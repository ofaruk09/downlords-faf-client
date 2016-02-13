package com.faforever.client.api;

import com.faforever.client.legacy.domain.VictoryCondition;
import com.faforever.client.replay.ReplayInfoBean;
import com.google.api.client.util.Key;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  public ReplayInfoBean toReplayInfoBean() {
    ReplayInfoBean replayInfoBean = new ReplayInfoBean();
    replayInfoBean.setId(Integer.parseInt(id));
    replayInfoBean.setHost(host);
    replayInfoBean.setDownloads(0);
    replayInfoBean.setGameType(gameMod);
    replayInfoBean.setLikes(0);
    replayInfoBean.setMap(mapName);
    replayInfoBean.setPlayerCount(players.size());
    replayInfoBean.setStartTime(getStartTime().toInstant(ZoneOffset.UTC));
    replayInfoBean.setTitle(gameName);
    replayInfoBean.setVictoryCondition(VictoryCondition.valueOf(victoryCondition));
    Map<String, List<String>> teams = new HashMap<>();
    for (GamePlayerStats player : players) {
      String team = Integer.toString(player.getTeam());
      LocalDateTime scoreTime = player.getScoreTime();
      if (replayInfoBean.getEndTime() == null && scoreTime != null) {
        replayInfoBean.setEndTime(scoreTime.toInstant(ZoneOffset.UTC));
      } else if (scoreTime != null && scoreTime.toInstant(ZoneOffset.UTC).isAfter(replayInfoBean.getEndTime())) {
        replayInfoBean.setEndTime(scoreTime.toInstant(ZoneOffset.UTC));
      }
      if (!teams.containsKey(team)) {
        teams.put(team, new ArrayList<>());
      }
      teams.get(team).add(player.getLogin());
    }
    replayInfoBean.getTeams().putAll(teams);

    return replayInfoBean;
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
