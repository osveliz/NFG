package tournament;
import util.*;
import games.*;

/**
 * MinMax strategy
 * @author Oscar
 * @version 2021.06.25
 */
public class Punish extends Player{
	protected final String newName = "Punish"; //Overwrite this variable in your player subclass

	/**Your constructor should look just like this*/
	public Punish() {
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
    	//return new MixedStrategy(mg.getNumActions(playerNumber));//default uniform random strategy
		return SolverUtils.computeMinMax(mg, playerNumber);
    }

}
