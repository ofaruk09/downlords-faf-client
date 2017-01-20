package com.faforever.client.clan;


import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClanTooltipController implements com.faforever.client.fx.Controller<Node> {
  public VBox root;
  public Label clanName;
  public Label clanDescription;
  public Label clanMembers;
  public Label clanLeader;

  public void setClan(Clan clan) {
    clanName.setText(clan.getClanName());
    clanDescription.setText(clan.getDescription());
    clanLeader.setText(clan.getLeaderName());
    clanMembers.setText(clan.getClanMembers().toString());
  }

  public VBox getRoot() {
    return root;
  }

}
