package tournament;

import games.*;
import tournament.*;

/**
 * Sample agent that uses a uniform random strategy. Remember to put your name in the author field,
 * change the name of the agent, and make your own solveGame(MatrixGame,PlayerNumber) method.
 * @author Oscar
 * @version 2015.10.04
 */
public class HalfHalf extends Player{
	protected final String newName = "HalfHalf"; //Overwrite this variable in your player subclass

	/**Your constructor should look just like this*/
	public HalfHalf() {
		super();
        playerName = newName;
	}
	/**
	 * Initialize is called at beginning of tournament.
	 * Use this time to decide on a strategy depending
	 * on the parameters.
	 */
	public void initialize(){
		//System.out.println("Uniform Random "+getParameters().getDescription());
	}
	
	/**
     * THIS METHOD SHOULD BE CHANGED 
     * GameMaster will call this to compute your strategy.
     * @param mg The game your agent will be playing
     * @param playerNumber Row Player = 0, Column Player = 1
     */
    protected MixedStrategy solveGame(MatrixGame mg, int playerNumber){
		int actions = mg.getNumActions(playerNumber);
		MixedStrategy s1 = new MixedStrategy(actions);//default uniform random strategy
		EpsNE ene = new EpsNE();
		MixedStrategy s2 = ene.solveGame(mg,playerNumber);
		//s2.setZeros();
		//s2.setProb(1,0.75);
		//s2.setProb(2,1.0);
		MixedStrategy result = new MixedStrategy(s1,s2,.5);
		System.out.println(result.toString());
		result.normalize();
		System.out.println(result.toString());
    	return result;
    }

}
