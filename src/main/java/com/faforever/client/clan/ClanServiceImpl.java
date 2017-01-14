package com.faforever.client.clan;

import com.faforever.client.remote.FafService;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;

@Lazy
@Service
public class ClanServiceImpl implements ClanService {
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  @Inject
  private FafService fafService;
  private List<Clan> clans;
  private HashMap<String, Clan> clansByTag;

  @Inject
  public ClanServiceImpl(FafService fafService) {
    this.fafService = fafService;
  }

  @PostConstruct
  public void init() {
    //can anybody help me, I know how to make a new Thread, but there seems to be some Class that handles Thread, how do I use it?
    clans = fafService.getClans();
    for (Clan clan : clans) {
      clansByTag.put(clan.getClanTag(), clan);
    }
  }

  @Override
  public Clan getClanByTag(@Nullable String tag) {
    if (clansByTag.containsKey(tag)) {
      return clansByTag.get(tag);
    } else {
      logger.warn("Clan with tag: {} not found", tag);
    }
    return null;
  }
}
