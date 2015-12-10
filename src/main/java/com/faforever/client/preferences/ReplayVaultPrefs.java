package com.faforever.client.preferences;

import com.faforever.client.replay.RatingType;
import com.faforever.client.replay.ReplaySortingOption;
import com.faforever.client.replay.SelectedReplayVault;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import static com.faforever.client.replay.RatingType.LADDER;
import static com.faforever.client.replay.ReplaySortingOption.DATE;
import static com.faforever.client.replay.SelectedReplayVault.LOCAL;

public class ReplayVaultPrefs {

  private final ObjectProperty<RatingType> ratingType;
  private final ObjectProperty<SelectedReplayVault> selectedReplayVault;
  private final BooleanProperty displayWinners;
  private final ObjectProperty<ReplaySortingOption> replaySortingOption;

  public ReplayVaultPrefs() {
    ratingType = new SimpleObjectProperty<>(LADDER);
    replaySortingOption = new SimpleObjectProperty<>(DATE);
    displayWinners = new SimpleBooleanProperty(false);
    selectedReplayVault = new SimpleObjectProperty<>(LOCAL);
  }

  public SelectedReplayVault getSelectedReplayVault() {
    return selectedReplayVault.get();
  }

  public void setSelectedReplayVault(SelectedReplayVault selectedReplayVault) {
    this.selectedReplayVault.set(selectedReplayVault);
  }

  public ObjectProperty<SelectedReplayVault> selectedReplayVaultProperty() {
    return selectedReplayVault;
  }

  public boolean getDisplayWinners() {
    return displayWinners.get();
  }

  public void setDisplayWinners(boolean displayWinners) {
    this.displayWinners.set(displayWinners);
  }

  public BooleanProperty displayWinnersProperty() {
    return displayWinners;
  }

  public ReplaySortingOption getReplaySortingOption() {
    return replaySortingOption.get();
  }

  public void setReplaySortingOption(ReplaySortingOption replaySortingOption) {
    this.replaySortingOption.set(replaySortingOption);
  }

  public ObjectProperty<ReplaySortingOption> replaySortingOptionProperty() {
    return replaySortingOption;
  }

  public RatingType getRatingType() {
    return ratingType.get();
  }

  public void setRatingType(RatingType ratingType) {
    this.ratingType.set(ratingType);
  }

  public ObjectProperty<RatingType> ratingTypeProperty() {
    return ratingType;
  }
}
