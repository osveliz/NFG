package games;

import util.GenericTensor;

import java.util.*;

/**
 * Represents a distribution over outcomes in a game
 */

public final class OutcomeDistribution {

  private final int nPlayers;
  private final int[] nActions;
  private final GenericTensor<Double> probs;
  
  /**
   * copy constructor
   * @param original what to be copied
   */
  public OutcomeDistribution(OutcomeDistribution original) {
    this.nPlayers = original.getNumPlayers();
    this.nActions = original.getNumActions().clone();
    this.probs = new GenericTensor<Double>(this.nActions);

    OutcomeIterator itr = original.iterator();
    while (itr.hasNext()) {
      int[] outcome = itr.next();
      probs.setValue(original.getProb(outcome), outcome);
    }
  }

  /**
   * Create a new distribution object
   * Initialized by default to the centroid (equal probabilities for all outcomes)
   * @param nActions the number of actions
   */
  public OutcomeDistribution(int[] nActions) {
    this.nPlayers = nActions.length;
    this.nActions = nActions.clone();
    this.probs = new GenericTensor<Double>(this.nActions);
    setCentroid();
  }

  /**
   * Create a new distribution object
   * Initial all probabilities to the given value
   * @param nActions the number of actions
   * @param initialValue what to initialize them to
   */
  public OutcomeDistribution(int[] nActions, double initialValue) {
    this.nPlayers = nActions.length;
    this.nActions = nActions.clone();
    this.probs = new GenericTensor<Double>(this.nActions);
    setAll(initialValue);
  }

  /**
   * Create a new distribution object based on the joint mixed strategies given
   * @param strategies a list of mixed strategies
   */
  public OutcomeDistribution(List<MixedStrategy> strategies) {
    if (strategies.size() == 0) {
      this.nPlayers = 0;
      this.nActions = null;
      this.probs = null;
      return;
    }

    this.nPlayers = strategies.size();
    this.nActions = new int[nPlayers];
    for (int pl = 0; pl < nPlayers; pl++) {
      nActions[pl] = strategies.get(pl).getNumActions();
    }
    this.probs = new GenericTensor<Double>(this.nActions);

    setMixedStrategies(strategies);
  }

  /**
   * Creates a new distribution based on a set of outcomes, with equal weight given to all
   * @param nActions the number of actions
   * @param outcomes a collection of outcomes
   */
  public OutcomeDistribution(int[] nActions, Collection<int[]> outcomes) {
    this.nPlayers = nActions.length;
    this.nActions = nActions.clone();
    this.probs = new GenericTensor<Double>(this.nActions);
    setOutcomeSet(outcomes);
  }

  /**
   * gets an iterator for this distribution
   * @return the outcome iterater
   */
  public OutcomeIterator iterator() {
    return new OutcomeIterator(nPlayers, nActions);
  }

  /**
   * Returns the conditional distribution for the given restriction set
   * @param restrictedPlayers avoid these players
   * @return the conditional distribrution
   */
  public OutcomeDistribution getConditionalDistribution(List<Integer> restrictedPlayers) {
    if (restrictedPlayers.size() >= nPlayers) return null;

    int nRemainingPlayers = nPlayers - restrictedPlayers.size();
    int[] remainingActions = new int[nRemainingPlayers];
    int[] playerMapping = new int[nRemainingPlayers];

    // create a representation of the restricted player set
    int restrictedIndex = 0;
    for (int pl = 0; pl < nPlayers; pl++) {
      if (!restrictedPlayers.contains(pl)) {
        playerMapping[restrictedIndex] = pl;
        remainingActions[restrictedIndex] = nActions[pl];
        restrictedIndex++;
      }
    }

    // create a new distribution to represent the marginal (initialized to zero)
    OutcomeDistribution conditional = new OutcomeDistribution(remainingActions, 0d);

    // loop over the unrestricted outcomes, summing the probabilities for each restricted outcome
    OutcomeIterator itr = iterator();
    int[] restrictedOutcome = new int[nRemainingPlayers];
    while (itr.hasNext()) {
      int[] outcome = itr.next();

      // map the outcome into the restricted player set
      for (int pl = 0; pl < nRemainingPlayers; pl++) {
        restrictedOutcome[pl] = outcome[playerMapping[pl]];
      }

      // sum the probabilities that map into the same restricted outcome
      double oldProb = conditional.getProb(restrictedOutcome);
      conditional.setProb(restrictedOutcome, oldProb + probs.getValue(outcome));
    }
    return conditional;
  }
/**
 * get the number of actions
 * @return the number of action
 */
  public int[] getNumActions() {
    return nActions;
  }

/**
 * get the number of players
 * @return the number of players
 */
  public int getNumPlayers() {
    return nPlayers;
  }

  /**
   * Compute  the marginal distribution for a given player's actions
   *
   * @param player player
   * @return marginal distribution
   */
  public double[] getMarginalDistribution(int player) {
    double[] marginal = new double[nActions[player] + 1];

    OutcomeIterator itr = iterator();
    while (itr.hasNext()) {
      int[] outcome = itr.next();
      marginal[outcome[player]] += probs.getValue(outcome);
    }
    return marginal;
  }

  /**
   * Sets this distribution to represent the centroid
   */
  public void setCentroid() {
    double tmpProb = 1d / probs.size();
    setAll(tmpProb);
  }

