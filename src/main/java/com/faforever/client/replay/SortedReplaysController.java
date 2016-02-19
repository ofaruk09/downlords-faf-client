package com.faforever.client.replay;

import com.faforever.client.i18n.I18n;
import com.faforever.client.util.Assert;
import com.faforever.client.util.TimeService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.faforever.client.replay.ReplaySortingOption.DATE;

public class SortedReplaysController {

  final public static int MAX_PER_PANE = 25;
  @FXML
  TitledPane sortedReplaysRoot;
  @FXML
  FlowPane replayContentPane;
  @Resource
  ApplicationContext applicationContext;
  @Resource
  I18n i18n;
  @Resource
  TimeService timeService;

  private ReplaySortingOption replaySortingOption;
  private int minValue;
  private int maxValue;
  private Collection<LocalReplayInfoBean> replays;
  private SelectedReplayVault selectedReplayVault;

  public SortedReplaysController() {
    replays = new LinkedList<>();
  }

  @FXML
  void initialize() {
    sortedReplaysRoot.expandedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        replays.forEach(SortedReplaysController.this::addTileForReplay);
      } else {
        replayContentPane.getChildren().clear();
      }
    });
  }

  public void setup(ReplaySortingOption replaySortingOption, SelectedReplayVault selectedReplayVault) {
    this.selectedReplayVault = selectedReplayVault;
    this.replaySortingOption = replaySortingOption;
  }

  public void addReplay(LocalReplayInfoBean localReplayInfoBean) {
    updateTitle(localReplayInfoBean);
    replays.add(localReplayInfoBean);

    if (sortedReplaysRoot.isExpanded()) {
      addTileForReplay(localReplayInfoBean);
    }
  }

  private void updateTitle(LocalReplayInfoBean localReplayInfoBean) {
    Assert.checkNullIllegalState(replaySortingOption, "Replay sorting option must be set before title is set");
    if (StringUtils.isNotEmpty(sortedReplaysRoot.getText())) {
      return;
    }

    String title;
    if (replaySortingOption == DATE) {
      title = timeService.timeAgo(localReplayInfoBean.getStartTime());
    } else {
      if (replayContentPane.getChildren().isEmpty()) {
        this.minValue = getSortingValueForReplayInfoBean(localReplayInfoBean);
      }
      this.maxValue = getSortingValueForReplayInfoBean(localReplayInfoBean);
      title = i18n.get("replayVault.titlePane.range", minValue, maxValue, i18n.get(replaySortingOption.getI18nKey()));
    }
    sortedReplaysRoot.setText(title);
  }

  private void addTileForReplay(LocalReplayInfoBean localReplayInfoBean) {
    ReplayTileController replayTileController = applicationContext.getBean(ReplayTileController.class);
    replayTileController.setReplay(localReplayInfoBean, selectedReplayVault);
    replayContentPane.getChildren().add(replayTileController.getRoot());
  }

  private int getSortingValueForReplayInfoBean(LocalReplayInfoBean localReplayInfoBean) {
    switch (replaySortingOption) {
/*      case MOST_LIKED:
        return localReplayInfoBean.getLikes();
      case MOST_DOWNLOADED:
        return localReplayInfoBean.getDownloads();*/
      case HIGHEST_AVG_GLOB_RATING:
      case HIGHEST_AVG_LADDER_RATING:
        // FIXME @Aulex I guess this should not be empty?
    }
    throw new IllegalArgumentException(String.format("Unsupported ReplaySortingOption enum: %s", replaySortingOption.toString()));
  }

  public void addReplays(List<LocalReplayInfoBean> localReplayInfoBeans) {
    localReplayInfoBeans.forEach(this::addReplay);
  }

  public Node getRoot() {
    return sortedReplaysRoot;
  }
}
