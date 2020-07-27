package tournament;

import games.*;
import java.util.*;
import util.*;

import java.util.Arrays;

// problem detected, quantalResponse(...)

/**
 * The Class CreateNewNormalFormAgent.
 * 
 * @author Oscar Veliz
 * @author Erick Augusto
 * @since 2015/06/23 (yyyy/mm/dd)
 * @version 2015/07/20
 */
public class CognitiveHierarchy extends Player{
	protected final String newName = "CH"; //Overwrite this variable in your player subclass

	protected int level;
	protected double tau;
	protected boolean quantal;
	protected double lambda;

	/**
	 * Instantiates a new Cognitive Hyerarchy agent and.
	 *
	 * @param name
	 *            the agent name
	 */
	public CognitiveHierarchy() {
		super();
        playerName = newName;        
	}

	/**
	 * Sets the default values for variables.
	 */
	public void initialize(){
		level = 12;
		tau = 1.5;
		lambda = 10.0;
		quantal = false;
	}

	
	/**
	 * Gets the level.
	 *
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Sets the level.
	 *
	 * @param level
	 *            the new level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Gets the tau.
	 *
	 * @return the tau
	 */
	public double getTau() {
		return tau;
	}

	/**
	 * Sets the tau.
	 *
	 * @param tau
	 *            the new tau
	 */
	public void setTau(double tau) {
		this.tau = tau;
	}

	/**
	 * Checks if is quantal.
	 *
	 * @return true, if is quantal
	 */
	public boolean isQuantal() {
		return quantal;
	}

	/**
	 * Sets the quantal.
	 *
	 * @param quantal
	 *            the new quantal
	 */
	public void setQuantal(boolean quantal) {
		this.quantal = quantal;
	}

	/**
	 * Gets the lambda.
	 *
	 * @return the lambda
	 */
	public double getLambda() {
		return lambda;
	}

	/**
	 * Sets the lambda.
	 *
	 * @param lambda
	 *            the new lambda
	 */
	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	/**
	 * Solve game.
	 *
	 * @return the mixed strategy
	 */
	protected MixedStrategy solveGame(MatrixGame mg, int playerNumber){		
		int player = playerNumber;
		int numActions = mg.getNumActions(0);
		MixedStrategy[][] strats = new MixedStrategy[level + 1][2];
		// level-0 starts with uniform random
		strats[0][player] = new MixedStrategy(numActions);
		strats[0][1 - player] = new MixedStrategy(numActions);
		// calculate P(level, tau) = (e^(-tau)*(tau^m))/m!
		double[] poi = new double[level];
		for (int i = 0; i < poi.length; i++) {
			poi[i] = Math.exp(-1 * tau) * Math.pow(tau, i) / fact(i); // OK
		}
		double poiSum = 0;
		double[] norm;
		// calculate level-1 through level-k
		for (int k = 1; k <= level; k++) {
			// normalize the poisson values for the k levels
			poiSum = 0;
			//System.out.println("\nk = "+k);
			for (int i = 0; i < k; i++) {
				poiSum += poi[i]; // OK
			}
			norm = new double[k];
			for (int i = 0; i < norm.length; i++) {
				norm[i] = poi[i] / poiSum; // OK
			}
			MixedStrategy[] temp = new MixedStrategy[2];
			temp[0] = new MixedStrategy(numActions);
			temp[1] = new MixedStrategy(numActions);
			// compute strategy distros for both players by combining
			// levels 0 through k (each multiplied by norm_poisson(level).
			for (int p = 0; p < 2; p++) {
				MixedStrategy m = temp[p];
				//for (int a = 1; a <= numActions; a++)
					//m.setProb(a, 0.0);
				m.setZeros();
				for (int i = 0; i < k; i++){
					for (int a = 1; a <= numActions; a++) {
						m.setProb(a, m.getProb(a) + norm[i] * strats[i][p].getProb(a));
					}
				}
				if (!m.isValid())
					m.normalize();
			}
			// now compute the strategy for the current level
			if (quantal) {// quantal logit
				//strats[k][player] = quantalResponse(temp, game, numActions,1 - player, lambda);
				strats[k][player] = SolverUtils.computeQuantalBestResponse(mg, playerNumber, temp[1-player], lambda);
				//strats[k][1 - player] = quantalResponse(temp, game, numActions,player, lambda);
				strats[k][1-player] = SolverUtils.computeQuantalBestResponse(mg, 1-playerNumber, temp[player], lambda);
			} else {// regular best response
				strats[k][player] = SolverUtils.computeBestResponse(mg, player, temp[1-player]);
				strats[k][1 - player] = SolverUtils.computeBestResponse(mg, 1-player, temp[player]);
			}
		}
		//System.out.println("level = "+level);
		//System.out.println("strats length = "+strats.length);
		//System.out.println("got over here");
		// if using quantal best response use br after calculating final logit
		if (quantal)
			//return bestResponse(strats[level], game, numActions, 1 - player);
			return SolverUtils.computeQuantalBestResponse(mg, playerNumber, strats[level][1-player], lambda);
		//System.out.println("what is the answer?");
		//System.out.println("Here "+strats[level-1][player].toString());
		//System.out.println("level = "+level);
		//System.out.println("strats length = "+strats.length);

		return strats[level][player];// return final answer
	}

