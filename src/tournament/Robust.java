package tournament;
import util.*;
import games.*;

/**
 * Sample agent that always picks Rock (the first action). Remember to put your name in the author field,
 * change the name of the agent, and make your own solveGame(MatrixGame,PlayerNumber) method.
 * @author Oscar
 * @version 2015.10.04
 */
public class Robust extends Player{
	protected final String newName = "Rob"; //Overwrite this variable in your player subclass
	private double lambda = 0.0;

	/**Your constructor should look just like this*/
	public Robust() {
		super();
        playerName = newName;
	}

	/**Your constructor should look just like this*/
	public Robust(double lam) {
		super();
        playerName = newName+":"+lam;
		lambda = lam;
	}
	
	/**
	 * Initialize is called at beginning of tournament.
	 * Use this time to decide on a strategy depending
	 * on the parameters.
	 */
	public void initialize(){
		//System.out.println("Solid Rock "+getParameters().getDescription());
	}
	
	/**
     * THIS METHOD SHOULD BE OVERRIDDEN 
     * GameMaster will call this to compute your strategy.
     * @param mg The game your agent will be playing
     * @param playerNumber Row Player = 0, Column Player = 1
     */
    protected MixedStrategy solveGame(MatrixGame mg, int playerNumber){
    	/*MixedStrategy ms = new MixedStrategy(mg.getNumActions(playerNumber));
    	ms.setProb(1, 1.0);//pure strategy that picks the 1st action
    	for(int a = 2; a <= mg.getNumActions(playerNumber);a++)
    		ms.setProb(a, 0);//set the rest of the strategy to 0
    	return ms;*/
		return SolverUtils.computeRobustResponse(mg,playerNumber,lambda);
    }

}
