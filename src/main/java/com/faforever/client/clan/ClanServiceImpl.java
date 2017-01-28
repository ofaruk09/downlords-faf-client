package com.faforever.client.clan;

import com.faforever.client.remote.FafService;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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

import static com.github.nocatch.NoCatch.noCatch;

@Lazy
@Service
public class ClanServiceImpl implements ClanService {
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final FafService fafService;

  private Future<ConcurrentHashMap<String, Clan>> clanByTagFuture;

  @Inject
  public ClanServiceImpl(FafService fafService) {
    this.fafService = fafService;
  }

  @PostConstruct
  public void init() {
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Callable<List<Clan>> clanCall = fafService::getClans;
    Future<List<Clan>> clans = executorService.submit(clanCall);

    Callable<ConcurrentHashMap<String, Clan>> byTagCall = () -> {
      ConcurrentHashMap<String, Clan> resultMap = new ConcurrentHashMap<String, Clan>();
      clans.get().forEach(clanItem -> resultMap.put(clanItem.getClanTag(), clanItem));
      return resultMap;
    };

    clanByTagFuture = executorService.submit(byTagCall);
    executorService.shutdown();
  }

  @Override
  public Clan getClanByTag(String tag) {
    return noCatch(() -> {

      if (clanByTagFuture.get(60, TimeUnit.SECONDS).containsKey(tag)) {
        return clanByTagFuture.get().get(tag);
      } else {
        logger.warn("Clan with tag: {} not found, Consider there are currently issues with the API (15.01.2017)", tag);
        return null;
      }
    });
  }

}


