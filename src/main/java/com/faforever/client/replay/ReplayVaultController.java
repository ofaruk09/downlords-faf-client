package com.faforever.client.replay;

import com.faforever.client.i18n.I18n;
import com.faforever.client.notification.Action;
import com.faforever.client.notification.NotificationService;
import com.faforever.client.notification.PersistentNotification;
import com.faforever.client.notification.Severity;
import com.faforever.client.reporting.ReportingService;
import com.faforever.client.task.TaskService;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class ReplayVaultController {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @FXML
  StackPane replayVaultRoot;
  @FXML
  VBox contentPane;
  @FXML
  VBox loadingPane;
  @FXML
  TextField mapField;
  @FXML
  MenuButton gameTypeMenu;
  @FXML
  TextField playerOneField;
  @FXML
  TextField playerTwoField;
  @FXML
  TextField playerOneFactionField;
  @FXML
  TextField playerTwoFactionField;
  @FXML
  TextField maxRatingField;
  @FXML
  TextField minRatingField;
  @FXML
  MenuButton selectRatingTypeMenu;
  @FXML
  MenuItem globalRatingTypeMenuItem;
  @FXML
  MenuItem ladderRatingTypeMenuItem;
  @FXML
  MenuButton sortByMenu;
  @FXML
  MenuItem sortByDateMenuItem;
  @FXML
  MenuItem sortByMostLikedMenuItem;
  @FXML
  MenuItem sortByMostDownloadedMenuItem;
  @FXML
  MenuItem sortByHighestAverageGlobalRatingMenuItem;
  @FXML
  MenuItem sortByHighestAverageLadderRatingMenuItem;
  @FXML
  CheckBox displayWinnersCheckBox;
  @FXML
  VBox searchResultGroup;
  @FXML
  FlowPane searchResultPane;

  @Resource
  ApplicationContext applicationContext;
  @Resource
  TaskService taskService;
  @Resource
  NotificationService notificationService;
  @Resource
  I18n i18n;
  @Resource
  ReportingService reportingService;
  @Resource
  ReplayService replayService;

  @PostConstruct
  void postConstruct() {

  }

  public CompletableFuture<Void> loadLocalReplaysInBackground() {
    LoadLocalReplaysTask task = applicationContext.getBean(LoadLocalReplaysTask.class);

    return taskService.submitTask(task)
        .thenAccept(this::addLocalReplays)
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

  public void loadOnlineReplaysInBackground() {
    replayService.getOnlineReplays()
        .thenAccept(this::addOnlineReplays)
        .exceptionally(throwable -> {
          logger.warn("Error while loading online replays", throwable);
          notificationService.addNotification(new PersistentNotification(
              i18n.get("replayVault.loadingOnlineTask.failed"),
              Severity.ERROR,
              Collections.singletonList(new Action(i18n.get("report"), event -> reportingService.reportError(throwable)))
          ));
          return null;
        });
  }

  @FXML
  void onGlobalRatingType(ActionEvent actionEvent) {
  }

  @FXML
  void onLadderRatingType(ActionEvent actionEvent) {
  }

  @FXML
  void onSortByDate(ActionEvent actionEvent) {
  }

  @FXML
  void onSortByMostLiked(ActionEvent actionEvent) {
  }

  @FXML
  void onSortByMostDownloaded(ActionEvent actionEvent) {
  }

  @FXML
  void onHighestAverageGlobalRating(ActionEvent actionEvent) {
  }

  @FXML
  void onHighestAverageLadderRating(ActionEvent actionEvent) {
  }

  @FXML
  void onReset(ActionEvent actionEvent) {
  }

  @FXML
  void onSearchReplay(ActionEvent actionEvent) {
  }

  @FXML
  void onAdvancedSearchFields(Event event) {
  }

  @FXML
  Node getRoot() {
    return replayVaultRoot;
  }

}
