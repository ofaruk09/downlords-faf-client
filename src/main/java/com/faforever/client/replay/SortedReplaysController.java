package com.faforever.client.replay;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.util.List;

public class SortedReplaysController {

  @FXML
  TitledPane sortedReplaysRoot;
  @FXML
  FlowPane replayContentPane;

  @Resource
  ApplicationContext applicationContext;

  private String sortedValue;

  public void addReplay(ReplayInfoBean replayInfoBean) {
    ReplayTileController replayTileController = applicationContext.getBean(ReplayTileController.class);
    replayTileController.setReplay(replayInfoBean);
    replayContentPane.getChildren().add(replayTileController.getRoot());
  }

  public void addReplays(List<ReplayInfoBean> replayInfoBeans) {
    replayInfoBeans.forEach(this::addReplay);
  }

  public String getSortedValue() {
    return sortedValue;
  }

  public void setSortedValue(String sortedValue) {
    this.sortedValue = sortedValue;
    sortedReplaysRoot.setText(sortedValue);
  }

  public Node getRoot() {
    return sortedReplaysRoot;
  }
}
