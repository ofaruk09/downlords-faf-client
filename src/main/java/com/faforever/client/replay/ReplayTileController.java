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
  private LocalReplayInfoBean localReplayInfoBean;

  public void setReplay(LocalReplayInfoBean localReplayInfoBean, SelectedReplayVault replayVault) {
    this.localReplayInfoBean = localReplayInfoBean;
    this.selectedReplayVault = replayVault;
    replayId = localReplayInfoBean.getId();

    gameTitleLabel.setText(localReplayInfoBean.getTitle());
    gameTypeLabel.setText(localReplayInfoBean.getGameType());
    gameMapLabel.setText(localReplayInfoBean.getMap());
    playerCountLabel.setText(String.format("%d", localReplayInfoBean.getPlayerCount()));
/*    likesLabel.setText(String.format("%d", localReplayInfoBean.getLikes()));
    downloadsLabel.setText(String.format("%d", localReplayInfoBean.getDownloads()));*/
    if (localReplayInfoBean.getEndTime() == null || localReplayInfoBean.getStartTime() == null) {
      durationLabel.setText(i18n.get("replayVault.unknownDuration"));
    } else {
      Duration duration = Duration.between(localReplayInfoBean.getStartTime(), localReplayInfoBean.getEndTime());
      durationLabel.setText(timeService.shortDuration(duration));
    }
    mapImageView.setImage(mapService.loadSmallPreview(localReplayInfoBean.getMap()));
  }

  @FXML
  void onClick(MouseEvent event) {
    if (event.getClickCount() == 2) {
      // TODO error handling, FIXME do something?
      // TODO automatically download replay if local is corrupted
      switch (selectedReplayVault) {
        case LOCAL:
          replaceService.runReplay(localReplayInfoBean);
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
