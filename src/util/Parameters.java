package util;

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
	/**
	 * Default parameters
	 */
	public Parameters(){
		maxPayoff = 100;
		actions = 10;
		outcomeUncertainty = 0;
		payoffUncertainty = 0;
	}
	
	/**
	 * Specify each value
	 * @param m max payoff
	 * @param a number of actions
	 * @param o outcomes to change
	 * @param p payoff change amount
	 */
	public Parameters(int m, int a, int o, int p){
		maxPayoff = m;
		actions = a;
		outcomeUncertainty = o;
		payoffUncertainty = p;
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
	
	public Parameters copy(){
		return new Parameters(maxPayoff,actions,outcomeUncertainty,payoffUncertainty);
	}
}
	
