package util;

import games.*;
import java.math.*;
import java.util.*;

/**
 * Utility functions for game solvers
 */

public class SolverUtils {

  // static class
  private SolverUtils() {
  }

  /**
   * Normalize the vector a so that the elements sum to 1
   *
   * @param a vector to normalize
   */
  public static void normalize(double[] a) {
    double sum = 0d;
    for (double b : a) {
      sum += b;
    }
    for (double b : a) {
      b /= sum;
    }
  }




  /**
   * Wrapper with default setting for ignoreUnsampled
   *
   * @param g                   the game
   * @param outcomeDistribution distribution over outcomes
   * @return vector of payoffs to each player in the given outcome
   */
  public static double[] computeOutcomePayoffs(Game g, OutcomeDistribution outcomeDistribution) {
    return computeOutcomePayoffs(g, outcomeDistribution, false);
  }

  /**
   * Given a game and a distribution over outcomes, compute the expected payoffs for each player
   *
   * @param g                   the game
   * @param outcomeDistribution distribution over outcomes
   * @param ignoreUnsampled     for empirical games, do not include the payoffs for any unsampled profiles
   * @return vector of payoffs to each player in the given outcome
   */
  public static double[] computeOutcomePayoffs(Game g, OutcomeDistribution outcomeDistribution,
                                               boolean ignoreUnsampled) {
    int nPlayers = g.getNumPlayers();
    double[] payoffs = new double[nPlayers];

    // loop through outcomes to compute the expected payoffs
    OutcomeIterator itr = g.iterator();
    while (itr.hasNext()) {
      int[] outcome = itr.next();

      double prob = outcomeDistribution.getProb(outcome);
      if (prob > 0) {
        // add in this component of the payoffs
        double[] outcomePayoffs = g.getPayoffs(outcome);
        for (int pl = 0; pl < nPlayers; pl++) {
          payoffs[pl] += prob * outcomePayoffs[pl];
        }
      }
    }
    return payoffs;
  }
  
  /**
   * Rounding an array to the nearest tenths place
   * @param a array to round
   * @return rounded array to nearest tenths place
   */
  public static double[] roundTenths(double[] a){
	  double b[] = new double[a.length];
	  for(int i = 0; i < b.length; i++){
		  b[i] = Math.round(a[i]*10.0)/10.0;
	  }
	  return b;
  }
  /**
   * Compute Expected Payoff in 2 player game
   * @param s1 strategy for player 1
   * @param s2 strategy for player 2
   * @param mg matrix game being played
   * @return [player 1 expected payoff, player 2 expected payoff] (-1337) if invalid
   */
	public static double[] expectedPayoffs(MixedStrategy s1, MixedStrategy s2, MatrixGame mg){
		double payoffs[] = {0.0,0.0};
		boolean valid1 = s1.isValid();
		boolean valid2 = s2.isValid();
		List<MixedStrategy> list = new ArrayList<MixedStrategy>();
		list.add(s1);
		list.add(s2);
		if(s1.isValid() && s2.isValid())
			return computeOutcomePayoffs(mg,new OutcomeDistribution(list));
		if(!valid1 || !valid2){
			System.out.println("Detected Invalid Strategy");
			if(!valid1)
				payoffs[0] = -1337.0;
			if(!valid2)
				payoffs[1] = -1337.0;
		}
		return payoffs;
	}
	/**
	 * Best response
	 */
	public static MixedStrategy computeBestResponse(MatrixGame mg, int player, MixedStrategy opponentStrat){
		double[] payoffs = new double[2];
		int actions = mg.getNumActions(player);
		MixedStrategy s = new MixedStrategy(actions);
		s.setZeros();
		double bestPay = Double.MIN_VALUE;
		int bestAction= 1;
		if(player == 0){
			for(int i = 1; i <= actions; i++){
				s.setProb(i,1);
				payoffs = expectedPayoffs(s, opponentStrat, mg);
				s.setZeros();
				if(payoffs[player]> bestPay){
					bestPay = payoffs[player];
					bestAction = i;
				}
			}
			s.setProb(bestAction,1.0);
		}
		else{
			for(int i = 1; i <= actions; i++){
				s.setProb(i,1);
				payoffs = expectedPayoffs(opponentStrat,s, mg);
				s.setZeros();
				if(payoffs[player]> bestPay){
					bestPay = payoffs[player];
					bestAction = i;
				}
			}
			s.setProb(bestAction,1.0);
		}
		return s;
	}
	public static MixedStrategy computeQuantalBestResponse(MatrixGame mg, int player, MixedStrategy opponentStrat, double lambda){
		if(lambda < 0){
			System.out.println("\u03BB should be positive");
			lambda = 0;
		}
		double[] payoffs = new double[2];
		int actions = mg.getNumActions(player);
		MixedStrategy s = new MixedStrategy(actions);
		s.setZeros();
		MixedStrategy qbr = new MixedStrategy(actions);
		qbr.setZeros();
		//double bestPay = Double.MIN_VALUE;
		//int bestAction= 1;
		double sum = 0.0;
		double temp = 0.0;
		for(int i = 1; i <= actions; i++){
			s.setProb(i,1);
			if(player == 0){
				payoffs = expectedPayoffs(s,opponentStrat,mg);		
			}
			else{
				payoffs = expectedPayoffs(opponentStrat,s,mg);
			}
			temp = Math.exp(lambda*payoffs[player]);
			sum += temp;
			qbr.setProb(i, temp);
			s.setProb(i,0);
		}
		for(int i = 1; i <= actions; i++){
			temp = qbr.getProb(i);
			qbr.setProb(i, temp / sum);
		}
		return qbr;
	}
	/**
	 * Best response
	 */
	public static MixedStrategy computePunishResponse(MatrixGame mg, int player, MixedStrategy opponentStrat){
		double[] payoffs = new double[2];
		int actions = mg.getNumActions(player);
		MixedStrategy s = new MixedStrategy(actions);
		s.setZeros();
		double bestPay = Double.MAX_VALUE;
		int bestAction= 1;
		if(player == 0){
			for(int i = 1; i <= actions; i++){
				s.setProb(i,1);
				payoffs = expectedPayoffs(s, opponentStrat, mg);
				s.setZeros();
				if(payoffs[1-player] < bestPay){
					bestPay = payoffs[1-player];
					bestAction = i;
				}
			}
			s.setProb(bestAction,1.0);
		}
		else{
			for(int i = 1; i <= actions; i++){
				s.setProb(i,1);
				payoffs = expectedPayoffs(opponentStrat,s, mg);
				s.setZeros();
				if(payoffs[1-player] < bestPay){
					bestPay = payoffs[1-player];
					bestAction = i;
				}
			}
			s.setProb(bestAction,1.0);
		}
		return s;
	}

