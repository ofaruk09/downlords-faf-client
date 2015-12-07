package com.faforever.client.replay;

import com.faforever.client.i18n.I18n;
import com.faforever.client.notification.Action;
import com.faforever.client.notification.NotificationService;
import com.faforever.client.notification.PersistentNotification;
import com.faforever.client.notification.Severity;
import com.faforever.client.reporting.ReportingService;
import com.faforever.client.task.TaskService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class LocalReplayVaultController {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  @FXML
  FlowPane replayPane;
  @FXML
  ScrollPane localReplayVaultRoot;

  @Resource
  ApplicationContext applicationContext;
  @Resource
  TaskService taskService;
  @Resource
  NotificationService notificationService;
  @Resource
  ReportingService reportingService;
  @Resource
  I18n i18n;

  public Node getRoot() {
    return localReplayVaultRoot;
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

  private void addLocalReplays(Collection<ReplayInfoBean> replayInfoBeans) {
    replayInfoBeans.forEach(replayInfoBean -> {
      ReplayTileController replayTileController = applicationContext.getBean(ReplayTileController.class);
      replayTileController.setReplay(replayInfoBean);
      replayPane.getChildren().add(replayTileController.getRoot());
    });
  }
}
