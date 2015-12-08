package com.faforever.client.util;

import com.faforever.client.replay.ReplayInfoBean;
import com.faforever.client.replay.ReplaySortingOption;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static com.faforever.client.replay.ReplaySortingOption.DATE;
import static com.faforever.client.replay.ReplaySortingOption.MOST_DOWNLOADED;
import static com.faforever.client.replay.ReplaySortingOption.MOST_LIKED;

public class ReplaySortingUtil {

  private static class Gap {

    private int gapSize;
    private int index;
    private int midPointToNext;
    private int midPointToPrev;

    public int getGapSize() {
      return gapSize;
    }

    public void setGapSize(int gapSize) {
      this.gapSize = gapSize;
    }

    public int getIndex() {
      return index;
    }

    public void setIndex(int index) {
      this.index = index;
    }

    public int getMidPointToNext() {
      return midPointToNext;
    }

    public void setMidPointToNext(int midPointToNext) {
      this.midPointToNext = midPointToNext;
    }

    public int getMidPointToPrev() {
      return midPointToPrev;
    }

    public void setMidPointToPrev(int midPointToPrev) {
      this.midPointToPrev = midPointToPrev;
    }
  }

  public static List<List<ReplayInfoBean>> sortByDate(List<ReplayInfoBean> replayInfoBeans) {
    Collections.sort(replayInfoBeans, (replayInfoBean1, replayInfoBean2) -> {
      LocalDate replayInfoBean1LocalDate = getLocalDateFromInstant(replayInfoBean1.getStartTime());
      LocalDate replayInfoBean2LocalDate = getLocalDateFromInstant(replayInfoBean2.getStartTime());
      return replayInfoBean1LocalDate.compareTo(replayInfoBean2LocalDate);
    });

    List<List<ReplayInfoBean>> sortedReplayInfoBeans = new ArrayList<>();
    List<ReplayInfoBean> currentReplayList = new ArrayList<>();
    currentReplayList.add(replayInfoBeans.get(0));

    String previousDate = null;
    boolean first = true;
    for (ReplayInfoBean replayInfoBean : replayInfoBeans) {
      String currentDate = getLocalDateFromInstant(replayInfoBean.getStartTime()).toString();

      if (!first && !currentDate.equals(previousDate)) {
        currentReplayList = new ArrayList<>();
        sortedReplayInfoBeans.add(currentReplayList);
      }
      currentReplayList.add(replayInfoBean);

      first = false;
      previousDate = currentDate;
    }
    return sortedReplayInfoBeans;
  }

