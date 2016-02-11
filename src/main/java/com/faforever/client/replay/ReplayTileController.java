package com.faforever.client.replay;

import com.faforever.client.map.MapService;
import com.faforever.client.util.TimeService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class ReplayTileController {

  @FXML
  GridPane replayTileRoot;
  @FXML
  ImageView mapImageView;
  @FXML
  Label gameTitleLabel;
  @FXML
  Label gameTypeLabel;
  @FXML
  Label gameMapLabel;
  @FXML
  Label playerCountLabel;
  @FXML
  Label durationLabel;
  @FXML
  Label likesLabel;
  @FXML
  Label downloadsLabel;
  @FXML
  Label avgRatingLabel;
  @Resource
  MapService mapService;
  @Resource
  TimeService timeService;
  @Resource
  ReplayService replaceService;

  private int replayId;

  public void setReplay(ReplayInfoBean replayInfoBean) {
    replayId = replayInfoBean.getId();

    gameTitleLabel.setText(replayInfoBean.getTitle());
    gameTypeLabel.setText(replayInfoBean.getGameType());
    gameMapLabel.setText(replayInfoBean.getMap());

    int playerCount = 0;
    double totalRating = 0;
    for (Map.Entry<String, List<String>> team : replayInfoBean.getTeams().entrySet()) {
      playerCount += team.getValue().size();
    }
    playerCountLabel.setText(String.format("%d", playerCount));

    likesLabel.setText(String.format("%d", replayInfoBean.getLikes()));
    downloadsLabel.setText(String.format("%d", replayInfoBean.getDownloads()));

    Duration duration = Duration.between(replayInfoBean.getStartTime(), replayInfoBean.getEndTime());
    durationLabel.setText(timeService.shortDuration(duration));
    mapImageView.setImage(mapService.loadSmallPreview(replayInfoBean.getMap()));
  }

  @FXML
  void onClick(MouseEvent event) {
    if (event.getClickCount() == 2) {
      replaceService.download(replayId);
    } else {

    }
  }

  public Pane getRoot() {
    return replayTileRoot;
  }
}
