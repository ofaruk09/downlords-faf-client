package com.faforever.client.replay;

import com.faforever.client.legacy.domain.VictoryCondition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.Instant;

public class ReplayInfoBean {
  private final IntegerProperty id;
  private final StringProperty title;
  private final ObjectProperty<Instant> startTime;
  private final ObjectProperty<Instant> endTime;
  private final StringProperty gameType;
  private final StringProperty map;
  private final SimpleIntegerProperty playerCount;
  private final SimpleStringProperty host;
  private final ObjectProperty<VictoryCondition> victoryCondition;

  public ReplayInfoBean() {
    id = new SimpleIntegerProperty();
    title = new SimpleStringProperty();
    startTime = new SimpleObjectProperty<>();
    endTime = new SimpleObjectProperty<>();
    gameType = new SimpleStringProperty();
    map = new SimpleStringProperty();
    playerCount = new SimpleIntegerProperty();
    host = new SimpleStringProperty();
    victoryCondition = new SimpleObjectProperty<>();
  }

  public VictoryCondition getVictoryCondition() {
    return victoryCondition.get();
  }

  public void setVictoryCondition(VictoryCondition victoryCondition) {
    this.victoryCondition.set(victoryCondition);
  }

  public ObjectProperty<VictoryCondition> victoryConditionProperty() {
    return victoryCondition;
  }

  public int getId() {
    return id.get();
  }

  public void setId(int id) {
    this.id.set(id);
  }

  public IntegerProperty idProperty() {
    return id;
  }

  public String getTitle() {
    return title.get();
  }

  public void setTitle(String title) {
    this.title.set(title);
  }

  public StringProperty titleProperty() {
    return title;
  }

  public Instant getStartTime() {
    return startTime.get();
  }

  public void setStartTime(Instant startTime) {
    this.startTime.set(startTime);
  }

  public ObjectProperty<Instant> startTimeProperty() {
    return startTime;
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

  public String getGameType() {
    return gameType.get();
  }

  public void setGameType(String gameType) {
    this.gameType.set(gameType);
  }

  public StringProperty gameTypeProperty() {
    return gameType;
  }

  public String getMap() {
    return map.get();
  }

  public void setMap(String map) {
    this.map.set(map);
  }

  public StringProperty mapProperty() {
    return map;
  }

  public int getPlayerCount() {
    return playerCount.get();
  }

  public void setPlayerCount(int playerCount) {
    this.playerCount.set(playerCount);
  }

  public SimpleIntegerProperty playerCountProperty() {
    return playerCount;
  }

  public String getHost() {
    return host.get();
  }

  public void setHost(String host) {
    this.host.set(host);
  }

  public SimpleStringProperty hostProperty() {
    return host;
  }
}
