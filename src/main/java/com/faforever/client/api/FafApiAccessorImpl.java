package com.faforever.client.api;

import com.faforever.client.config.CacheNames;
import com.faforever.client.leaderboard.Ranked1v1EntryBean;
import com.faforever.client.mod.ModInfoBean;
import com.faforever.client.net.UriUtil;
import com.faforever.client.preferences.PreferencesService;
import com.faforever.client.replay.ReplayInfoBean;
import com.faforever.client.user.UserService;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.JsonToken;
import com.google.api.client.util.Key;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.api.client.json.JsonToken.END_ARRAY;
import static com.google.api.client.json.JsonToken.END_OBJECT;

public class FafApiAccessorImpl implements FafApiAccessor {

  private static final String HTTP_LOCALHOST = "http://localhost:";
  private static final String ENCODED_HTTP_LOCALHOST = HTTP_LOCALHOST.replace(":", "%3A").replace("/", "%2F");
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String SCOPE_READ_ACHIEVEMENTS = "read_achievements";
  private static final String SCOPE_READ_EVENTS = "read_events";
  private static final int MAX_PAGE_SIZE = 1000;
  private static final int DEFAULT_GAME_PAGE_SIZE = 100;
  private static Map<String, Class<?>> typeMap;

  static {
    typeMap = new HashMap<>();
    typeMap.put("achievement", AchievementDefinition.class);
    //TODO Implement
    typeMap.put("event", null);
    typeMap.put("game_stats", GameStats.class);
    typeMap.put("game_player_stats", GamePlayerStats.class);
    //TODO Implement
    typeMap.put("map", null);
    typeMap.put("mod", Mod.class);
    typeMap.put("player_achievement", PlayerAchievement.class);
    typeMap.put("player_event", PlayerEvent.class);
    //TODO Implement
    typeMap.put("player", null);
    typeMap.put("ranked1v1", Ranked1v1Stats.class);
    typeMap.put("ranked1v1_stats", Ranked1v1Stats.class);
  }

  @Resource
  JsonFactory jsonFactory;
  @Resource
  PreferencesService preferencesService;
  @Resource
  ExecutorService executorService;
  @Resource
  HttpTransport httpTransport;
  @Resource
  UserService userService;
  @Resource
  ClientHttpRequestFactory clientHttpRequestFactory;
  @Value("${api.baseUrl}")
  String baseUrl;
  @Value("${oauth.authUri}")
  String oAuthUrl;
  @Value("${oauth.tokenUri}")
  String oAuthTokenServerUrl;
  @Value("${oauth.clientId}")
  String oAuthClientId;
  @Value("${oauth.clientSecret}")
  String oAuthClientSecret;
  @Value("${oauth.loginUri}")
  URI oAuthLoginUrl;
  @VisibleForTesting
  Credential credential;
  @VisibleForTesting
  HttpRequestFactory requestFactory;
  private FileDataStoreFactory dataStoreFactory;

