package com.faforever.client.replay;

import com.faforever.client.i18n.I18n;
import com.faforever.client.notification.Action;
import com.faforever.client.notification.NotificationService;
import com.faforever.client.notification.PersistentNotification;
import com.faforever.client.notification.Severity;
import com.faforever.client.reporting.ReportingService;
import com.faforever.client.task.TaskService;
import com.faforever.client.util.ReplaySortingUtil;
import com.faforever.client.util.TimeService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LocalReplayVaultController {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


  @FXML
  VBox localReplayVaultRoot;
  @Resource
  TimeService timeService;
  @Resource
  TaskService taskService;
  @Resource
  NotificationService notificationService;
  @Resource
  ReportingService reportingService;
  @Resource
  I18n i18n;
  @Resource
  ApplicationContext applicationContext;

  public CompletableFuture<Void> loadLocalReplaysInBackground() {
    LoadLocalReplaysTask task = applicationContext.getBean(LoadLocalReplaysTask.class);

    return taskService.submitTask(task)
        .thenAccept(replayInfoBeans -> addLocalReplays(new ArrayList<>(replayInfoBeans)))
        .exceptionally(throwable -> {
              logger.warn("Error while loading local replays", throwable);
              notificationService.addNotification(new PersistentNotification(
                  i18n.get("replayVault.loadingLocalTask.failed"),
                  Severity.ERROR,
                  Collections.singletonList(new Action(i18n.get("report"), event -> reportingService.reportError(throwable)))
              ));
              return null;
            }
        );
  }

  private void addLocalReplays(List<ReplayInfoBean> replayInfoBeans) {
    if (replayInfoBeans.isEmpty()) {
      return;
    }

    ReplayVaultController replayVaultController = applicationContext.getBean(ReplayVaultController.class);

    List<List<ReplayInfoBean>> sortedReplayInfoBeansLists = new ArrayList<>();
    ReplaySortingOption replaySortingOption = replayVaultController.getReplaySortingOption();
    switch (replaySortingOption) {
      case DATE:
        sortedReplayInfoBeansLists = ReplaySortingUtil.sortByDate(replayInfoBeans);
        break;
      case MOST_LIKED:
        sortedReplayInfoBeansLists = ReplaySortingUtil.sortByMostLiked(replayInfoBeans);
        break;
      case MOST_DOWNLOADED:
        sortedReplayInfoBeansLists = ReplaySortingUtil.sortByMostDownloaded(replayInfoBeans);
        break;
      case HIGHEST_AVG_GLOB_RATING:
        sortedReplayInfoBeansLists = ReplaySortingUtil.sortByHighestAvgGlobRating(replayInfoBeans);
        break;
      case HIGHEST_AVG_LADDER_RATING:
        sortedReplayInfoBeansLists = ReplaySortingUtil.sortByHighestAvgLadderRating(replayInfoBeans);
        break;
    }
    for (List<ReplayInfoBean> sortedReplayInfoBeans : sortedReplayInfoBeansLists) {
      SortedReplaysController sortedReplaysController = applicationContext.getBean(SortedReplaysController.class);
      sortedReplaysController.setSortedValue(ReplaySortingUtil.getSortingValueForReplayInfoBean(sortedReplayInfoBeans.get(0), replaySortingOption));
      sortedReplaysController.addReplays(sortedReplayInfoBeans);
      localReplayVaultRoot.getChildren().add(sortedReplaysController.getRoot());
    }
  }


  public Node getRoot() {
    return localReplayVaultRoot;
  }
}
