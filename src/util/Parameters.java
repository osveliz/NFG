package util;

import games.*;
/**
 * Parameters for a tournament
 */
public class Parameters{
	//Maximum Payoff
	private int maxPayoff;
	//Actions per player
	private int actions;
	//Outcomes to change;
	private int outcomeUncertainty;
	//Payoff uncertainty
	private int payoffUncertainty;
	//Game Type
	private GameType type;
	/**
	 * Default parameters (a small game)
	 */
	public Parameters(){
		maxPayoff = 10;
		actions = 2;
		outcomeUncertainty = 0;
		payoffUncertainty = 0;
		type = GameType.GENERAL_SUM;
	}
	
	/**
	 * Specify each value
	 * @param m max payoff
	 * @param a number of actions per player
	 * @param o outcomes to change
	 * @param p payoff change amount
	 * @param t game type
	 */
	public Parameters(int m, int a, int o, int p, GameType t){
		maxPayoff = m;
		actions = a;
		outcomeUncertainty = o;
		payoffUncertainty = p;
		type = t;
	}
	
	/**
	 * Get max payoff
	 * @return max payoff
	 */
	public int getMaxPayoff(){
		return maxPayoff;
	}
	
	/**
	 * Get number of actions
	 * @return number of actions
	 */
	public int getNumActions(){
		return actions;
	}
	
	/**
	 * Get outcome uncerainty
	 * @return outcome uncertainty
	 */
	public int getOutcomeUncertainty(){
		return outcomeUncertainty;
	}
	
	/**
	 * Get payoff uncertainty
	 * @return payoff uncertainty
	 */
	public int getPayoffUncertainty(){
		return payoffUncertainty;
	}
	
	/**
	 * Get the game type
	 * @return the game type
	 */
	public GameType getGameType(){
		return type;
	}
	
	/**
	 * Create a copy of the parameters
	 * @return a copy
	 */
	public Parameters copy(){
		return new Parameters(maxPayoff,actions,outcomeUncertainty,payoffUncertainty, type);
	}
}
	
