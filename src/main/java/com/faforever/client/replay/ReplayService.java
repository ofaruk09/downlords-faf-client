package com.faforever.client.replay;

import com.faforever.client.api.GameSearchFields;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ReplayService {

 CompletableFuture<Collection<ReplayInfoBean>> getLocalReplays();

 CompletableFuture<List<ReplayInfoBean>> getOnlineReplays();

 CompletableFuture<List<ReplayInfoBean>> getOnlineReplays(GameSearchFields gameSearchFields, int page, int size);

 void runReplay(ReplayInfoBean item);

  void runLiveReplay(int gameId, int playerId) throws IOException;

  void runLiveReplay(URI uri) throws IOException;

 CompletableFuture<Path> download(int id);
}
