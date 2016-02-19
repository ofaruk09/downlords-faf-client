package com.faforever.client.replay;

public class ReplayInfoBeanBuilder {

  private final LocalReplayInfoBean localReplayInfoBean;

  private ReplayInfoBeanBuilder(int id) {
    localReplayInfoBean = new LocalReplayInfoBean();
    localReplayInfoBean.setId(id);
  }

  public LocalReplayInfoBean get() {
    return localReplayInfoBean;
  }

  public static ReplayInfoBeanBuilder create(int id) {
    return new ReplayInfoBeanBuilder(id);
  }
}