	public static MixedStrategy computeRobustBestResponseBackup(MatrixGame mg, int player, double lambda){
		if(lambda < 0){
			System.out.println("\u03BB should be positive");
			lambda = 0;
		}
		
		double[] payoffs = new double[2];
		int actions = mg.getNumActions(player);
		MixedStrategy opponentStrat = new MixedStrategy(actions);
		opponentStrat.setUniform();
		MixedStrategy s = new MixedStrategy(actions);
		s = SolverUtils.computeBestResponse(mg, player, opponentStrat);

		double[] punishment = new double[actions];
		opponentStrat.setZeros();
		for(int i = 1; i <= actions; i++){
			opponentStrat.setProb(i,1);//choose action i
			if(player == 0){
				payoffs = expectedPayoffs(s,opponentStrat,mg);		
			}
			else{
				payoffs = expectedPayoffs(opponentStrat,s,mg);
			}
			opponentStrat.setProb(i,0);//reset action i
			//punishment[i-1] = payoffs[1-player];
			punishment[i-1] = -1*payoffs[1-player];
		}

		/*double max = punishment[0];
		for(int i = 1; i < actions; i++){
			if(max < punishment[i]){
				max = punishment[i];
			}
		}*/


		MixedStrategy robust = new MixedStrategy(actions);
		robust.setZeros();
		double sum = 0.0;
		double temp = 0.0;
		for(int i = 0; i < actions; i++){
			//punishment[i] = max - punishment[i];
			temp = Math.exp(lambda*punishment[i]);
			sum += temp;
			robust.setProb(i+1, temp);
		}
		
		for(int i = 1; i <= actions; i++){
			temp = robust.getProb(i);
			robust.setProb(i, temp / sum);
		}
		return robust;
	}

	public static MixedStrategy computeRobustBestResponse(MatrixGame mg, int player, double lambda){
		if(lambda < 0){
			System.out.println("\u03BB should be positive");
			lambda = 0;
		}
		
		double[] payoffs = new double[2];
		int actions = mg.getNumActions(player);
		MixedStrategy myStrat = new MixedStrategy(actions);
		myStrat.setUniform();
		MixedStrategy opponentStrat = new MixedStrategy(actions);
		opponentStrat = SolverUtils.computeBestResponse(mg, 1-player, myStrat);

		double[] punishment = new double[actions];
		myStrat.setZeros();
		for(int i = 1; i <= actions; i++){
			myStrat.setProb(i,1);//choose action i
			if(player == 0){
				payoffs = expectedPayoffs(myStrat,opponentStrat,mg);
			}
			else{
				payoffs = expectedPayoffs(opponentStrat,myStrat,mg);
			}
			myStrat.setProb(i,0);//reset action i
			//punishment[i-1] = payoffs[1-player];
			punishment[i-1] = -1*payoffs[1-player];
		}

		/*double max = punishment[0];
		for(int i = 1; i < actions; i++){
			if(max < punishment[i]){
				max = punishment[i];
			}
		}*/


		MixedStrategy robust = new MixedStrategy(actions);
		robust.setZeros();
		double sum = 0.0;
		double temp = 0.0;
		for(int i = 0; i < actions; i++){
			//punishment[i] = max - punishment[i];
			temp = Math.exp(lambda*punishment[i]);
			sum += temp;
			robust.setProb(i+1, temp);
		}
		
		for(int i = 1; i <= actions; i++){
			temp = robust.getProb(i);
			robust.setProb(i, temp / sum);
		}
		return robust;
	}
}
