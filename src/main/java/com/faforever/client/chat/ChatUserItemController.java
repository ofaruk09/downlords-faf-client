package com.faforever.client.chat;

import com.faforever.client.chat.avatar.AvatarService;
import com.faforever.client.clan.Clan;
import com.faforever.client.clan.ClanService;
import com.faforever.client.clan.ClanTooltipController;
import com.faforever.client.fx.Controller;
import com.faforever.client.fx.JavaFxUtil;
import com.faforever.client.fx.PlatformService;
import com.faforever.client.game.JoinGameHelper;
import com.faforever.client.game.PlayerStatus;
import com.faforever.client.i18n.I18n;
import com.faforever.client.player.Player;
import com.faforever.client.player.PlayerService;
import com.faforever.client.preferences.ChatPrefs;
import com.faforever.client.preferences.PreferencesService;
import com.faforever.client.replay.ReplayService;
import com.faforever.client.theme.UiService;
import com.google.common.eventbus.EventBus;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.WeakMapChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.PopupWindow;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static com.faforever.client.chat.ChatColorMode.CUSTOM;
import static com.faforever.client.chat.SocialStatus.SELF;
import static com.faforever.client.game.PlayerStatus.IDLE;
import static com.faforever.client.util.RatingUtil.getGlobalRating;
import static com.faforever.client.util.RatingUtil.getLeaderboardRating;
import static java.time.Instant.now;
import static java.util.Locale.US;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
// TODO null safety for "player"
public class ChatUserItemController implements Controller<Node> {

  private static final String CLAN_TAG_FORMAT = "[%s]";
  private static final PseudoClass PRESENCE_STATUS_ONLINE = PseudoClass.getPseudoClass("online");
  private static final PseudoClass PRESENCE_STATUS_IDLE = PseudoClass.getPseudoClass("idle");
  private final AvatarService avatarService;
  private final CountryFlagService countryFlagService;
  private final PreferencesService preferencesService;
  private final ChatService chatService;
  private final I18n i18n;
  private final UiService uiService;
  private final JoinGameHelper joinGameHelper;
  private final EventBus eventBus;
  public Pane chatUserItemRoot;
  public ImageView countryImageView;
  public ImageView avatarImageView;
  public Label usernameLabel;
  public MenuButton clanMenu;
  public Label statusLabel;
  public Text presenceStatusIndicator;
  public Label clanLabel;
  private Player player;
  private boolean colorsAllowedInPane;
  private ChangeListener<ChatColorMode> colorModeChangeListener;
  private MapChangeListener<? super String, ? super Color> colorPerUserMapChangeListener;
  private ChangeListener<String> avatarChangeListener;
  private ChangeListener<String> clanChangeListener;
  private ChangeListener<PlayerStatus> gameStatusChangeListener;
  private InvalidationListener userActivityListener;
  private ClanService clanService;
  private Clan clan;
  private PlayerService playerService;
  private PlatformService platformService;
  private String baseClanWebsite;
  private boolean closedByClick = false;
  private InvalidationListener closingClanMenuListener;


  @Inject
  // TODO reduce dependencies, rely on eventBus instead
  public ChatUserItemController(PreferencesService preferencesService, AvatarService avatarService,
                                CountryFlagService countryFlagService, ChatService chatService,
                                ReplayService replayService, I18n i18n, UiService uiService,
                                JoinGameHelper joinGameHelper, EventBus eventBus, ClanService clanService,
                                PlayerService playerService, PlatformService platformService,
                                @Value("${clan.clanWebpagesBaseUrl}") String baseClanWebsite) {

    this.preferencesService = preferencesService;
    this.avatarService = avatarService;
    this.playerService = playerService;
    this.clanService = clanService;
    this.countryFlagService = countryFlagService;
    this.chatService = chatService;
    this.i18n = i18n;
    this.uiService = uiService;
    this.joinGameHelper = joinGameHelper;
    this.eventBus = eventBus;
    this.platformService = platformService;
    this.baseClanWebsite = baseClanWebsite;

  }

