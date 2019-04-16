package games;

import java.util.*;

/**
 * This is a (slightly) modified version of the gamut Game class.
 * Main difference is that it does not inherit the parameterizations stuff.
 */

public abstract class Game {

  protected final int nPlayers;
  protected final int[] nActions;
  protected int nProfiles;
  protected int nDeviations;
  protected String description;

  /**
   * Standard constructor
   */
  protected Game(int numPlayers, int[] numActions) {
    this.description = "";
    this.nPlayers = numPlayers;
    this.nActions = numActions.clone();
    updateGameSize();
  }

  /**
   * Constructor for creating games with the same number of actions for all players
   */
  protected Game(int numPlayers, int numActions) {
    this.description = "";
    this.nPlayers = numPlayers;
    this.nActions = new int[numPlayers];
    Arrays.fill(this.nActions, numActions);
    updateGameSize();
  }

  /**
   * This function *MUST* be called if the size of the game is modified in a derived class
   */
  protected void updateGameSize() {
    int tmpProfiles = 1;
    int tmpDevs = 0;
    for (int tmp : nActions) {
      tmpProfiles *= tmp;
      tmpDevs += tmp - 1;
    }
    nProfiles = tmpProfiles;
    nDeviations = tmpDevs;
  }

  /**
   * Returns Game Description (Name, Params, etc)
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description string for the game which will be
   * output as part of the help string.
   *
   * @param description a description of the game
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns the number of players in this game
   */
  public int getNumPlayers() {
    return nPlayers;
  }

  /**
   * Returns the number of actions of the given player
   * or a vector of Integers with the numbers of actions.
   *
   * @param player the index of the player whose actions should
   *               be returned
   */
  public int getNumActions(int player) {
    return nActions[player];
  }

  /**
   * Returns an array containing the number of actions that each
   * player has.
   */
  public int[] getNumActions() {
    return nActions.clone();
  }

  /**
   * Returns the total number of outcome profiles for this game
   */
  public int getNumProfiles() {
    return nProfiles;
  }

  // returns a random profile of this game
  public int[] getRandomProfile() {
    int[] profile = new int[nPlayers];
    for (int pl = 0; pl < nPlayers; pl++) {
      profile[pl] = EGAUtils.rand.nextInt(nActions[pl]) + 1;
    }
    return profile;
  }

  // returns a random profile in the given object
  public void getRandomProfile(int[] profileHolder) {
    if (profileHolder.length != nPlayers) return;
    for (int pl = 0; pl < nPlayers; pl++) {
      profileHolder[pl] = EGAUtils.rand.nextInt(nActions[pl]) + 1;
    }
  }

  /**
   * Returns the total number of possible unilateral deviations from any profile
   */
  public int getNumPossibleDeviations() {
    return nDeviations;
  }

  /**
   * Get an iterator for looping over the outcomes of this game
   */
  public OutcomeIterator iterator() {
    return new OutcomeIterator(this);
  }

  /**
   * @return the difference between the max and min payoffs in the game
   */
  public double getPayoffRange() {
    double[] maxAndMin = getExtremePayoffs();
    return maxAndMin[0] - maxAndMin[1];
  }

  /**
   * Get the maximum and minimum payoffs for the game over all players: [max, min]
   */
  public double[] getExtremePayoffs() {
    double max = Double.NEGATIVE_INFINITY;
    double min = Double.POSITIVE_INFINITY;

    OutcomeIterator itr = iterator();
    while (itr.hasNext()) {
      int[] outcome = itr.next();
      double[] payoffs = getPayoffs(outcome);
      for (double tmp : payoffs) {
        max = Math.max(max, tmp);
        min = Math.min(min, tmp);
      }
    }

    return new double[] {max, min};
  }

  /**
   * Get the maximum and minimum payoffs for a particular player
   */
  public double[] getExtremePayoffs(int player) {
    double max = Double.NEGATIVE_INFINITY;
    double min = Double.POSITIVE_INFINITY;

    OutcomeIterator itr = iterator();
    while (itr.hasNext()) {
      int[] outcome = itr.next();
      double payoff = getPayoff(outcome, player);
      max = Math.max(max, payoff);
      min = Math.min(min, payoff);
    }

    return new double[] {max, min};
  }

  /**
   * Returns a payoff for a given player. (Override if this is more efficient)
   *
   * @param outcome action choices for all players
   * @param player  player whose payoff to return
   */
  public double getPayoff(int[] outcome, int player) {
    return getPayoffs(outcome)[player];
  }

  /**
   * Returns payoffs for all players
   *
   * @param outcome action choices for all players
   */
  public abstract double[] getPayoffs(int[] outcome);
}



