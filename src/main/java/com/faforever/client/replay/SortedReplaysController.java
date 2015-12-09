package com.faforever.client.replay;

import com.faforever.client.i18n.I18n;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.util.List;

public class SortedReplaysController {

  final public static int MAX_PER_PANE = 25;
  private static ReplaySortingOption replaySortingOption;
  @FXML
  TitledPane sortedReplaysRoot;
  @FXML
  FlowPane replayContentPane;
  @Resource
  ApplicationContext applicationContext;
  @Resource
  I18n i18n;
  String headOfTitle;
  private boolean first = true;

  public void setReplaySortingOption(ReplaySortingOption replaySortingOption) {
    SortedReplaysController.replaySortingOption = replaySortingOption;
  }

  public void addReplay(ReplayInfoBean replayInfoBean) {
    if (first) {
      headOfTitle = i18n.get("replayVault.titlePane.range.front", getSortingValueForReplayInfoBean(replayInfoBean));
      first = false;
    }

    ReplayTileController replayTileController = applicationContext.getBean(ReplayTileController.class);
    replayTileController.setReplay(replayInfoBean);
    replayContentPane.getChildren().add(replayTileController.getRoot());
    String endOfTitle = i18n.get(
        "replayVault.titlePane.range.end",
        getSortingValueForReplayInfoBean(replayInfoBean),
        i18n.get(replaySortingOption.getI18nKey()));
    sortedReplaysRoot.setText(String.format("%s %s", headOfTitle, endOfTitle));
  }

  private int getSortingValueForReplayInfoBean(ReplayInfoBean replayInfoBean) {
    switch (replaySortingOption) {
      case MOST_LIKED:
        return replayInfoBean.getLikes();
      case MOST_DOWNLOADED:
        return replayInfoBean.getDownloads();
      case HIGHEST_AVG_GLOB_RATING:
      case HIGHEST_AVG_LADDER_RATING:
    }
    throw new IllegalArgumentException(String.format("Unsupported ReplaySortingOption enum: %s", replaySortingOption.toString()));
  }

  public void addReplays(List<ReplayInfoBean> replayInfoBeans) {
    replayInfoBeans.forEach(this::addReplay);
  }

  public Node getRoot() {
    return sortedReplaysRoot;
  }
}
