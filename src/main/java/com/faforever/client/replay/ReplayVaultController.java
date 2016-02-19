package com.faforever.client.replay;

import com.faforever.client.fx.JavaFxUtil;
import com.faforever.client.i18n.I18n;
import com.faforever.client.notification.NotificationService;
import com.faforever.client.preferences.PreferencesService;
import com.faforever.client.preferences.ReplayVaultPrefs;
import com.faforever.client.reporting.ReportingService;
import com.faforever.client.task.TaskService;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import static com.faforever.client.replay.RatingType.GLOBAL;
import static com.faforever.client.replay.RatingType.LADDER;
import static com.faforever.client.replay.ReplaySortingOption.DATE;
import static com.faforever.client.replay.ReplaySortingOption.HIGHEST_AVG_GLOB_RATING;
import static com.faforever.client.replay.ReplaySortingOption.HIGHEST_AVG_LADDER_RATING;
import static com.faforever.client.replay.ReplaySortingOption.MOST_DOWNLOADED;
import static com.faforever.client.replay.ReplaySortingOption.MOST_LIKED;
import static com.faforever.client.replay.SelectedReplayVault.LIVE;
import static com.faforever.client.replay.SelectedReplayVault.LOCAL;
import static com.faforever.client.replay.SelectedReplayVault.ONLINE;

public class ReplayVaultController {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  public MenuButton replayVaultSelectorMenu;
  public MenuItem localReplayVaultSelectorMenuItem;
  public MenuItem onlineReplayVaultSelectorMenuItem;
  public MenuItem liveReplayVaultSelectorMenuItem;
  @FXML
  TextArea playersField;
  @FXML
  StackPane replayVaultRoot;
  @FXML
  ScrollPane contentPane;
  @FXML
  VBox loadingPane;
  @FXML
  TextField mapField;
  @FXML
  MenuButton gameTypeMenu;
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
  @Resource
  PreferencesService preferencesService;
  //FIXME: track in preferences
  private boolean initialized = false;

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

    sortByMenu.setText(i18n.get("replayVault.sortBy.date"));
    setRatingFieldPromptText();
    addRatingTypePropertyListener();

