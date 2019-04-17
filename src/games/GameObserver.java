package games;

import util.GenericTensor;

/**
 * Abstract class for observing a game matrix.
 */

public abstract class GameObserver {

  public static final int DEFAULT_SAMPLE_DENSITY_BOUND = 1;

  // Representation of the underlying game
  protected Game game = null;

  // Total number of observations made
  protected int totalObservations = 0;

  // Total number of profiles with at least 1 sample
  protected int profilesObserved = 0;

  // Counts for the number of observations of each outcome
  protected GenericTensor<Integer> observationCounts = null;

  // Flag for deterministic/stochastic observations
  protected boolean deterministic = false;

  // A bound on the total number of observations this observer will allow
  protected int bound = Integer.MIN_VALUE;

  // A density bound; used to compute the actual bound based on number of profiles
  protected double densityBound = Double.NEGATIVE_INFINITY;

  // The default payoff to use for unsampled profiles
  // If this should not be revealed, used NEGATIVE_INFINITY
  protected double defaultPayoff = Double.NEGATIVE_INFINITY;
  
  /**
   * Blank constructor
   */
  public GameObserver() {
  }
  /**
   * constructor given if deterministic
   * @param deterministic true when deterministic false otherwise
   */
  public GameObserver(boolean deterministic) {
    this.deterministic = deterministic;
  }
  /**
   * constructor given gmea
   * @param g game to observe
   */
  public GameObserver(Game g) {
    setGame(g);
    deterministic = false;
  }
  /**
   * constructor given gmea
   * @param g game to observe
   * @param deterministic true when deterministic
   */
  public GameObserver(Game g, boolean deterministic) {
    setGame(g);
    this.deterministic = deterministic;
  }

  /**
   * Set the game to observe
   * @param g the game
   */
  public void setGame(Game g) {
    this.game = g;
    totalObservations = 0;
    profilesObserved = 0;
    if (densityBound > 0) {
      bound = (int) Math.ceil(densityBound * game.getNumProfiles());
    }
    observationCounts = new GenericTensor<Integer>(game.getNumActions());
    initObsCounts();
  }

  /**
   * Reset all of the observation count information
   */
  public void reset() {
    if (densityBound > 0) {
      bound = (int) Math.ceil(densityBound * game.getNumProfiles());
    }
    totalObservations = 0;
    profilesObserved = 0;
    if (observationCounts != null) {
      initObsCounts();
    }
  }

  /**
   * Initialize all observation counts to 0
   */
  public void initObsCounts() {
    for (int i = 0; i < observationCounts.size(); i++) {
      observationCounts.setValue(0, i);
    }
  }
  /**
   * get the default payoff
   * @return the default payoff
   */
  public double getDefaultPayoff() {
    return defaultPayoff;
  }
  /**
   * get the default payoff
   * @param defaultPayoff the default payoff
   */
  public void setDefaultPayoff(double defaultPayoff) {
    this.defaultPayoff = defaultPayoff;
  }

  /**
   * Returns the number of samples left before the bound
   * @return the number of samples left
   */
  public int numObsLeft() {
    if (bound < 0) {
      return Integer.MAX_VALUE;
    } else {
      return bound - totalObservations;
    }
  }

  /**
   * Returns the bound on observations
   * @return the bound
   */
  public int getBound() {
    return bound;
  }

  /**
   * Sets a bound on the maximum observations
   * @param maxObservations the bound
   */
  public void setBound(int maxObservations) {
    this.bound = maxObservations;
  }

  /**
   * Sets a bound based on a particular density of samples
   * @param densityBound the bound
   */
  public void setDensityBound(double densityBound) {
    this.densityBound = densityBound;
    if (game != null) {
      this.bound = (int) Math.ceil(densityBound * game.getNumProfiles());
    }
  }

  /**
   * Returns the density bound
   * @return the bound
   */
  public double getDensityBound() {
    return densityBound;
  }

  /**
   * Set the observer to operate in deterministic mode
   * In this case, repeated queries for the same outcome are not counted as
   * additional observations.
   * @param deterministic true if deterministic
   */
  public void setDeterministic(boolean deterministic) {
    this.deterministic = deterministic;
  }

  /**
   * Returns whether or not the observations are deterministic
   * @return if deterministic
   */
  public boolean getDeterministic() {
    return deterministic;
  }

  /**
   * Returns the number of players in this game
   * @return the number of players
   * 
   */
  public int getNumPlayers() {
    return game.getNumPlayers();
  }

  /**
   * Returns the number of actions of the given player
   * or a vector of Integers with the numbers of actions.
   *
   * @param player the index of the player whose actions should be returned
   * @return the number of actions
   */
  public int getNumActions(int player) {
    return game.getNumActions(player);
  }

  /**
   * Returns an array containing the number of actions that each
   * player has.
   * @return the number of actions for each player
   */
  public int[] getNumActions() {
    return game.getNumActions();
  }

  /**
   * Returns the number of profiles in the underlying game
   * @return the number of profiles
   */
  public int getNumProfiles() {
    return game.getNumProfiles();
  }

  /**
   * Returns a sample of the payoffs for a particular outcome,
   * recording the access. (in the deterministic case, max count is 1)
   * <p>
   * If the bound on the number of samples has been reached, return null
   * </p>
   * @param outcome the outcome you want to sample payoffs of
   * @return the sample
   */
  public double[] getSample(int[] outcome) {
    // check if we have hit the sample bound or not
    if (bound > 0 && totalObservations >= bound) {
      return null;
    }

    int currSamples = observationCounts.getValue(outcome);
    if (!deterministic || currSamples < 1) {
      totalObservations++;
      if (currSamples < 1) {
        profilesObserved++;
      }
      observationCounts.setValue(observationCounts.getValue(outcome) + 1, outcome);
    }
    return samplePayoffs(outcome);
  }

  /**
   * Returns the total number of observations
   * @return the number of observations
   */
  public int getNumObs() {
    return totalObservations;
  }

  /**
   * Returns the sample density (total samples / num profiles)
   * @return the density
   */
  public double getObservationDensity() {
    return (double) totalObservations / (double) game.getNumProfiles();
  }

  /**
   * Returns the fraction of profiles that have at least 1 sample
   * @return the fraction
   */
  public double getFractionProfilesObserved() {
    return (double) profilesObserved / (double) game.getNumProfiles();
  }

  /**
   * Returns a description of the observation method
   * @return the description
   */
  public abstract String getDescription();

  /**
   * Returns payoffs for all players
   *
   * @param outcome the action choices for all players
   * @return the payoffs
   */
  protected abstract double[] samplePayoffs(int[] outcome);
}
