package com.faforever.client.replay;

import com.faforever.client.i18n.I18n;
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
  @FXML
  Label avgRatingLabel;
  @Resource
  MapService mapService;
  @Resource
  TimeService timeService;
  @Resource
  ReplayService replaceService;
  @Resource
  I18n i18n;

  private int replayId;
  private SelectedReplayVault selectedReplayVault;
  private ReplayInfoBean replayInfoBean;

  public void setReplay(ReplayInfoBean replayInfoBean, SelectedReplayVault replayVault) {
    this.replayInfoBean = replayInfoBean;
    this.selectedReplayVault = replayVault;
    replayId = replayInfoBean.getId();

    gameTitleLabel.setText(replayInfoBean.getTitle());
    gameTypeLabel.setText(replayInfoBean.getGameType());
    gameMapLabel.setText(replayInfoBean.getMap());
    playerCountLabel.setText(String.format("%d", replayInfoBean.getPlayerCount()));
    likesLabel.setText(String.format("%d", replayInfoBean.getLikes()));
    downloadsLabel.setText(String.format("%d", replayInfoBean.getDownloads()));
    if (replayInfoBean.getEndTime() == null || replayInfoBean.getStartTime() == null) {
      durationLabel.setText(i18n.get("replayVault.unknownDuration"));
    } else {
      Duration duration = Duration.between(replayInfoBean.getStartTime(), replayInfoBean.getEndTime());
      durationLabel.setText(timeService.shortDuration(duration));
    }
    mapImageView.setImage(mapService.loadSmallPreview(replayInfoBean.getMap()));
  }

  @FXML
  void onClick(MouseEvent event) {
    if (event.getClickCount() == 2) {
      // TODO error handling, FIXME do something?
      // TODO automatically download replay if local is corrupted
      switch (selectedReplayVault) {
        case LOCAL:
          replaceService.runReplay(replayInfoBean);
          break;
        case ONLINE:
          replaceService.download(replayId);
          break;
        case LIVE:
          break;
      }
    }
  }

  public Pane getRoot() {
    return replayTileRoot;
  }
}
