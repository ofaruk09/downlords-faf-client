package com.faforever.client.api;

import com.google.api.client.util.Key;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GamePlayerStats {

  @Key
  private int color;
  @Key
  private float deviation;
  @Key
  private int faction;
  @Key
  private String login;
  @Key
  private float mean;
  @Key
  private int place;
  @Key
  private int score;
  @Key
  private int team;
  @Key
  private String id;
  @Key("game_id")
  private int gameId;
  @Key("player_id")
  private int playerId;
  @Key("score_time")
  private String scoreTime;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public float getDeviation() {
    return deviation;
  }

  public void setDeviation(float deviation) {
    this.deviation = deviation;
  }

  public int getFaction() {
    return faction;
  }

  public void setFaction(int faction) {
    this.faction = faction;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public float getMean() {
    return mean;
  }

  public void setMean(float mean) {
    this.mean = mean;
  }

  public int getPlace() {
    return place;
  }

  public void setPlace(int place) {
    this.place = place;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getGameId() {
    return gameId;
  }

  public void setGameId(int gameId) {
    this.gameId = gameId;
  }

  public int getPlayerId() {
    return playerId;
  }

  public void setPlayerId(int playerId) {
    this.playerId = playerId;
  }

  public LocalDateTime getScoreTime() {
    if (scoreTime != null && !scoreTime.equals("")) {
      return LocalDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(scoreTime));
    } else {
      return null;
    }
  }

  public void setScoreTime(LocalDateTime scoreTime) {
    this.scoreTime = DateTimeFormatter.ISO_DATE_TIME.format(scoreTime);
  }

  public int getTeam() {
    return team;
  }

  public void setTeam(int team) {
    this.team = team;
  }
}