  /**
   * set a single profile chosen at uniform random as the predictions
   */
  public void setRandomPureProfile() {
    int r = EGAUtils.rand.nextInt(probs.size());
    probs.setValue(1d, r);
  }


  /**
   * Sets all probabilities to the given value
   *
   * @param value value to set each probability to
   */
  public void setAll(double value) {
    int nProfiles = probs.size();
    for (int i = 0; i < nProfiles; i++) {
      probs.setValue(value, i);
    }
  }

  /**
   * Sets the distribution based on the given mixed strategies
   * @param strategies the list of mixed strategies
   */
  public void setMixedStrategies(List<MixedStrategy> strategies) {
    if (strategies.size() != nPlayers) {
      System.out.println("Warning: invalid number of strategies when setting mixed strategies in OutcomeDistribution!");
      return;
    }

    OutcomeIterator itr = iterator();
    while (itr.hasNext()) {
      int[] outcome = itr.next();
      double prob = 1d;
      for (int pl = 0; pl < nPlayers; pl++) {
        prob *= strategies.get(pl).getProb(outcome[pl]);
      }
      probs.setValue(prob, outcome);
    }
  }

  /**
   * Sets the distribution to an even distribution over the given outcomes
   * @param outcomes the set of outcomes
   */
  public void setOutcomeSet(Collection<int[]> outcomes) {
    setAll(0d);
    double tmpProb = 1d / outcomes.size();
    for (int[] outcome : outcomes) {
      probs.setValue(tmpProb, outcome);
    }
  }
  /**
   * Legacy
   * @param outcome the outcome to set
   */
  public void setPureOutcome(int[] outcome) {
    setAll(0d);
    probs.setValue(1d, outcome);
  }

  /**
   * normalize this distribution so that it sums to 1
   * If the sum of current values is 0, set the values to the centroid
   */
  public void normalize() {
    double sum = computeSum();
    if (sum <= 0) {
      setCentroid();
      return;
    }

    int nProfiles = probs.size();
    for (int i = 0; i < nProfiles; i++) {
      probs.setValue(probs.getValue(i) / sum, i);
    }
  }
  
  /**
   * Initialize the probability of an outcome
   * @param outcome to be set
   * @param value to be set
   */
  public void setProb(int[] outcome, double value) {
    probs.setValue(value, outcome);
  }
  
  /**
   * get the probability of an outcome
   * @param outcome the outcome
   * @return the probability
   */
  public double getProb(int[] outcome) {
    return probs.getValue(outcome);
  }

  /**
   * Check whether this is a valid probability distribution (sums to 1)
   * Uses a tolerance of 0.01, so anything between 0.99 and 1.01 is valid
   * @return true if distribution sums to 1, false otherwise
   */
  public boolean isValid() {
    double sum = computeSum();
    return sum > 0.99d && sum < 1.01d;
  }

  /**
   * compute the sum of the values for all profiles
   * @return the sum
   */
  private double computeSum() {
    int nProfiles = probs.size();
    double sum = 0d;
    for (int i = 0; i < nProfiles; i++) {
      sum += probs.getValue(i);
    }
    return sum;
  }

  /**
   * returns the information entropy of this distribution
   * @return entropy
   */
  public double computeEntropy() {
    int nProfiles = probs.size();
    double entropy = 0d;
    double log2 = Math.log(2);
    for (int i = 0; i < nProfiles; i++) {
      double prob = probs.getValue(i);
      if (prob > 0) {
        entropy += prob * (Math.log(prob) / log2);
      }
    }
    if (entropy != 0) {
      entropy *= -1;
    }
    return entropy;
  }

  /**
   * computes a mixture of this distribution with the uniform distribution
   * with probability delta, the distribution of play is uniform
   * with probability 1-delta, the distribution of play is the current distribution
   * @param delta see above description
   */
  public void mixWithUniform(double delta) {
    int nProfiles = probs.size();
    double uniformProb = delta / nProfiles;
    for (int i = 0; i < nProfiles; i++) {
      double prob = probs.getValue(i);
      prob *= (1 - delta);
      prob += uniformProb;
      probs.setValue(prob, i);
    }
  }

  /**
   * select a random profile according to this outcome distribution
   * @return random profile
   */
  public int[] sampleDistribution() {
    for (int i = 0; i < 5; i++) {
      OutcomeIterator itr = iterator();
      double r = EGAUtils.rand.nextDouble();
      while (itr.hasNext()) {
        int[] outcome = itr.next();
        r -= probs.getValue(outcome);
        if (r <= 0) return outcome;
      }
      System.out.println("Error Sampling outcome distribution: " + r + " " + i);
    }

    System.out.println("SERIOUS error sampling outcome distribution! (more than 5 iterations failed).");
    int[] tmp = new int[nPlayers];
    Arrays.fill(tmp, 1);
    return tmp;
  }

  /**
   * returns a string distribution of the distribution
   * @return standard toString()
   */
  public String toString() {
    StringBuilder sb = EGAUtils.getSB();
    OutcomeIterator itr = iterator();

    sb.append("Distribution: \n");
    while (itr.hasNext()) {
      int[] outcome = itr.next();
      sb.append(Arrays.toString(outcome)).append(": ");
      sb.append(probs.getValue(outcome)).append("\n");
    }
    sb.append("\n");
    String tmp = sb.toString();
    EGAUtils.returnSB(sb);
    return tmp;
  }
}
