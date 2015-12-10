package com.faforever.client.replay;

import com.faforever.client.i18n.I18n;
import com.faforever.client.notification.Action;
import com.faforever.client.notification.NotificationService;
import com.faforever.client.notification.PersistentNotification;
import com.faforever.client.notification.Severity;
import com.faforever.client.reporting.ReportingService;
import com.faforever.client.task.TaskService;
import com.faforever.client.util.TimeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.faforever.client.replay.SortedReplaysController.MAX_PER_PANE;

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
  ObservableList<ReplayInfoBean> replayInfoBeans;
  private ReplaySortingOption replaySortingOption;

  public void setReplaySortingOption(ReplaySortingOption replaySortingOption) {
    this.replaySortingOption = replaySortingOption;
  }

  public CompletableFuture<Void> loadLocalReplaysInBackground() {
    LoadLocalReplaysTask task = applicationContext.getBean(LoadLocalReplaysTask.class);

    return taskService.submitTask(task)
        .thenAccept(replayInfoBeans -> {
          this.replayInfoBeans = FXCollections.observableArrayList(replayInfoBeans);
          addLocalReplays();
        })
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

  private void addLocalReplays() {
    if (replayInfoBeans.isEmpty()) {
      return;
    }

    switch (replaySortingOption) {
      case DATE:
        sortByDate();
        break;
      case MOST_LIKED:
        sortByMostLiked();
        break;
      case MOST_DOWNLOADED:
        sortByMostDownloaded();
        break;
      case HIGHEST_AVG_GLOB_RATING:
        sortByHighestAvgGlobRating();
        break;
      case HIGHEST_AVG_LADDER_RATING:
        sortByHighestAvgLadderRating();
        break;
    }
  }

  public void sortByDate() {
    Collections.sort(replayInfoBeans, (replayInfoBean1, replayInfoBean2) -> {
      LocalDate replayInfoBean1LocalDate = timeService.getLocalDateFromInstant(replayInfoBean1.getStartTime());
      LocalDate replayInfoBean2LocalDate = timeService.getLocalDateFromInstant(replayInfoBean2.getStartTime());
      return replayInfoBean1LocalDate.compareTo(replayInfoBean2LocalDate);
    });

    LocalDate currentDate;
    SortedReplaysController currentSortedReplaysController = null;
    LocalDate previousDate = null;

    for (ReplayInfoBean replayInfoBean : replayInfoBeans) {
      currentDate = timeService.getLocalDateFromInstant(replayInfoBean.getStartTime());

      if (!currentDate.equals(previousDate)) {
        currentSortedReplaysController = createSortedReplayPane();
      }
      currentSortedReplaysController.addReplay(replayInfoBean);
      previousDate = currentDate;
    }
  }

  private SortedReplaysController createSortedReplayPane() {
    SortedReplaysController sortedReplaysController = applicationContext.getBean(SortedReplaysController.class);
    sortedReplaysController.setReplaySortingOption(replaySortingOption);
    localReplayVaultRoot.getChildren().add(sortedReplaysController.getRoot());
    return sortedReplaysController;
  }

  public void sortByMostLiked() {
    Collections.sort(replayInfoBeans, (replayInfoBean1, replayInfoBean2) ->
        Integer.compare(replayInfoBean1.getLikes(), replayInfoBean2.getLikes()));

    addNumericallySortedReplays(replayInfoBeans);
  }

  public void sortByMostDownloaded() {
    Collections.sort(replayInfoBeans, (replayInfoBean1, replayInfoBean2) ->
        Integer.compare(replayInfoBean1.getDownloads(), replayInfoBean2.getDownloads()));

    addNumericallySortedReplays(replayInfoBeans);
  }

  public void sortByHighestAvgGlobRating() {
  }

  public void sortByHighestAvgLadderRating() {
  }

  private void addNumericallySortedReplays(List<ReplayInfoBean> replayInfoBeans) {
    AtomicInteger counter = new AtomicInteger();
    Map<String, List<ReplayInfoBean>> splitReplayInfoBeans = replayInfoBeans.stream().collect(Collectors.groupingBy(replayInfoBean ->
        String.valueOf(counter.incrementAndGet() / MAX_PER_PANE * MAX_PER_PANE)));
    splitReplayInfoBeans.forEach((s, separatedReplayInfoBeans) -> createSortedReplayPane().addReplays(separatedReplayInfoBeans));
  }


  public Node getRoot() {
    return localReplayVaultRoot;
  }
}
