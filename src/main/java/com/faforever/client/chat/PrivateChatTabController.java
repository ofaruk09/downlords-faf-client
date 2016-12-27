package com.faforever.client.chat;

import com.faforever.client.audio.AudioService;
import com.faforever.client.fx.WebViewConfigurer;
import com.faforever.client.game.Game;
import com.faforever.client.map.MapService;
import com.faforever.client.map.MapServiceImpl.PreviewSize;
import com.faforever.client.player.Player;
import com.faforever.client.preferences.ChatPrefs;
import com.faforever.client.remote.domain.GameState;
import com.faforever.client.util.IdenticonUtil;
import com.google.common.annotations.VisibleForTesting;
import com.neovisionaries.i18n.CountryCode;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.Instant;

import static com.faforever.client.chat.SocialStatus.FOE;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PrivateChatTabController extends AbstractChatTabController {

  private final AudioService audioService;
  private final ChatService chatService;
  private final CountryFlagService countryFlagService;
  private final MapService mapService;
  private final WebViewConfigurer webViewConfigurer;
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
  private boolean userOffline;

  @Inject
  public PrivateChatTabController(AudioService audioService, ChatService chatService, CountryFlagService countryFlagService, MapService mapService, WebViewConfigurer webViewConfigurer) {
    this.audioService = audioService;
    this.chatService = chatService;
    this.countryFlagService = countryFlagService;
    this.mapService = mapService;
    this.webViewConfigurer = webViewConfigurer;
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
    username = playerService.getCurrentPlayer().getUsername();//TODO: THIS IS FOR TESTING PURPOSES ONLY; REMOVE!
    final Player player = playerService.getPlayerForUsername(username);
    //TODO---------------------------------
    Game game = new Game();
    game.setHost("TestHost");
    game.setId(1234);
    game.setMapFolderName("10 The Pass");
    game.setMaxPlayers(10);
    game.setNumPlayers(7);
    game.setStatus(GameState.OPEN);
    game.setTitle("TestTitle");
    player.setGame(game);
    //-------------------------------------
    CountryCode countryCode = CountryCode.getByCode(player.getCountry());

    usernameLabel.setText(username);
    userImageView.setImage(IdenticonUtil.createIdenticon(player.getId()));
    countryImageView.setImage(countryFlagService.loadCountryFlag(player.getCountry()));
    countryLabel.setText(countryCode == null ? player.getCountry() : countryCode.getName());
    loadReceiverRatingInformation(player);
    player.globalRatingMeanProperty().addListener((observable, oldValue, newValue) -> loadReceiverRatingInformation(player));
    gamesPlayedLabel.textProperty().bind(player.numberOfGamesProperty().asString());
    loadPlayerGameInformation(player.getGame());
    player.gameProperty().addListener((observable, oldValue, newValue) -> {
      loadPlayerGameInformation(newValue);
    });
  }

  private void loadReceiverRatingInformation(Player player) {
    ratingLabel.setText((int) (Math.round(player.getGlobalRatingMean())) + " +/- " + (int) (Math.round(player.getGlobalRatingDeviation())));
  }

  private void loadPlayerGameInformation(Game game) {
    inGameLabel.setText(game != null ? (game.getStatus() == GameState.OPEN ? "In lobby" : "In game") : "Not in game");

    gameTitleLabel.textProperty().unbind();

    if (game != null) {
      gamePreview.setVisible(true);
      gamePreview.setManaged(true);

      game.statusProperty().addListener((observable, oldValue, newValue) -> {
        inGameLabel.setText(game != null ? (game.getStatus() == GameState.OPEN ? "In lobby" : "In game") : "Not in game");
        gameHostVBox.setManaged(false);
        gameHostVBox.setVisible(false);
      });

      gameTitleLabel.textProperty().bind(game.titleProperty());

      loadMapPreview(game.getMapFolderName());
      game.mapFolderNameProperty().addListener((observable, oldValue, newValue) -> loadMapPreview(newValue));

      InvalidationListener playerCountListener = (observable) -> gamePlayerCountLabel.setText(game.getNumPlayers() + "/" + game.getMaxPlayers());
      playerCountListener.invalidated(null);
      game.numPlayersProperty().addListener(playerCountListener);
      game.maxPlayersProperty().addListener(playerCountListener);

      if (game.getStatus() == GameState.OPEN) {
        gameHostVBox.setManaged(true);
        gameHostVBox.setVisible(true);
        gameHostLabel.setText(game.getHost());
      }
      //TODO: featured mod, join/spectate button
    } else {
      gamePreview.setManaged(false);
      gamePreview.setVisible(false);
    }
  }

  private void loadMapPreview(String mapName) {
    new Thread(() -> mapPreview.setImage(mapService.loadPreview(mapName, PreviewSize.SMALL))).start();//TODO: nope!
  }

  public void initialize() {
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
