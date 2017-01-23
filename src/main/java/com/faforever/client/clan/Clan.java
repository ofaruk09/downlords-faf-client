package com.faforever.client.clan;

import com.google.api.client.util.Key;

public class Clan {

  @Key("clan_desc")
  private String description;
  @Key("clan_founder_id")
  private Integer clanFounderId;
  @Key("clan_id")
  private String clanId;
  @Key("clan_leader_id")
  private Integer clanLeader;
  @Key("clan_members")
  private Integer clanMembers;
  @Key("clan_name")
  private String clanName;
  @Key("clan_tag")
  private String clanTag;
  @Key("create_date")
  private String createTime;
  @Key("founder_name")
  private String founderName;
  @Key("leader_name")
  private String leaderName;
  @Key
  private Integer status;


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getClanFounderId() {
    return clanFounderId;
  }

  public void setClanFounderId(Integer clanFounderId) {
    this.clanFounderId = clanFounderId;
  }

  public String getClanId() {
    return clanId;
  }

  public void setClanId(String clanId) {
    this.clanId = clanId;
  }

  public Integer getClanLeader() {
    return clanLeader;
  }

  public void setClanLeader(Integer clanLeader) {
    this.clanLeader = clanLeader;
  }

  public Integer getClanMembers() {
    return clanMembers;
  }

  public void setClanMembers(Integer clanMembers) {
    this.clanMembers = clanMembers;
  }

  public String getClanName() {
    return clanName;
  }

  public void setClanName(String clanName) {
    this.clanName = clanName;
  }

  public String getClanTag() {
    return clanTag;
  }

  public void setClanTag(String clanTag) {
    this.clanTag = clanTag;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getFounderName() {
    return founderName;
  }

  public void setFounderName(String founderName) {
    this.founderName = founderName;
  }

  public String getLeaderName() {
    return leaderName;
  }

  public void setLeaderName(String leaderName) {
    this.leaderName = leaderName;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  @Override
  public int hashCode() {
    return clanId != null ? clanId.hashCode() : 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Clan that = (Clan) o;

    return !(clanId != null ? !clanId.equals(that.clanId) : that.clanId != null);

  }
}