	/**
	 * Pure Strategy Best Response.
	 *
	 * @param out
	 *            outcome
	 * @param g
	 *            game
	 * @param actions
	 *            number of actions
	 * @param opponent
	 *            player 0 or 1
	 * @return pure strategy best response
	 */
	public MixedStrategy bestResponse(MixedStrategy[] out, MatrixGame g, int actions, int opponent) {
		OutcomeDistribution outcome = new OutcomeDistribution(Arrays.asList(out));
		MixedStrategy response = new MixedStrategy(actions);
		double[] stratPayoffs = SolverUtils.expectedPayoffs(out[0],out[1],g);
		response.setBestResponse(stratPayoffs);
		return response;
	}

	/**
	 * factorial.
	 *
	 * @param a
	 *            input
	 * @return a!
	 */
	public long fact(int a) {
		if (a < 2)
			return 1;
		else {
			long fac = a;
			for (int i = a - 1; i > 1; i--)
				fac = fac * i;
			return fac;
		}
	}

	/**
	 * Quantal response.
	 *
	 * @param out
	 *            the out
	 * @param g
	 *            the g
	 * @param actions
	 *            the actions
	 * @param opponent
	 *            the opponent
	 * @param lambda
	 *            the lambda
	 * @return the mixed strategy
	 */
	/*public MixedStrategy quantalResponse(MixedStrategy[] out, NormalFormGame g,
			int actions, int opponent, double lambda) {
		MixedStrategy response = new MixedStrategy(actions);
		MixedStrategy[] distro = new MixedStrategy[2];
		distro[0] = out[0].clone();
		distro[1] = out[1].clone();
		// compute logit quantal best response
		double[] expa = new double[actions];
		double[] pa = new double[actions];
		for (int i = 0; i < expa.length; i++) {
			// compute payoffs p against each pure strategy a
			int a = i + 1;
			MixedStrategy temp = new MixedStrategy(actions);
			for (int z = 1; z <= actions; z++)
				temp.setProb(z, 0);
			temp.setProb(a, 1);
			distro[1 - opponent] = temp.clone();
			distro[opponent] = out[opponent].clone();
			OutcomeDistribution outcome = new OutcomeDistribution(
					Arrays.asList(distro));
			double[] p = SolverUtils.computeOutcomePayoffs(g, outcome);
			pa[i] = p[1 - opponent];
			expa[i] = Math.exp(lambda * pa[i]);
		}
		double expaSum = 0;
		for (int i = 0; i < expa.length; i++)
			expaSum += expa[i];
		for (int i = 0; i < actions; i++)
			response.setProb(i + 1, expa[i] / (expaSum));
		// avoid underflow
		double max = lambda * pa[0];
		for (int i = 1; i < pa.length; i++)
			if (max < lambda * pa[i])
				max = lambda * pa[i];
		double denom = 0;
		for (int i = 0; i < pa.length; i++)
			denom += Math.exp(lambda * pa[i] - max);
		denom = Math.log(denom) + max;
		MixedStrategy response2 = new MixedStrategy(actions);

		for (int i = 1; i <= actions; i++) {
			double numerator = Math.exp(lambda * pa[i - 1]);
			double denominator = Math.exp(denom);
			numerator = replaceInfinite(numerator);
			denominator = replaceInfinite(denominator);
			response2.setProb(i, numerator / denominator);
		}

		// normalize if it needs it
		if (!response2.isValid()) // ok
			response2.normalize();
		// return response;

		return response2;
	}*/

	/**
	 * Replace infinite.
	 * 
	 * @author Erick Augusto
	 *
	 * @param number
	 *            the number
	 * @return the double
	 */
	private double replaceInfinite(double number) {
		if (Double.isFinite(number))
			return number;
		if (number == Double.POSITIVE_INFINITY)
			return Double.MAX_VALUE;
		else
			return Double.MIN_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see agents.normalformagents.NormalFormAgent#toString()
	 */
	public String toString() {
		return super.toString() + "_L" + level;
	}

}