  @PostConstruct
  void postConstruct() throws IOException {
    Path playServicesDirectory = preferencesService.getPreferencesDirectory().resolve("oauth");
    dataStoreFactory = new FileDataStoreFactory(playServicesDirectory.toFile());
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<PlayerAchievement> getPlayerAchievements(int playerId) {
    logger.debug("Loading achievements for player: {}", playerId);
    return getMany("/players/" + playerId + "/achievements", 1);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<PlayerEvent> getPlayerEvents(int playerId) {
    logger.debug("Loading events for player: {}", playerId);
    return getMany("/players/" + playerId + "/events", 1);
  }

  @Override
  @SuppressWarnings("unchecked")
  @Cacheable(CacheNames.ACHIEVEMENTS)
  public List<AchievementDefinition> getAchievementDefinitions() {
    logger.debug("Loading achievement definitions");
    return getMany("/achievements?sort=order", 1);
  }

  @Override
  @Cacheable(CacheNames.ACHIEVEMENTS)
  public AchievementDefinition getAchievementDefinition(String achievementId) {
    logger.debug("Getting definition for achievement {}", achievementId);
    return getSingle("/achievements/" + achievementId);
  }

  @Override
  public void authorize(int playerId) {
    try {
      AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(
          BearerToken.authorizationHeaderAccessMethod(),
          httpTransport,
          jsonFactory,
          new GenericUrl(oAuthTokenServerUrl),
          new ClientParametersAuthentication(oAuthClientId, oAuthClientSecret),
          oAuthClientId,
          oAuthUrl)
          .setDataStoreFactory(dataStoreFactory)
          .setScopes(Arrays.asList(SCOPE_READ_ACHIEVEMENTS, SCOPE_READ_EVENTS))
          .build();

      credential = authorize(flow, String.valueOf(playerId));
      requestFactory = httpTransport.createRequestFactory(credential);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  @Cacheable(CacheNames.MODS)
  public List<ModInfoBean> getMods() {
    logger.debug("Loading available mods");
    return getMany("/mods").stream()
        .map(mod -> ModInfoBean.fromModInfo((Mod) mod))
        .collect(Collectors.toList());
  }

  @Override
  public List<ReplayInfoBean> getGames() {
    logger.debug("Loading unfiltered online replays");
    return getMany("/games", 0, DEFAULT_GAME_PAGE_SIZE).stream()
        .map(gameStats -> ((GameStats) gameStats).toReplayInfoBean())
        .collect(Collectors.toList());
  }

  @Override
  public List<ReplayInfoBean> getGames(GameSearchFields gameSearchFields, int page, int size) {
    logger.debug("Loading filtered online replays");
    return getMany("/games" + gameSearchFields.toString(), page, size).stream()
        .map(gameStats -> ((GameStats) gameStats).toReplayInfoBean())
        .collect(Collectors.toList());
  }

  @Override
  public List<Ranked1v1EntryBean> getRanked1v1Entries() {
    return getMany("/ranked1v1?filter[is_active]=true").stream()
        .map(leaderboardEntry -> Ranked1v1EntryBean.fromLeaderboardEntry((LeaderboardEntry) leaderboardEntry))
        .collect(Collectors.toList());
  }

  @Override
  public Ranked1v1Stats getRanked1v1Stats() {
    return getSingle("/ranked1v1/stats");
  }

  @Override
  public Ranked1v1EntryBean getRanked1v1EntryForPlayer(int playerId) {
    return Ranked1v1EntryBean.fromLeaderboardEntry(getSingle("/ranked1v1/" + playerId));
  }

  private Credential authorize(AuthorizationCodeFlow flow, String userId) throws IOException {
    Credential credential = flow.loadCredential(userId);
    if (credential != null && (credential.getRefreshToken() != null || credential.getExpiresInSeconds() > 60)) {
      return credential;
    }

    // The redirect URI is irrelevant to this implementation, however the server requires one
    String redirectUri = "http://localhost:1337";
    AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);

    // Google's GenericUrl does not escape ":" and "/", but Flask (FAF's OAuth) requires it.
    URI fixedAuthorizationUri = UriUtil.fromString(authorizationUrl.build()
        .replaceFirst("uri=" + Pattern.quote(HTTP_LOCALHOST), "uri=" + ENCODED_HTTP_LOCALHOST));

    Escaper escaper = UrlEscapers.urlFormParameterEscaper();
    byte[] postData = ("username=" + escaper.escape(userService.getUsername()) +
        "&password=" + escaper.escape(userService.getPassword()) +
        "&next=" + fixedAuthorizationUri).getBytes(StandardCharsets.UTF_8);
    int postDataLength = postData.length;

    ClientHttpRequest loginRequest = clientHttpRequestFactory.createRequest(oAuthLoginUrl, HttpMethod.POST);
    loginRequest.getHeaders().add("Content-Length", Integer.toString(postDataLength));
    try (DataOutputStream outputStream = new DataOutputStream(loginRequest.getBody())) {
      outputStream.write(postData);
    }
    ClientHttpResponse loginResponse = loginRequest.execute();

    if (!loginResponse.getStatusCode().is3xxRedirection()) {
      throw new RuntimeException("Could not log in (" + loginResponse.getStatusCode() + ")");
    }

    String cookie = Joiner.on("").join(loginResponse.getHeaders().get("set-cookie"));

    postData = "allow".getBytes(StandardCharsets.UTF_8);
    postDataLength = postData.length;

    ClientHttpRequest authorizeRequest = clientHttpRequestFactory.createRequest(fixedAuthorizationUri, HttpMethod.POST);
    authorizeRequest.getHeaders().add("Content-Length", Integer.toString(postDataLength));
    authorizeRequest.getHeaders().add("Cookie", cookie);
    try (DataOutputStream outputStream = new DataOutputStream(authorizeRequest.getBody())) {
      outputStream.write(postData);
    }
    ClientHttpResponse authorizeResponse = authorizeRequest.execute();

    if (!authorizeResponse.getStatusCode().is3xxRedirection()) {
      throw new RuntimeException("Could not authorize (" + authorizeResponse.getStatusCode() + ")");
    }

    URI redirectLocation = authorizeResponse.getHeaders().getLocation();
    String code = UriComponentsBuilder.fromUri(redirectLocation).build().getQueryParams().get("code").get(0);

    TokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();

    return flow.createAndStoreCredential(tokenResponse, userId);
  }

  private <T> List<T> getMany(String endpointPath) {
    List<T> result = new LinkedList<>();
    List<T> current = null;
    int page = 1;
    while (current == null || !current.isEmpty()) {
      current = getMany(endpointPath, page++);
      result.addAll(current);
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private <T> T getSingle(String endpointPath) {
    try (InputStream inputStream = executeGet(endpointPath)) {
      JsonParser jsonParser = jsonFactory.createJsonParser(inputStream, StandardCharsets.UTF_8);
      jsonParser.nextToken();
      jsonParser.skipToKey("data");
      return extractJsonObject(jsonParser);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private <T> List<T> getMany(String endpointPath, int page, int size) {
    String innerEndpointPath = endpointPath;
    if (size > MAX_PAGE_SIZE) {
      size = MAX_PAGE_SIZE;
    }

    innerEndpointPath += endpointPath.contains("?") ? "&" : "?";
    innerEndpointPath += "page[size]=" + size;

    return getMany(innerEndpointPath, page);
  }


  @SuppressWarnings("unchecked")
  private <T> List<T> getMany(String endpointPath, int page) {
    String innerEndpointPath = endpointPath;
    if (page > 0) {
      innerEndpointPath += endpointPath.contains("?") ? "&" : "?";
      innerEndpointPath += "page[number]=" + page;
    }

    try (InputStream inputStream = executeGet(innerEndpointPath)) {
      JsonParser jsonParser = jsonFactory.createJsonParser(inputStream, StandardCharsets.UTF_8);
      jsonParser.nextToken();
      jsonParser.skipToKey("data");
      return extractJsonArray(jsonParser);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private InputStream executeGet(String endpointPath) throws IOException {
    if (requestFactory == null) {
      throw new IllegalStateException("authorize() must be called first");
    }
    String url = baseUrl + endpointPath;
    logger.trace("Calling: {}", url);
    HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(url));
    credential.initialize(request);
    return request.execute().getContent();
  }

  private <T> List<T> extractJsonArray(JsonParser jsonParser) throws IOException {
    List<T> objects = new ArrayList<>();
    JsonToken currentToken = jsonParser.getCurrentToken();
    while (currentToken != null && currentToken != END_ARRAY) {
      switch (currentToken) {
        case START_OBJECT:
          objects.add(extractJsonObject(jsonParser));
          break;
      }
      currentToken = jsonParser.nextToken();
    }
    return objects;
  }

  private <T> T extractJsonObject(JsonParser jsonParser) throws IOException {
    T object;
    JsonToken currentToken = jsonParser.getCurrentToken();
    Map<String, String> fields = new HashMap<>();
    while (currentToken != null && currentToken != END_OBJECT) {
      switch (currentToken) {
        case FIELD_NAME:
          try {
            String fieldName = jsonParser.getCurrentName();
            JsonToken nextToken = jsonParser.nextToken();
            switch (nextToken) {
              case START_OBJECT:
                fields.put(fieldName, stringifyJsonObjectFromParser(jsonParser));
                break;
              case NOT_AVAILABLE:
                break;
              default:
                fields.put(fieldName, jsonParser.getText());
                break;
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
      }
      currentToken = jsonParser.nextToken();
    }

    Class<T> type = (Class<T>) typeMap.get(fields.get("type"));
    try {
      if (!fields.containsKey("attributes")) {
        return null;
      }
      object = jsonFactory.fromString(fields.get("attributes"), type);
      Field idField = ReflectionUtils.findField(type, "id");
      ReflectionUtils.makeAccessible(idField);
      idField.set(object, fields.get("id"));
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    if (fields.containsKey("relationships")) {
      JsonParser relationshipsParser = jsonFactory.createJsonParser(fields.get("relationships"));
      relationshipsParser.nextToken();
      currentToken = relationshipsParser.getCurrentToken();
      while (currentToken != null && currentToken != END_OBJECT) {
        switch (currentToken) {
          case FIELD_NAME:
            String currentFieldName = relationshipsParser.getCurrentName();
            relationshipsParser.nextToken();
            ReflectionUtils.doWithFields(type,
                field -> {
                  field.setAccessible(true);
                  try {
                    if (field.getType().isAssignableFrom(List.class)) {
                      List<Object> players = extractJsonArray(relationshipsParser);
                      if (players.get(0) == null) {
                        players = new ArrayList<>();
                      }
                      field.set(object, players);
                    } else {
                      field.set(object, extractJsonObject(relationshipsParser));
                    }
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                  }
                },
                field -> {
                  Key annotation = field.getAnnotation(Key.class);
                  return annotation != null
                      && (annotation.value().equals("##default") && currentFieldName.equals(field.getName())
                      || !annotation.value().equals("##default") && currentFieldName.equals(annotation.value()));
                });
        }
        currentToken = relationshipsParser.nextToken();
      }
    }
    return object;
  }

  private String stringifyJsonObjectFromParser(JsonParser jsonParser) throws IOException {
    int objectsCount = 0;
    StringBuilder stringBuilder = new StringBuilder();
    do {
      switch (jsonParser.getCurrentToken()) {
        case START_OBJECT:
          stringBuilder.append(jsonParser.getText());
          objectsCount++;
          break;
        case END_OBJECT:
          stringBuilder.deleteCharAt(stringBuilder.length() - 1);
          stringBuilder.append(String.format("%s,", jsonParser.getText()));
          objectsCount--;
          break;
        case START_ARRAY:
          stringBuilder.append(jsonParser.getText());
          break;
        case END_ARRAY:
          if (stringBuilder.lastIndexOf("[") != stringBuilder.length() - 1) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
          }
          stringBuilder.append(String.format("%s,", jsonParser.getText()));
          break;
        case FIELD_NAME:
          stringBuilder.append(String.format("\"%s\": ", jsonParser.getText()));
          break;
        case VALUE_STRING:
          stringBuilder.append(String.format("\"%s\",", jsonParser.getText().replace("\\", "\\\\").replace("\"", "\\\"")));
          break;
        case VALUE_NUMBER_INT:
        case VALUE_NUMBER_FLOAT:
        case VALUE_TRUE:
        case VALUE_FALSE:
        case VALUE_NULL:
          stringBuilder.append(String.format("%s,", jsonParser.getText()));
          break;
      }
      if (objectsCount != 0) {
        jsonParser.nextToken();
      }
    } while (objectsCount != 0);
    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    return stringBuilder.toString();
  }
}