  public void initialize() {
    userActivityListener = (observable) -> Platform.runLater(this::onUserActivity);

    // TODO until server side support is available, the precense status is initially set to "unknown" until the user
    // does something
    presenceStatusIndicator.setText("\uF10C");
    setIdle(false);

    chatUserItemRoot.setUserData(this);
    countryImageView.managedProperty().bind(countryImageView.visibleProperty());
    countryImageView.setVisible(false);
    statusLabel.managedProperty().bind(statusLabel.visibleProperty());
    statusLabel.visibleProperty().bind(statusLabel.textProperty().isNotEmpty());

    closingClanMenuListener = observable -> {
      if (clanMenu.isShowing() || closedByClick) {
        closedByClick = false;
        return;
      }
      deflate(clanMenu);
      inflate(clanLabel);
    };
    clanMenu.showingProperty().addListener(closingClanMenuListener);

    ChatPrefs chatPrefs = preferencesService.getPreferences().getChat();

    colorModeChangeListener = (observable, oldValue, newValue) -> configureColor();
    colorPerUserMapChangeListener = change -> {
      String lowerUsername = player.getUsername().toLowerCase(US);
      if (lowerUsername.equalsIgnoreCase(change.getKey())) {
        Color newColor = chatPrefs.getUserToColor().get(lowerUsername);
        assignColor(newColor);
      }
    };
    avatarChangeListener = (observable, oldValue, newValue) -> Platform.runLater(() -> setAvatarUrl(newValue));
    clanChangeListener = (observable, oldValue, newValue) -> Platform.runLater(() -> setClanTag(newValue));
    gameStatusChangeListener = (observable, oldValue, newValue) -> Platform.runLater(this::updateGameStatus);
    joinGameHelper.setParentNode(getRoot());
  }

  public void onContextMenuRequested(ContextMenuEvent event) {
    ChatUserContextMenuController contextMenuController = uiService.loadFxml("theme/chat/chat_user_context_menu.fxml");
    contextMenuController.setPlayer(player);
    contextMenuController.getContextMenu().show(chatUserItemRoot.getScene().getWindow(), event.getScreenX(), event.getScreenY());
  }

