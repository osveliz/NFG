package tournament;

import games.*;

/**
 * Sample agent that uses a uniform random strategy. Remember to put your name in the author field,
 * change the name of the agent, and make your own solveGame(MatrixGame,PlayerNumber) method.
 * @author Oscar
 * @version 2015.10.04
 */
public class UniformRandom extends Player{
	protected final String newName = "UniformRandom"; //Overwrite this variable in your player subclass

	/**Your constructor should look just like this*/
	public UniformRandom() {
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
    	return new MixedStrategy(mg.getNumActions(playerNumber));//default uniform random strategy
    }

}
