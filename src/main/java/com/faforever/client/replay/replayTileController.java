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

    //FIXME: update with api
    playerCountLabel.setText("0");
    likesLabel.setText("0");
    downloadsLabel.setText("0");

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
