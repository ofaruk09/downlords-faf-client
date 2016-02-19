package com.faforever.client.replay;

import com.faforever.client.api.GamePlayerStats;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.time.Instant;
import java.time.ZoneOffset;

public class OnlineReplayPlayerInfoBean {

  private IntegerProperty color;
  private FloatProperty deviation;
  private IntegerProperty faction;
  private StringProperty login;
  private FloatProperty mean;
  private IntegerProperty place;
  private IntegerProperty score;
  private IntegerProperty team;
  private IntegerProperty gameId;
  private IntegerProperty playerId;
  private ObjectProperty<Instant> endTime;

  public OnlineReplayPlayerInfoBean(GamePlayerStats player) {
    setColor(player.getColor());
    setDeviation(player.getDeviation());
    setFaction(player.getFaction());
    setLogin(player.getLogin());
    setMean(player.getMean());
    setPlace(player.getPlace());
    setScore(player.getScore());
    setTeam(player.getTeam());
    setGameId(player.getGameId());
    setPlayerId(player.getPlayerId());
    setEndTime(player.getScoreTime().toInstant(ZoneOffset.UTC));
  }

  public int getColor() {
    return color.get();
  }

  public void setColor(int color) {
    this.color.set(color);
  }

  public IntegerProperty colorProperty() {
    return color;
  }

  public float getDeviation() {
    return deviation.get();
  }

  public void setDeviation(float deviation) {
    this.deviation.set(deviation);
  }

  public FloatProperty deviationProperty() {
    return deviation;
  }

  public int getFaction() {
    return faction.get();
  }

  public void setFaction(int faction) {
    this.faction.set(faction);
  }

  public IntegerProperty factionProperty() {
    return faction;
  }

  public String getLogin() {
    return login.get();
  }

  public void setLogin(String login) {
    this.login.set(login);
  }

  public StringProperty loginProperty() {
    return login;
  }

  public float getMean() {
    return mean.get();
  }

  public void setMean(float mean) {
    this.mean.set(mean);
  }

  public FloatProperty meanProperty() {
    return mean;
  }

  public int getPlace() {
    return place.get();
  }

  public void setPlace(int place) {
    this.place.set(place);
  }

  public IntegerProperty placeProperty() {
    return place;
  }

  public int getScore() {
    return score.get();
  }

  public void setScore(int score) {
    this.score.set(score);
  }

  public IntegerProperty scoreProperty() {
    return score;
  }

  public int getTeam() {
    return team.get();
  }

  public void setTeam(int team) {
    this.team.set(team);
  }

  public IntegerProperty teamProperty() {
    return team;
  }

  public int getGameId() {
    return gameId.get();
  }

  public void setGameId(int gameId) {
    this.gameId.set(gameId);
  }

  public IntegerProperty gameIdProperty() {
    return gameId;
  }

  public int getPlayerId() {
    return playerId.get();
  }

  public void setPlayerId(int playerId) {
    this.playerId.set(playerId);
  }

  public IntegerProperty playerIdProperty() {
    return playerId;
  }

  public Instant getEndTime() {
    return endTime.get();
  }

  public void setEndTime(Instant endTime) {
    this.endTime.set(endTime);
  }

  public ObjectProperty<Instant> endTimeProperty() {
    return endTime;
  }
}
