package com.faforever.client.api;

import com.faforever.client.legacy.domain.VictoryCondition;
import com.faforever.client.replay.RatingType;
import com.google.common.base.Joiner;

import java.util.List;

public class GameSearchFields {

  private List<String> players;
  private String mapName;
  private boolean mapExclude;
  private int maxRating;
  private int minRating;
  private VictoryCondition victoryCondition;
  private RatingType ratingType;
  private int maxPlayerCount;
  private int minPlayerCount;

  public GameSearchFields(List<String> players, String mapName, boolean mapExclude, int maxRating, int minRating, VictoryCondition victoryCondition, RatingType ratingType, int maxPlayerCount, int minPlayerCount) {
    this.players = players;
    this.mapName = mapName;
    this.mapExclude = mapExclude;
    this.maxRating = maxRating;
    this.minRating = minRating;
    this.victoryCondition = victoryCondition;
    this.ratingType = ratingType;
    this.maxPlayerCount = maxPlayerCount;
    this.minPlayerCount = minPlayerCount;
  }

  public String getQueryParameters() {
    String filter = "filter[%s]=%s";
    return String.format(filter, "players", Joiner.on(',').join(players)) +
        String.format(filter, "map_name", mapName) +
        String.format(filter, "map_exclude", mapExclude) +
        String.format(filter, "max_rating", maxRating) +
        String.format(filter, "min_rating", minRating) +
        String.format(filter, "victory_condition", victoryCondition.toString()) +
        String.format(filter, "rating_type", ratingType.toString()) +
        String.format(filter, "max_player_count", maxPlayerCount) +
        String.format(filter, "min_player_count", minPlayerCount);
  }
}
