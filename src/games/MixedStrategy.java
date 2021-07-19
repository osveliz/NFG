package games;

import java.util.*;

/**
 * A class the encapsulates the notion of a mixed strategy:
 * the probability of playing each action, summing to 1
 * Note: strategies are labeled 1..numActions
 */

public final class MixedStrategy {

  private final int nActions;
  private final double[] probs;

  /**
   * Create a new mixed strategy object representing these probabilities
   * @param probs the strategy probabilities to set
   */
  public MixedStrategy(double[] probs) {
    this.probs = new double[probs.length];
    System.arraycopy(probs, 0, this.probs, 0, probs.length);
    this.nActions = probs.length - 1;
  }

  /*public MixedStrategy(MixedStrategy ms){
    this(ms.getProbs());
  }*/

  /**
   * Create a new mixed strategy for the given number of actions;
   * defaults to uniform mixture
   * @param nActions the number of actions
   */
  public MixedStrategy(int nActions) {
    this.nActions = nActions;
    this.probs = new double[nActions + 1];
    Arrays.fill(probs, 1d / (double) nActions);
    probs[0] = 0d;
  }

  /**
   * Create a new mixed strategy for the given number of actions;
   * initial probability of each action is given
   * @param nActions the number of actions
   * @param initialValue the initial value
   */
  public MixedStrategy(int nActions, double initialValue) {
    this.nActions = nActions;
    this.probs = new double[nActions + 1];
    Arrays.fill(probs, initialValue);
    probs[0] = 0d;
  }
  public MixedStrategy(MixedStrategy a, MixedStrategy b, double w) {
    this.nActions = a.getNumActions();
    this.probs = new double[nActions + 1];
    //Arrays.fill(probs, initialValue);
    probs[0] = 0d;
    for (int i = 1; i < probs.length; i++) {
      probs[i] = a.getProb(i)*w + b.getProb(i)*(1-w);
	}
  }

  /**
   * Returns the number of actions represented
   * @return the number of actions
   */
  public int getNumActions() {
    return nActions;
  }

  /**
   * Returns the probability of playing a particular action
   * @param action the action you want the probability of
   * @return that action's probability
   */
  public double getProb(int action) {
    if (action > 0) {
      return probs[action];
    } else {
      return 0;
    }
  }

  /**
   * Return all action probabilities as an array
   * @return the action probabilities
   */
  public double[] getProbs() {
    return probs;
  }

  /**
   * Resets the strategy to the uniform strategy
   */
  public void setUniform() {
    Arrays.fill(probs, 1d / (double) (probs.length - 1));
    probs[0] = 0d;
  }

  /**
   * Resets the mixed strategy to a randomly-chosen strategy
   */
  public void setRandom() {
    for (int i = 1; i < probs.length; i++) {
      probs[i] = EGAUtils.rand.nextDouble();
    }
    normalize();
  }

  /**
   * Set all probabilities to zero
   */
  public void setZeros() {
    Arrays.fill(probs, 0d);
  }

  /**
   * Set the probability of playing a given action
   * @param action action number
   * @param prob the probability to set
   */
  public void setProb(int action, double prob) {
    if (action > 0) {
      probs[action] = prob;
    }
  }

  /**
   * Set all action probabilities
   * @param probs the probabilities
   */
  public void setProbs(double[] probs) {
    if (probs.length - 1 != nActions) {
      System.err.println("Warning: trying to set mixed strategy with invalid number of actions!");
      return;
    }
    System.arraycopy(probs, 1, this.probs, 1, probs.length - 1);
  }
  
  /**
   * set the mixed strategy
   * @param ms the mixed strategy
   */
  public void setProbs(MixedStrategy ms) {
    if (ms.getNumActions() != nActions) {
      System.err.println("Warning: trying to copy mixed strategy with invalid number of actions!");
      return;
    }
    System.arraycopy(ms.getProbs(), 1, this.probs, 1, probs.length - 1);
  }

  /**
   * Normalize the vector to sum to 1
   */
  public void normalize() {
    probs[0] = 0d;
    double sum = 0d;
    double countzeroone = 0;
    for (int i = 1; i < probs.length; i++) {
	  if(probs[i] < 10e-10)
	    probs[i] = 0;
      if(probs[i] == 0 || probs[i] == 1)
		countzeroone++;
    }
    for (int i = 1; i < probs.length; i++) {
      sum += probs[i];
    }
    double change = (sum - 1) * -1 / (probs.length -1 - countzeroone);
    for (int i = 1; i < probs.length; i++) {
      //probs[i] /= sum;
      if(probs[i] == 0 || probs[i] == 1)
		continue;
	  if(probs[i] + change < 0)
		probs[i] = 0;
	  else if(probs[i] + change > 1)
		probs[i] = 1;
	  else
		probs[i] += change;
    }
    if(!isValid())//check valid{
		normalize();
    
  }

  /**
   * Sets the mixed strategy to a uniform mixture over the actions that have the
   * highest payoffs in the given payoff vector
   *
   * @param payoffs payoffs for each pure strategy
   */
  public void setBestResponse(double[] payoffs) {
    if (payoffs.length != probs.length) return;

    double max = Double.NEGATIVE_INFINITY;
    double tot = 0;
    for (int a = 1; a < payoffs.length; a++) {
      max = Math.max(max, payoffs[a]);
    }
    for (int a = 1; a < payoffs.length; a++) {
      if (payoffs[a] == max) {
        probs[a] = 1d;
        tot++;
      } else {
        probs[a] = 0d;
      }
    }
    for (int a = 1; a < probs.length; a++) {
      probs[a] /= tot;
    }
  }

  /**
   * play randomly with probability delta; the current mixture with probability 1-delta
   * @param delta see above
   */
  public void mixWithUniform(double delta) {
    double uniformProb = delta / nActions;
    for (int i = 1; i <= nActions; i++) {
      probs[i] *= (1 - delta);
      probs[i] += uniformProb;
    }
  }

  /**
   * Determines whether the probabilities sum to 1 (within a tolerance of 0.01)
   * @return true when value false otherwise
   */
  public boolean isValid() {
    double sum = 0;
    for (int i = 1; i < probs.length; i++) {
      sum += probs[i];
    }
    return sum > 0.999d && sum < 1.001d;
  }

  /**
   * Returns a string representation of the mixed strategy
   * @return standard toString()
   */
  public String toString() {
    StringBuilder sb = EGAUtils.getSB();
    sb.append("{");
    for (int i = 1; i < probs.length - 1; i++) {
      sb.append(probs[i]);
      sb.append(", ");
    }
    sb.append(probs[probs.length - 1]);
    sb.append("}");
    String tmp = sb.toString();
    EGAUtils.returnSB(sb);
    return tmp;
  }

  public String toStringSpaces() {
    StringBuilder sb = EGAUtils.getSB();
    for (int i = 1; i < probs.length - 1; i++) {
      sb.append(probs[i]);
      sb.append(" ");
    }
    sb.append(probs[probs.length - 1]);
    String tmp = sb.toString();
    EGAUtils.returnSB(sb);
    return tmp;
  }

  public void print(){
    System.out.println(toString());
  }
}