  public void onUsernameClicked(MouseEvent mouseEvent) {
    if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
      eventBus.post(new InitiatePrivateChatEvent(player.getUsername()));
    }
  }

  public void onClanLeaderRequested() {
    if (clan != null || playerService.isOnline(clan.getLeaderName())) {
      eventBus.post(new InitiatePrivateChatEvent(clan.getLeaderName()));
    }
  }


  private void configureColor() {
    ChatPrefs chatPrefs = preferencesService.getPreferences().getChat();

    if (player.getSocialStatus() == SELF) {
      usernameLabel.getStyleClass().add(SELF.getCssClass());
      clanMenu.getStyleClass().add(SELF.getCssClass());
      return;
    }

    Color color = null;
    String lowerUsername = player.getUsername().toLowerCase(US);
    ChatUser chatUser = chatService.getOrCreateChatUser(lowerUsername);

    if (chatPrefs.getChatColorMode() == CUSTOM) {
      synchronized (chatPrefs.getUserToColor()) {
        if (chatPrefs.getUserToColor().containsKey(lowerUsername)) {
          color = chatPrefs.getUserToColor().get(lowerUsername);
        }

        chatPrefs.getUserToColor().addListener(new WeakMapChangeListener<>(colorPerUserMapChangeListener));
      }
    } else if (chatPrefs.getChatColorMode() == ChatColorMode.RANDOM && colorsAllowedInPane) {
      color = ColorGeneratorUtil.generateRandomColor(chatUser.getUsername().hashCode());
    }

    chatUser.setColor(color);
    assignColor(color);
  }

  private void assignColor(Color color) {
    if (color != null) {
      usernameLabel.setStyle(String.format("-fx-text-fill: %s", JavaFxUtil.toRgbCode(color)));
      clanMenu.setStyle(String.format("-fx-text-fill: %s", JavaFxUtil.toRgbCode(color)));
    } else {
      usernameLabel.setStyle("");
      clanMenu.setStyle("");
    }
  }

  private void setAvatarUrl(@Nullable String avatarUrl) {
    if (StringUtils.isEmpty(avatarUrl)) {
      avatarImageView.setVisible(false);
    } else {
      CompletableFuture.supplyAsync(() -> avatarService.loadAvatar(avatarUrl)).thenAccept(image -> avatarImageView.setImage(image));
      avatarImageView.setVisible(true);
    }
  }

  private void setClanTag(String newValue) {
    // code to test can be inserted here set new Value to some existing clanTag and the players clan...player.setClan("TAG")
    if (StringUtils.isEmpty(newValue)) {
      clanLabel.setVisible(false);
      return;
    }
    if (usernameLabel.getTooltip() != null || clanMenu.getTooltip() != null) {
      usernameLabel.setTooltip(null);
      clanMenu.setTooltip(null);
    }
    clanMenu.setOnMousePressed(event -> {
      event.consume();
      if (clanMenu.isShowing()) {
        closedByClick = true;
        clanMenu.hide();
        return;
      }
      clanMenu.show();
    });
    clanLabel.setVisible(true);
    clanMenu.setText(String.format(CLAN_TAG_FORMAT, newValue));
    clanLabel.setText(String.format(CLAN_TAG_FORMAT, newValue));


  }

  private void updateGameStatus() {
    switch (player.getStatus()) {
      case IDLE:
        statusLabel.setText("");
        break;
      case HOSTING:
        statusLabel.setText(i18n.get("user.status.hosting", player.getGame().getTitle()));
        break;
      case LOBBYING:
        statusLabel.setText(i18n.get("user.status.waiting", player.getGame().getTitle()));
        break;
      case PLAYING:
        statusLabel.setText(i18n.get("user.status.playing", player.getGame().getTitle()));
        break;
    }
  }

  public Pane getRoot() {
    return chatUserItemRoot;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;

    configureColor();
    addChatColorModeListener();
    configureCountryImageView();
    configureAvatarImageView();
    configureClanMenu();
    configureGameStatusView();

    usernameLabel.setText(player.getUsername());
    player.idleSinceProperty().addListener(new WeakInvalidationListener(userActivityListener));
    player.statusProperty().addListener(new WeakInvalidationListener(userActivityListener));
  }

  private void addChatColorModeListener() {
    ChatPrefs chatPrefs = preferencesService.getPreferences().getChat();
    synchronized (chatPrefs.chatColorModeProperty()) {
      chatPrefs.chatColorModeProperty().addListener(new WeakChangeListener<>(colorModeChangeListener));
    }
  }

  private void configureCountryImageView() {
    setCountry(player.getCountry());

    countryImageView.setVisible(true);

    Tooltip countryTooltip = new Tooltip(player.getCountry());
    countryTooltip.textProperty().bind(player.countryProperty());

    Tooltip.install(countryImageView, countryTooltip);
  }

  private void configureAvatarImageView() {
    player.avatarUrlProperty().addListener(new WeakChangeListener<>(avatarChangeListener));
    setAvatarUrl(player.getAvatarUrl());

    Tooltip avatarTooltip = new Tooltip(player.getAvatarTooltip());
    avatarTooltip.textProperty().bind(player.avatarTooltipProperty());
    avatarTooltip.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_LEFT);

    Tooltip.install(avatarImageView, avatarTooltip);
  }

  private void configureClanMenu() {
    //code to test as api is bugged: if(player.getUsername().equals("axel12"))player.setClan("DEV");
    setClanTag(player.getClan());
    player.clanProperty().addListener(new WeakChangeListener<>(clanChangeListener));
  }

  private void configureGameStatusView() {
    player.statusProperty().addListener(new WeakChangeListener<>(gameStatusChangeListener));
    updateGameStatus();
  }

  private void setCountry(String country) {
    if (StringUtils.isEmpty(country)) {
      countryImageView.setVisible(false);
    } else {
      countryImageView.setImage(countryFlagService.loadCountryFlag(country));
      countryImageView.setVisible(true);
    }
  }

  public void onMouseEnterUsername() {
    if (player == null || player.getChatOnly() || usernameLabel.getTooltip() != null) {
      return;
    }
    Tooltip tooltip = new Tooltip();
    usernameLabel.setTooltip(tooltip);
    tooltip.textProperty().bind(Bindings.createStringBinding(
        () -> i18n.get("userInfo.ratingFormat", getGlobalRating(player), getLeaderboardRating(player)),
        player.leaderboardRatingMeanProperty(), player.leaderboardRatingDeviationProperty(),
        player.globalRatingMeanProperty(), player.globalRatingDeviationProperty()
    ));

    CompletableFuture<Clan> clanCompletableFutureForTooltip = CompletableFuture.supplyAsync(() -> clanService.getClanByTag(player.getClan()));
    clanCompletableFutureForTooltip.thenAccept(clan1 -> {
      clan = clan1;
      if (clanMenu.getTooltip() != null || clan == null || player.getClan().isEmpty()) {
        return;
      }
        Tooltip clanTooltip = new Tooltip();
        clanMenu.setTooltip(clanTooltip);
        if (clan.getDescription() == null) {
          clan.setDescription("-");
        }
      ClanTooltipController clanTooltipController = uiService.loadFxml("theme/chat/clan_tooltip.fxml");
        clanTooltipController.setClan(clan);
        clanTooltip.setMaxHeight(clanTooltipController.getRoot().getHeight());
        clanTooltip.setGraphic(clanTooltipController.getRoot());

      MenuItem page = new MenuItem(i18n.get("clan.visitPage"));
      page.setOnAction(event -> {
        platformService.showDocument(baseClanWebsite + clan.getClanId());
        // TODO: Could be viewed in clan section (if implemented)
      });
      if (!playerService.isOnline(clan.getLeaderName())) {
        clanMenu.getItems().addAll(FXCollections.observableArrayList(page));
        return;
      }
      MenuItem toLeader = new MenuItem(i18n.get("clan.toLeader"));
      toLeader.setOnAction(event -> onClanLeaderRequested());
      clanMenu.getItems().addAll(FXCollections.observableArrayList(toLeader, page));
    });

  }

  void setColorsAllowedInPane(boolean colorsAllowedInPane) {
    this.colorsAllowedInPane = colorsAllowedInPane;
    configureColor();
  }

  public void setVisible(boolean visible) {
    chatUserItemRoot.setVisible(visible);
    chatUserItemRoot.setManaged(visible);
  }

  /**
   * Updates the displayed idle indicator (online/idle). This is called from outside in order to only have one timer per
   * channel, instead of one timer per chat user.
   */
  void updatePresenceStatusIndicator() {
    JavaFxUtil.assertApplicationThread();

    if (player == null || player.getStatus() != IDLE) {
      setIdle(false);
      return;
    }
    int idleThreshold = preferencesService.getPreferences().getChat().getIdleThreshold();
    setIdle(player.getIdleSince().isBefore(now().minus(Duration.ofMinutes(idleThreshold))));
  }
  private void setIdle(boolean idle) {
    presenceStatusIndicator.pseudoClassStateChanged(PRESENCE_STATUS_ONLINE, !idle);
    presenceStatusIndicator.pseudoClassStateChanged(PRESENCE_STATUS_IDLE, idle);
    if (idle) {
      // TODO only until server-side support
      presenceStatusIndicator.setText("\uF111");
    }
  }

  private void onUserActivity() {
    // TODO only until server-side support
    presenceStatusIndicator.setText("\uF111");
    updatePresenceStatusIndicator();
  }

  public void onMouseEnterTag() {
    onMouseEnterUsername();
    inflate(clanMenu);
    deflate(clanLabel);

  }

  public void onMouseExitedTag() {
    if (clanMenu.isShowing()) {
      return;
    }
    deflate(clanMenu);
    inflate(clanLabel);
  }

  private void inflate(Control control) {
    control.setMinWidth(Region.USE_PREF_SIZE);
    control.setMaxHeight(Region.USE_PREF_SIZE);
    control.setPrefWidth(Region.USE_COMPUTED_SIZE);
    control.setVisible(true);
  }

  private void deflate(Control control) {
    control.setMinWidth(0);
    control.setMaxHeight(0);
    control.setPrefWidth(0);
    control.setVisible(false);
  }

}
