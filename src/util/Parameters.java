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
	//Number of repeated games
	private int repeat;
	//description
	private String description;
	/**
	 * Default parameters (a small game)
	 */
	public Parameters(){
		/*maxPayoff = 10;
		actions = 2;
		outcomeUncertainty = 0;
		payoffUncertainty = 0;
		type = GameType.GENERAL_SUM;*/
		this(10, 2, 0, 0, 0, GameType.GENERAL_SUM);
	}
	
	/**
	 * Specify each value
	 * @param m max payoff
	 * @param a number of actions per player
	 * @param o outcomes to change
	 * @param p payoff change amount
	 * @param r number of times repeating the game
	 * @param t game type
	 */
	public Parameters(int m, int a, int o, int p, int r, GameType t){
		maxPayoff = m;
		actions = a;
		outcomeUncertainty = o;
		payoffUncertainty = p;
		type = t;
		description = "";
		repeat = r;
		switch(t){
			case ZERO_SUM:
				description += "Zero Sum ";
				break;
			case RISK:
				description += "Risk v Reward ";
				break;
			default:
				description += "General Sum ";
				break;
		}
		if(outcomeUncertainty == 0)
			description += "- no uncertainty ";
		else if(outcomeUncertainty < a)
			description += "- few outcomes changed ";
		else
			description += "- many outcomes changed ";
		if(payoffUncertainty > 0 && payoffUncertainty < 0.1*maxPayoff)
			description += "with small intervals ";
		else if(payoffUncertainty >= 0.1*maxPayoff)
			description += "with large intervals ";
		if(repeat == 0)
			description += "one shot";
		else
			description += "repeated "+repeat+" times";
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
		return new Parameters(maxPayoff,actions,outcomeUncertainty,payoffUncertainty,repeat,type);
	}
	
	/**
	 * Gets number of times repeating
	 * @return number of times repeating
	 */
	public int getNumRepeat(){
		return repeat;
	}
	
	/**
	 * Contextualize the parameters
	 * @return the parameter descrption
	 */
	public String getDescription(){
		return description;
	}
}
	
