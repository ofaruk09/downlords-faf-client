package com.faforever.client.chat;

import com.faforever.client.audio.AudioService;
import com.faforever.client.fx.WebViewConfigurer;
import com.faforever.client.game.Game;
import com.faforever.client.game.JoinGameHelper;
import com.faforever.client.map.MapService;
import com.faforever.client.map.MapServiceImpl.PreviewSize;
import com.faforever.client.notification.ImmediateNotification;
import com.faforever.client.notification.Severity;
import com.faforever.client.player.Player;
import com.faforever.client.preferences.ChatPrefs;
import com.faforever.client.remote.domain.GameStatus;
import com.faforever.client.replay.ReplayService;
import com.faforever.client.util.IdenticonUtil;
import com.google.common.annotations.VisibleForTesting;
import com.neovisionaries.i18n.CountryCode;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import static com.faforever.client.chat.SocialStatus.FOE;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PrivateChatTabController extends AbstractChatTabController {

  private final AudioService audioService;
  private final ChatService chatService;
  private final CountryFlagService countryFlagService;
  private final MapService mapService;
  private final WebViewConfigurer webViewConfigurer;
  private final JoinGameHelper joinGameHelper;
  private final ReplayService replayService;
  public Tab privateChatTabRoot;
  public WebView messagesWebView;
  public TextInputControl messageTextField;
  public ImageView userImageView;
  public Label usernameLabel;
  public ImageView countryImageView;
  public Label countryLabel;
  public Label ratingLabel;
  public Label gamesPlayedLabel;
  public Label inGameLabel;
  public ImageView mapPreview;
  public Label gameTitleLabel;
  public VBox gameHostVBox;
  public Label gameHostLabel;
  public VBox gamePreview;
  public Label gamePlayerCountLabel;
  public Label featuredModLabel;
  public Button joinSpectateButton;

  private Game userGame;
  private Player userPlayer;
  private boolean userOffline;

  @Inject
  public PrivateChatTabController(AudioService audioService, ChatService chatService, CountryFlagService countryFlagService, MapService mapService, WebViewConfigurer webViewConfigurer, JoinGameHelper joinGameHelper, ReplayService replayService) {
    this.audioService = audioService;
    this.chatService = chatService;
    this.countryFlagService = countryFlagService;
    this.mapService = mapService;
    this.webViewConfigurer = webViewConfigurer;
    this.joinGameHelper = joinGameHelper;
    this.replayService = replayService;
  }

  public boolean isUserOffline() {
    return userOffline;
  }

  @Override
  public Tab getRoot() {
    return privateChatTabRoot;
  }

  @Override
  public void setReceiver(String username) {
    super.setReceiver(username);
    privateChatTabRoot.setId(username);
    privateChatTabRoot.setText(username);

    //Load receiver information
    //username = playerService.getCurrentPlayer().getUsername();//TODO: THIS IS FOR TESTING PURPOSES ONLY; REMOVE!
    userPlayer = playerService.getPlayerForUsername(username);
    /*/TODO---------------------------------
    Game game = new Game();
    game.setHost("TestHost");
    game.setId(1234);
    game.setMapFolderName("10 The Pass");
    game.setMaxPlayers(10);
    game.setNumPlayers(7);
    game.setStatus(GameStatus.OPEN);
    game.setFeaturedMod("FAF Develop");
    game.setTitle("TestTitle");
    userPlayer.setGame(game);*/
    //-------------------------------------
    if (userPlayer != null) {
      CountryCode countryCode = CountryCode.getByCode(userPlayer.getCountry());

      usernameLabel.setText(username);
      userImageView.setImage(IdenticonUtil.createIdenticon(userPlayer.getId()));
      countryImageView.setImage(countryFlagService.loadCountryFlag(userPlayer.getCountry()));
      countryLabel.setText(countryCode == null ? userPlayer.getCountry() : countryCode.getName());
      loadReceiverRatingInformation(userPlayer);
      userPlayer.globalRatingMeanProperty().addListener((observable, oldValue, newValue) -> loadReceiverRatingInformation(userPlayer));
      gamesPlayedLabel.textProperty().bind(userPlayer.numberOfGamesProperty().asString());
      loadPlayerGameInformation(userPlayer.getGame());
      userPlayer.gameProperty().addListener((observable, oldValue, newValue) -> {
        loadPlayerGameInformation(newValue);
        System.out.println("game changed " + newValue + " " + userPlayer.getGame());
      });
    }
  }//TODO: fix chat pane

  private void loadReceiverRatingInformation(Player player) {
    ratingLabel.setText(Math.round(player.getGlobalRatingMean()) + " +/- " + Math.round(player.getGlobalRatingDeviation()));
  }

  private void loadPlayerGameInformation(Game game) {
    this.userGame = game;

    setIsInGameLabel();

    gameTitleLabel.textProperty().unbind();

    if (game != null) {
      gamePreview.setVisible(true);
      gamePreview.setManaged(true);

      game.statusProperty().addListener((observable, oldValue, newValue) -> {
        setIsInGameLabel();
        setJoinSpectateButton();
        gameHostVBox.setManaged(false);
        gameHostVBox.setVisible(false);
      });
      setJoinSpectateButton();

      gameTitleLabel.textProperty().bind(game.titleProperty());

      loadMapPreview(game.getMapFolderName());
      game.mapFolderNameProperty().addListener((observable, oldValue, newValue) -> loadMapPreview(newValue));

      InvalidationListener playerCountListener = (observable) -> gamePlayerCountLabel.setText(game.getNumPlayers() + "/" + game.getMaxPlayers());
      playerCountListener.invalidated(null);
      game.numPlayersProperty().addListener(playerCountListener);
      game.maxPlayersProperty().addListener(playerCountListener);

      featuredModLabel.setText(game.getFeaturedMod());

      if (game.getStatus() == GameStatus.OPEN) {
        gameHostVBox.setManaged(true);
        gameHostVBox.setVisible(true);
        gameHostLabel.setText(game.getHost());
      }

    } else {
      gamePreview.setVisible(false);
      gamePreview.setManaged(false);
    }
  }

  private void setIsInGameLabel() {
    if (userGame != null && userGame.getStatus() == GameStatus.OPEN) {
      inGameLabel.setText(i18n.get("game.gameStatus.lobby"));
    } else if (userGame != null && userGame.getStatus() == GameStatus.PLAYING) {
      inGameLabel.setText(i18n.get("game.gameStatus.playing"));
    } else {
      inGameLabel.setText(i18n.get("chat.privateMessage.gamestate.notInGame"));
    }
  }

  private void setJoinSpectateButton() {
    if (userGame.getStatus() == GameStatus.OPEN) {
      joinSpectateButton.setText(i18n.get("game.join"));
    } else if (userGame.getStatus() == GameStatus.PLAYING) {
      joinSpectateButton.setText(i18n.get("game.spectate"));
    }
  }

  private void loadMapPreview(String mapName) {
    CompletableFuture.runAsync(() -> mapPreview.setImage(mapService.loadPreview(mapName, PreviewSize.SMALL)));
  }

  @FXML
  private void joinSpectateGameButtonClicked(ActionEvent event) {
    if (userGame.getStatus() == GameStatus.OPEN) {
      joinGameHelper.join(userGame);
    } else if (userGame.getStatus() == GameStatus.PLAYING) {
      replayService.runLiveReplay(userGame.getId(), userPlayer.getId());
    } else {
      notificationService.addNotification(new ImmediateNotification(i18n.get("chat.privateMessage.game.joinSpectateError.title"), i18n.get("chat.privateMessage.game.joinSpectateError.text"), Severity.ERROR));
    }
  }

  public void initialize() {
    super.initialize();
    userOffline = false;
    chatService.addChatUsersByNameListener(change -> {
      if (change.wasRemoved()) {
        onPlayerDisconnected(change.getKey(), change.getValueRemoved());
      }
      if (change.wasAdded()) {
        onPlayerConnected(change.getKey(), change.getValueRemoved());
      }
    });
    webViewConfigurer.configureWebView(messagesWebView);
  }

  @Override
  protected TextInputControl getMessageTextField() {
    return messageTextField;
  }

  @Override
  protected WebView getMessagesWebView() {
    return messagesWebView;
  }

  @Override
  public void onChatMessage(ChatMessage chatMessage) {
    Player player = playerService.getPlayerForUsername(chatMessage.getUsername());
    ChatPrefs chatPrefs = preferencesService.getPreferences().getChat();

    if (player != null && player.getSocialStatus() == FOE && chatPrefs.getHideFoeMessages()) {
      return;
    }

    super.onChatMessage(chatMessage);

    if (!hasFocus()) {
      audioService.playPrivateMessageSound();
      showNotificationIfNecessary(chatMessage);
      setUnread(true);
      incrementUnreadMessagesCount(1);
    }
  }

  @VisibleForTesting
  void onPlayerDisconnected(String userName, ChatUser userItem) {
    if (userName.equals(getReceiver())) {
      userOffline = true;
      Platform.runLater(() -> onChatMessage(new ChatMessage(userName, Instant.now(), i18n.get("chat.operator") + ":", i18n.get("chat.privateMessage.playerLeft", userName), true)));
    }
  }

  @VisibleForTesting
  void onPlayerConnected(String userName, ChatUser userItem) {
    if (userOffline && userName.equals(getReceiver())) {
      userOffline = false;
      Platform.runLater(() -> onChatMessage(new ChatMessage(userName, Instant.now(), i18n.get("chat.operator") + ":", i18n.get("chat.privateMessage.playerReconnect", userName), true)));
    }
  }
}
