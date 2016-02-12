package com.faforever.client.replay;

import com.faforever.client.i18n.I18n;
import com.faforever.client.notification.DismissAction;
import com.faforever.client.notification.ImmediateNotification;
import com.faforever.client.notification.NotificationService;
import com.faforever.client.notification.ReportAction;
import com.faforever.client.notification.Severity;
import com.faforever.client.preferences.PreferencesService;
import com.faforever.client.reporting.ReportingService;
import com.faforever.client.task.TaskService;
import com.faforever.client.util.TimeService;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.faforever.client.replay.SortedReplaysController.MAX_PER_PANE;
import static java.util.Arrays.asList;

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
  @Resource
  PreferencesService preferenceService;
  @Resource
  ReplayService replayService;

  private ObservableList<ReplayInfoBean> replayInfoBeans;
  private ObjectProperty<ReplaySortingOption> replaySortingOption;

  @PostConstruct
  void init() {
    replaySortingOption = preferenceService.getPreferences().getReplayVault().replaySortingOptionProperty();
  }

  public CompletableFuture<Void> loadLocalReplaysInBackground() {
    return replayService.getLocalReplays()
        .thenAccept(replayInfoBeans -> {
          this.replayInfoBeans = FXCollections.observableArrayList(replayInfoBeans);
          Platform.runLater(() -> sortLocalReplays(replayInfoBean -> true));
        })
        .exceptionally(throwable -> {
              logger.warn("Error while loading local replays", throwable);
          notificationService.addNotification(new ImmediateNotification(
              i18n.get("errorTitle"),
                  i18n.get("replayVault.loadingLocalTask.failed"),
              Severity.ERROR, throwable,
              asList(new ReportAction(i18n, reportingService, throwable), new DismissAction(i18n))
              ));
              return null;
            }
        );
  }

  public void sortLocalReplays(Predicate<ReplayInfoBean> replayInfoBeanPredicate) {
    localReplayVaultRoot.getChildren().clear();
    if (replayInfoBeans.isEmpty()) {
      return;
    }

    switch (replaySortingOption.get()) {
      case DATE:
        sortByDate(replayInfoBeanPredicate);
        break;
      case MOST_LIKED:
        sortByMostLiked(replayInfoBeanPredicate);
        break;
      case MOST_DOWNLOADED:
        sortByMostDownloaded(replayInfoBeanPredicate);
        break;
      case HIGHEST_AVG_GLOB_RATING:
        sortByHighestAvgGlobRating(replayInfoBeanPredicate);
        break;
      case HIGHEST_AVG_LADDER_RATING:
        sortByHighestAvgLadderRating(replayInfoBeanPredicate);
        break;
    }
  }

  private void sortByDate(Predicate<ReplayInfoBean> replayInfoBeanPredicate) {
    Collections.sort(replayInfoBeans, (replayInfoBean1, replayInfoBean2)
        -> replayInfoBean2.getStartTime().compareTo(replayInfoBean1.getStartTime())
    );

    Month currentMonth = null;
    int currentYear = 0;
    SortedReplaysController currentSortedReplaysController = null;

    List<ReplayInfoBean> filteredReplayInfoBeans = replayInfoBeans.filtered(replayInfoBeanPredicate);
    for (ReplayInfoBean replayInfoBean : filteredReplayInfoBeans) {
      LocalDate date = timeService.getLocalDateFromInstant(replayInfoBean.getStartTime());
      Month month = date.getMonth();
      int year = date.getYear();

      if (currentSortedReplaysController == null || month != currentMonth || year != currentYear) {
        currentMonth = month;
        currentYear = year;
        currentSortedReplaysController = createSortedReplayPane();
      }

      currentSortedReplaysController.addReplay(replayInfoBean);
    }
  }

  private SortedReplaysController createSortedReplayPane() {
    SortedReplaysController sortedReplaysController = applicationContext.getBean(SortedReplaysController.class);
    sortedReplaysController.setReplaySortingOption(replaySortingOption.get());
    localReplayVaultRoot.getChildren().add(sortedReplaysController.getRoot());
    return sortedReplaysController;
  }

  private void sortByMostLiked(Predicate<ReplayInfoBean> replayInfoBeanPredicate) {
    List<ReplayInfoBean> filteredReplayInfoBeans = replayInfoBeans.filtered(replayInfoBeanPredicate);
    Collections.sort(filteredReplayInfoBeans, (replayInfoBean1, replayInfoBean2) ->
        Integer.compare(replayInfoBean1.getLikes(), replayInfoBean2.getLikes()));

    addNumericallySortedReplays(replayInfoBeans);
  }

  private void sortByMostDownloaded(Predicate<ReplayInfoBean> replayInfoBeanPredicate) {
    List<ReplayInfoBean> filteredReplayInfoBeans = replayInfoBeans.filtered(replayInfoBeanPredicate);
    Collections.sort(filteredReplayInfoBeans, (replayInfoBean1, replayInfoBean2) ->
        Integer.compare(replayInfoBean1.getDownloads(), replayInfoBean2.getDownloads()));

    addNumericallySortedReplays(replayInfoBeans);
  }

  private void sortByHighestAvgGlobRating(Predicate<ReplayInfoBean> replayInfoBeanPredicate) {
  }

  private void sortByHighestAvgLadderRating(Predicate<ReplayInfoBean> replayInfoBeanPredicate) {
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
