package com.faforever.client.replay;

import com.faforever.client.api.GamePlayerStats;
import com.faforever.client.api.GameStats;
import com.faforever.client.legacy.domain.VictoryCondition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlineReplayInfoBean extends ReplayInfoBean {

  private final IntegerProperty downloads;
  private final IntegerProperty likes;
  private Map<String, List<OnlineReplayPlayerInfoBean>> teams;

  public OnlineReplayInfoBean(GameStats gameStats) {
    setId(Integer.parseInt(gameStats.getId()));
    setHost(gameStats.getHost());
    downloads = new SimpleIntegerProperty(0);
    setGameType(gameStats.getGameMod());
    likes = new SimpleIntegerProperty(0);
    setMap(gameStats.getMapName());
    setPlayerCount(gameStats.getPlayers().size());
    setTitle(gameStats.getGameName());

    setVictoryCondition(VictoryCondition.valueOf(gameStats.getVictoryCondition()));
    teams = new HashMap<>();

    for (GamePlayerStats player : gameStats.getPlayers()) {
      OnlineReplayPlayerInfoBean onlineReplayPlayerInfoBean = new OnlineReplayPlayerInfoBean(player);
      String team = Integer.toString(player.getTeam());
      LocalDateTime scoreTime = player.getScoreTime();
      if (getEndTime() == null && scoreTime != null) {
        setEndTime(scoreTime.toInstant(ZoneOffset.UTC));
      } else if (scoreTime != null && scoreTime.toInstant(ZoneOffset.UTC).isAfter(getEndTime())) {
        setEndTime(scoreTime.toInstant(ZoneOffset.UTC));
      }
      if (!teams.containsKey(team)) {
        teams.put(team, new ArrayList<>());
      }
      teams.get(team).add(onlineReplayPlayerInfoBean);
    }
  }

  public int getDownloads() {
    return downloads.get();
  }

  public void setDownloads(int downloads) {
    this.downloads.set(downloads);
  }

  public IntegerProperty downloadsProperty() {
    return downloads;
  }

  public int getLikes() {
    return likes.get();
  }

  public void setLikes(int likes) {
    this.likes.set(likes);
  }

  public IntegerProperty likesProperty() {
    return likes;
  }

  public Map<String, List<OnlineReplayPlayerInfoBean>> getTeams() {
    return teams;
  }

  public void setTeams(Map<String, List<OnlineReplayPlayerInfoBean>> teams) {
    this.teams = teams;
  }
}
