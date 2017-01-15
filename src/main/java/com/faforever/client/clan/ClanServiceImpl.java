package com.faforever.client.clan;

import com.faforever.client.remote.FafService;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Lazy
@Service
public class ClanServiceImpl implements ClanService {
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  public FafService fafService;

  private Future<ConcurrentHashMap<String, Clan>> clanByTagFuture;

  @Inject
  public ClanServiceImpl(FafService fafService) {
    this.fafService = fafService;
  }

  @PostConstruct
  public void init() {
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Callable<List<Clan>> clanCall = () -> fafService.getClans();
    Future<List<Clan>> clans = executorService.submit(clanCall);

    Callable<ConcurrentHashMap<String, Clan>> byTagCall = new Callable<ConcurrentHashMap<String, Clan>>() {
      @Override
      public ConcurrentHashMap<String, Clan> call() throws Exception {
        ConcurrentHashMap<String, Clan> resultMap = new ConcurrentHashMap<String, Clan>();
        clans.get().forEach(clanItem -> resultMap.put(clanItem.getClanTag(), clanItem));
        return resultMap;
      }
    };
    clanByTagFuture = executorService.submit(byTagCall);
    executorService.shutdown();
  }

  @Override
  public Clan getClanByTag(@Nullable String tag) {
    try {
      if (clanByTagFuture.get(60, TimeUnit.SECONDS).containsKey(tag)) {
        return clanByTagFuture.get().get(tag);
      } else {
        logger.warn("Clan with tag: {} not found, Consider there are currently issues with the API (15.01.2017)", tag);
      }
    } catch (Exception e) {
      logger.error("Error acquiring Clan Information (Check your connection): ", e);
    }
    return null;
  }
}
