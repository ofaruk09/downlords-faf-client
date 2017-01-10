package com.faforever.client.clan;

import com.faforever.client.fx.AbstractViewController;
import com.faforever.client.preferences.LoginPrefs;
import com.faforever.client.preferences.PreferencesService;
import com.google.common.base.Strings;
import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLButtonElement;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClanController extends AbstractViewController<Node> {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  public WebView clanRoot;
  @Inject
  PreferencesService preferencesService;
  private LoginPrefs login;
  @Value("${clanWebsite.url}")
  private String clanWebsiteUrl;
  @PostConstruct
  public void init() {
    login = preferencesService.getPreferences().getLogin();
  }

  public void onDisplay() {
    if (Strings.isNullOrEmpty(clanRoot.getEngine().getLocation())) {
      clanRoot.getEngine().load(clanWebsiteUrl);
      clanRoot.getEngine().setJavaScriptEnabled(true);
      clanRoot.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
        if (Worker.State.SUCCEEDED == newValue) {
          onSiteLoaded();
        }
      });
    }
  }

  public void onSiteLoaded() {
    try {
      org.w3c.dom.Document site = clanRoot.getEngine().getDocument();
      org.w3c.dom.Element username = site.getElementById("login_form_username_input");
      if (username == null) {
        logger.trace("usernameField not found. Is this the main Page?");
      } else {
        username.setAttribute("value", login.getUsername());
        NodeList elemtenList = site.getElementsByTagName("input");
        org.w3c.dom.Element passwordElement = (org.w3c.dom.Element) elemtenList.item(1);
        passwordElement.setAttribute("value", "");
        HTMLButtonElement button = (HTMLButtonElement) site.getElementsByTagName("button").item(0);

      }
    } catch (Exception e) {
      logger.warn("Consider this might be triggered also if another page then the front page is loaded", e);
    }
  }

  public Node getRoot() {
    return clanRoot;
  }
}


