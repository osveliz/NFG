package games;

import java.util.*;

/**
 * Holds the payoffs for a profile
 */
public class Payoffs {

  // a default value for the payoffs,
  private static final double DEFAULT_VALUE = Double.NEGATIVE_INFINITY;

  private double[] payoffs;

  public Payoffs() {
    payoffs = null;
  }

  public Payoffs(int numPlayers) {
    payoffs = new double[numPlayers];
    Arrays.fill(payoffs, DEFAULT_VALUE);
  }

  public Payoffs(Payoffs p) {
    this.payoffs = p.payoffs.clone();
  }

  public Payoffs(double[] values) {
    payoffs = values.clone();
  }

  public void init(int numPlayers) {
    payoffs = new double[numPlayers];
    Arrays.fill(payoffs, DEFAULT_VALUE);
  }

  public double getPayoff(int player) {
    return payoffs[player];
  }

  public double[] getPayoffs() {
    return payoffs.clone();
  }

  public void setPayoff(int player, double value) {
    if (payoffs == null || player > payoffs.length) return;
    payoffs[player] = value;
  }

  public void setPayoffs(double[] values) {
    if (payoffs == null || values.length != payoffs.length) {
      payoffs = values.clone();
    } else {
      System.arraycopy(values, 0, this.payoffs, 0, values.length);
    }
  }

  public String toString() {
    return Arrays.toString(payoffs);
  }
}
