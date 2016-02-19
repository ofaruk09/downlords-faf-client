package com.faforever.client.replay;

import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.apache.commons.lang3.StringEscapeUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static com.faforever.client.util.TimeUtil.fromPythonTime;

public class LocalReplayInfoBean extends ReplayInfoBean {

  private final MapProperty<String, List<String>> teams;
  private final ObjectProperty<Path> replayFile;

  public LocalReplayInfoBean(LocalReplayInfo replayInfo, Path replayFile) {
    this();
    setId(replayInfo.getUid());
    setTitle(StringEscapeUtils.unescapeHtml4(replayInfo.getTitle()));
    setStartTime(fromPythonTime(replayInfo.getGameTime()));
    setEndTime(fromPythonTime(replayInfo.getGameEnd()));
    setGameType(replayInfo.getFeaturedMod());
    setMap(replayInfo.getMapname());
    this.replayFile.set(replayFile);
    if (replayInfo.getTeams() != null) {
      teams.putAll(replayInfo.getTeams());
    }
    if (replayInfo.getNumPlayers() != null) {
      setPlayerCount(replayInfo.getNumPlayers());
    }
  }

  public LocalReplayInfoBean() {
    teams = new SimpleMapProperty<>(FXCollections.observableHashMap());
    replayFile = new SimpleObjectProperty<>();
  }

  public Path getReplayFile() {
    return replayFile.get();
  }

  public void setReplayFile(Path replayFile) {
    this.replayFile.set(replayFile);
  }

  public ObjectProperty<Path> replayFileProperty() {
    return replayFile;
  }

  public ObservableMap<String, List<String>> getTeams() {
    return teams.get();
  }

  public void setTeams(ObservableMap<String, List<String>> teams) {
    this.teams.set(teams);
  }

  public MapProperty<String, List<String>> teamsProperty() {
    return teams;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LocalReplayInfoBean that = (LocalReplayInfoBean) o;
    return Objects.equals(getId(), that.getId());
  }
}
