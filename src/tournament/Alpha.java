package tournament;
import util.*;
import games.*;

/**
 * Sample agent that always picks Rock (the first action). Remember to put your name in the author field,
 * change the name of the agent, and make your own solveGame(MatrixGame,PlayerNumber) method.
 * @author Oscar
 * @version 2015.10.04
 */
public class Alpha extends Player{
	protected final String newName = "Alph"; //Overwrite this variable in your player subclass
	private double lambda = 0.0;
	private double alpha = 1.0;

	/**Your constructor should look just like this*/
	public Alpha() {
		super();
        playerName = newName;
	}

	/**Your constructor should look just like this*/
	public Alpha(double lam, double alp) {
		super();
        playerName = newName+":"+lam+":"+alp;
		lambda = lam;
		alpha = alp;
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
		MixedStrategy rob = SolverUtils.computeRobustResponse(mg,playerNumber,lambda);
		MixedStrategy nem = SolverUtils.computeNemesis(mg, playerNumber, rob);
		MixedStrategy brn = SolverUtils.computeBestResponse(mg, playerNumber, nem);
		return SolverUtils.interpolate(rob, brn, alpha);
    }

}