    setPane();
  }

  private void enterLoadingState() {
    contentPane.setVisible(false);
    loadingPane.setVisible(true);
  }

  private void setRatingFieldPromptText() {
    ReplayVaultPrefs replayVaultPrefs = preferencesService.getPreferences().getReplayVault();

    if (replayVaultPrefs.getRatingType() == LADDER) {
      selectRatingTypeMenu.setText(i18n.get("replayVault.selectRatingType.ladder"));
      maxRatingField.setPromptText(i18n.get("replayVault.maxLadderRating"));
      minRatingField.setPromptText(i18n.get("replayVault.minLadderRating"));
    } else {
      selectRatingTypeMenu.setText(i18n.get("replayVault.selectRatingType.global"));
      maxRatingField.setPromptText(i18n.get("replayVault.maxGlobalRating"));
      minRatingField.setPromptText(i18n.get("replayVault.minGlobalRating"));
    }

  }

  private void addRatingTypePropertyListener() {
    ReplayVaultPrefs replayVaultPrefs = preferencesService.getPreferences().getReplayVault();
    replayVaultPrefs.ratingTypeProperty().addListener(observable -> {
      setRatingFieldPromptText();
    });
  }


  private void setPane() {
    ReplayVaultPrefs replayVaultPrefs = preferencesService.getPreferences().getReplayVault();
    JavaFxUtil.bindOnApplicationThread(sortByMenu.textProperty(), () -> i18n.get(preferencesService.getPreferences().getReplayVault().getReplaySortingOption().getI18nKey()), replayVaultPrefs.replaySortingOptionProperty());
    JavaFxUtil.bindOnApplicationThread(replayVaultSelectorMenu.textProperty(), () -> i18n.get(preferencesService.getPreferences().getReplayVault().getSelectedReplayVault().getI18nKey()), replayVaultPrefs.selectedReplayVaultProperty());

    CompletableFuture<Void> voidCompletableFuture = null;
    switch (replayVaultPrefs.getSelectedReplayVault()) {
      case LOCAL:
        LocalReplayVaultController localReplayVaultController = applicationContext.getBean(LocalReplayVaultController.class);
        voidCompletableFuture = localReplayVaultController.loadLocalReplaysInBackground();
//        onSortByDate(new ActionEvent());
        sortByMenu.setDisable(true);
        contentPane.setContent(localReplayVaultController.getRoot());
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
    preferencesService.getPreferences().getReplayVault().setRatingType(GLOBAL);
  }

  @FXML
  void onLadderRatingType(ActionEvent actionEvent) {
    preferencesService.getPreferences().getReplayVault().setRatingType(LADDER);
  }

  @FXML
  void onSortByDate(ActionEvent actionEvent) {
    preferencesService.getPreferences().getReplayVault().setReplaySortingOption(DATE);
  }

  @FXML
  void onSortByMostLiked(ActionEvent actionEvent) {
    preferencesService.getPreferences().getReplayVault().setReplaySortingOption(MOST_LIKED);
  }

  @FXML
  void onSortByMostDownloaded(ActionEvent actionEvent) {
    preferencesService.getPreferences().getReplayVault().setReplaySortingOption(MOST_DOWNLOADED);
  }

  @FXML
  void onHighestAverageGlobalRating(ActionEvent actionEvent) {
    preferencesService.getPreferences().getReplayVault().setReplaySortingOption(HIGHEST_AVG_GLOB_RATING);
  }

  @FXML
  void onHighestAverageLadderRating(ActionEvent actionEvent) {
    preferencesService.getPreferences().getReplayVault().setReplaySortingOption(HIGHEST_AVG_LADDER_RATING);
  }

  @FXML
  void onReset(ActionEvent actionEvent) {
    playersField.clear();
    mapField.clear();
    maxRatingField.clear();
    minRatingField.clear();
  }

  @FXML
  void onSearchReplay(ActionEvent actionEvent) {
    Map<String, String> playerFactionMap = parsePlayersField();
    Predicate<LocalReplayInfoBean> replayInfoBeanPredicate = replayInfoBean -> {
      for (String player : playerFactionMap.keySet()) {
        for (List<String> team : replayInfoBean.getTeams().values()) {
          if (!team.stream().filter(new Predicate<String>() {
            @Override
            public boolean test(String s) {
              return s.equalsIgnoreCase(player);
            }
          }).findFirst().isPresent()) {
            return false;
          }
        }
      }
      return true;
    };
    LocalReplayVaultController localReplayVaultController = applicationContext.getBean(LocalReplayVaultController.class);
    localReplayVaultController.sortLocalReplays(replayInfoBeanPredicate);
  }

  private Map<String, String> parsePlayersField() {
    if (playersField.getText().isEmpty()) {
      return Collections.emptyMap();
    }
    SelectedReplayVault selectedReplayVault = preferencesService.getPreferences().getReplayVault().getSelectedReplayVault();
    Map<String, String> playerFactionMap = new HashMap<>();
    String[] playersFactions = playersField.getText().trim().split(",");
    for (String playerFaction : playersFactions) {
      String[] playerFactionPair = playerFaction.split(":");
      if (selectedReplayVault == ONLINE && playerFactionPair.length == 2) {
        playerFactionMap.put(playerFactionPair[0], playerFactionPair[1]);
      } else {
        playerFactionMap.put(playerFactionPair[0], "");
      }
    }

    return playerFactionMap;
  }

  @FXML
  void onAdvancedSearchFields(Event event) {
  }

  @FXML
  void onLocalReplayVault(ActionEvent actionEvent) {
    preferencesService.getPreferences().getReplayVault().setSelectedReplayVault(LOCAL);
  }

  @FXML
  void onOnlineReplayVault(ActionEvent actionEvent) {
    preferencesService.getPreferences().getReplayVault().setSelectedReplayVault(ONLINE);

  }

  @FXML
  void onLiveReplayVault(ActionEvent actionEvent) {
    preferencesService.getPreferences().getReplayVault().setSelectedReplayVault(LIVE);
  }

  public Node getRoot() {
    return replayVaultRoot;
  }
}
