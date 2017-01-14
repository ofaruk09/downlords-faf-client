package com.faforever.client.clan;

import java.util.HashMap;

public interface ClanService {

  HashMap<String, Clan> getclansByTag();

  void setClansByTag(HashMap clansByTag);

  Clan getClanByTag(String tag);
}