  private static LocalDate getLocalDateFromInstant(Instant instant) {
    return LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId()).toLocalDate();
  }

  public static List<List<ReplayInfoBean>> sortByHighestAvgLadderRating(List<ReplayInfoBean> replayInfoBeans) {
    return null;
  }

  public static List<List<ReplayInfoBean>> sortByHighestAvgGlobRating(List<ReplayInfoBean> replayInfoBeans) {
    return null;
  }

  public static List<List<ReplayInfoBean>> sortByMostDownloaded(List<ReplayInfoBean> replayInfoBeans) {
    Collections.sort(replayInfoBeans, (replayInfoBean1, replayInfoBean2) ->
        Integer.compare(replayInfoBean1.getDownloads(), replayInfoBean2.getDownloads()));

    List<List<ReplayInfoBean>> nestedList = new ArrayList<>();
    nestedList.add(replayInfoBeans);
    return smartSplitSortedReplays(nestedList, MOST_DOWNLOADED);
  }

  public static List<List<ReplayInfoBean>> sortByMostLiked(List<ReplayInfoBean> replayInfoBeans) {
    Collections.sort(replayInfoBeans, (replayInfoBean1, replayInfoBean2) ->
        Integer.compare(replayInfoBean1.getLikes(), replayInfoBean2.getLikes()));

    List<List<ReplayInfoBean>> nestedList = new ArrayList<>();
    nestedList.add(replayInfoBeans);
    return smartSplitSortedReplays(nestedList, MOST_LIKED);
  }

  private static List<List<ReplayInfoBean>> smartSplitSortedReplays(List<List<ReplayInfoBean>> sortedList, ReplaySortingOption replaySortingOption) {
    if (replaySortingOption == DATE) {
      return sortByDate(sortedList.get(0));
    }

    List<List<ReplayInfoBean>> sortedReplayInfoBeans = new ArrayList<>();

    for (List<ReplayInfoBean> replayInfoBeans : sortedList) {
      if (replayInfoBeans.size() < 25) {
        sortedReplayInfoBeans.add(replayInfoBeans);
        continue;
      }
      List<Gap> gaps = new ArrayList<>();
      Iterator<ReplayInfoBean> replayInfoBeanIterator = replayInfoBeans.iterator();
      int previous = Integer.parseInt(getSortingValueForReplayInfoBean(replayInfoBeanIterator.next(), replaySortingOption));
      int index = 1;
      int current;

      while (replayInfoBeanIterator.hasNext()) {
        Gap gap = new Gap();
        current = Integer.parseInt(getSortingValueForReplayInfoBean(replayInfoBeanIterator.next(), replaySortingOption));
        gap.setGapSize(current - previous);
        gap.setIndex(index);
        gaps.add(gap);
        previous = current;
        index++;
      }

      Collections.sort(gaps, (gap1, gap2) -> Integer.compare(gap1.getGapSize(), gap2.getGapSize()));
      gaps = gaps.stream().limit(4).sorted((gap1, gap2) -> Integer.compare(gap1.getIndex(), gap2.getIndex())).collect(Collectors.toList());

      int next;
      for (int i = 0; i < gaps.size(); i++) {
        Gap gap = gaps.get(i);
        current = Integer.parseInt(getSortingValueForReplayInfoBean(replayInfoBeans.get(gaps.get(i).getIndex()), replaySortingOption));
        if (i == 0) {
          gap.setMidPointToPrev(0);
        } else {
          previous = Integer.parseInt(getSortingValueForReplayInfoBean(replayInfoBeans.get(gaps.get(i - 1).getIndex()), replaySortingOption));
          gap.setMidPointToPrev((current - previous) / 2);
        }

        if (i == gaps.size() - 1) {
          gap.setMidPointToNext(Integer.MAX_VALUE);
        } else {
          next = Integer.parseInt(getSortingValueForReplayInfoBean(replayInfoBeans.get(gaps.get(i + 1).getIndex()), replaySortingOption));
          gap.setMidPointToNext((next - current) / 2);
        }
        sortedReplayInfoBeans.add(new ArrayList<>());
      }


      for (ReplayInfoBean replayInfoBean : replayInfoBeans) {
        int gapIndex = 0;
        for (Gap gap : gaps) {
          int value = Integer.parseInt(getSortingValueForReplayInfoBean(replayInfoBean, replaySortingOption));
          if (value >= gap.getMidPointToPrev() && value < gap.getMidPointToNext()) {
            sortedReplayInfoBeans.get(gapIndex).add(replayInfoBean);
            gapIndex++;
          }
        }
      }
      return smartSplitSortedReplays(sortedReplayInfoBeans, replaySortingOption);
    }
    return sortedReplayInfoBeans;
  }

  public static String getSortingValueForReplayInfoBean(ReplayInfoBean replayInfoBean, ReplaySortingOption replaySortingOption) {
    switch (replaySortingOption) {
      case MOST_LIKED:
        return String.format("%d", replayInfoBean.getLikes());
      case MOST_DOWNLOADED:
        return String.format("%d", replayInfoBean.getDownloads());
      case HIGHEST_AVG_GLOB_RATING:
      case HIGHEST_AVG_LADDER_RATING:
      case DATE:
        return getLocalDateFromInstant(replayInfoBean.getStartTime()).toString();
    }
    return "Add the new sorting option!!!";
  }
}



