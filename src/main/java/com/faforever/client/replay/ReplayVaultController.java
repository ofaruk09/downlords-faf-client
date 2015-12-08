package com.faforever.client.replay;

import com.faforever.client.i18n.I18n;
import com.faforever.client.notification.NotificationService;
import com.faforever.client.reporting.ReportingService;
import com.faforever.client.task.TaskService;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.CompletableFuture;

public class ReplayVaultController {

  private enum SelectedPane {
    LOCAL, ONLINE, LIVE
  }
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  public Label replayVaultHeaderLabel;
  public MenuButton replayVaultSelectorMenu;
  public MenuItem localReplayVaultSelectorMenuItem;
  public MenuItem onlineReplayVaultSelectorMenuItem;
  public MenuItem liveReplayVaultSelectorMenuItem;
  @FXML
  StackPane replayVaultRoot;
  @FXML
  Pane contentPane;
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
  //FIXME: track in preferences
  private boolean ladderRatingSelected = true;
  private boolean initialized = false;
  private SelectedPane selectedPane = SelectedPane.LOCAL;
  private ReplaySortingOption replay = ReplaySortingOption.MOST_LIKED;

  public ReplaySortingOption getReplaySortingOption() {
    return replay;
  }

  @FXML
  void initialize() {
    loadingPane.managedProperty().bind(loadingPane.visibleProperty());
  }

  public void setUpIfNecessary() {
    if (initialized) {
      return;
    }
    initialized = true;
    enterLoadingState();

    playerOneField.setPromptText(i18n.get("replayVault.player", 1));
    playerTwoField.setPromptText(i18n.get("replayVault.player", 2));

    playerOneFactionField.setPromptText(i18n.get("replayVault.player.faction", 1));
    playerTwoFactionField.setPromptText(i18n.get("replayVault.player.faction", 2));

    sortByMenu.setText(i18n.get("replayVault.sortBy.date"));
    setRatingFieldPromptText();
    setPane();
  }

  private void enterLoadingState() {
    contentPane.setVisible(false);
    loadingPane.setVisible(true);
  }

  private void setRatingFieldPromptText() {
    if (ladderRatingSelected) {
      selectRatingTypeMenu.setText(i18n.get("replayVault.selectRatingType.ladder"));
      maxRatingField.setPromptText(i18n.get("replayVault.maxLadderRating"));
      minRatingField.setPromptText(i18n.get("replayVault.minLadderRating"));
    } else {
      selectRatingTypeMenu.setText(i18n.get("replayVault.selectRatingType.global"));
      maxRatingField.setPromptText(i18n.get("replayVault.maxGlobalRating"));
      minRatingField.setPromptText(i18n.get("replayVault.minLadderRating"));
    }
  }

  private void setPane() {
    CompletableFuture<Void> voidCompletableFuture = null;
    switch (selectedPane) {
      case LOCAL:
        replayVaultSelectorMenu.setText(i18n.get("replayVault.localReplays"));
        LocalReplayVaultController localReplayVaultController = applicationContext.getBean(LocalReplayVaultController.class);
        contentPane.getChildren().add(localReplayVaultController.getRoot());
        voidCompletableFuture = localReplayVaultController.loadLocalReplaysInBackground();
        break;
      case ONLINE:
        break;
      case LIVE:
        break;
    }
    voidCompletableFuture.thenAccept(aVoid -> {
      loadingPane.setVisible(false);
      contentPane.setVisible(true);
    });
  }

/*  public void loadOnlineReplaysInBackground() {
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
  }*/

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
  void onLocalReplayVault(ActionEvent actionEvent) {
  }

  @FXML
  void onOnlineReplayVault(ActionEvent actionEvent) {
  }

  @FXML
  void onLiveReplayVault(ActionEvent actionEvent) {
  }

  public Node getRoot() {
    return replayVaultRoot;
  }
}
