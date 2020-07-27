package tournament;

import games.*;
import util.*;

/**
 * Sample agent that uses a uniform random strategy. Remember to put your name in the author field,
 * change the name of the agent, and make your own solveGame(MatrixGame,PlayerNumber) method.
 * @author Oscar
 * @version 2015.10.04
 */
public class Quantal extends Player{
	protected final String newName = "Quantal"; //Overwrite this variable in your player subclass
	protected double lambda;
	/**Your constructor should look just like this*/
	public Quantal() {
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
		lambda = 10;
	}
	
	/**
     * THIS METHOD SHOULD BE CHANGED 
     * GameMaster will call this to compute your strategy.
     * @param mg The game your agent will be playing
     * @param playerNumber Row Player = 0, Column Player = 1
     */
    /*protected MixedStrategy solveGame(MatrixGame mg, int playerNumber){
		int actions = mg.getNumActions(playerNumber);
		MixedStrategy s1 = new MixedStrategy(actions);//default uniform random strategy
		MixedStrategy s2 = new MixedStrategy(actions);
		MixedStrategy r1 = new MixedStrategy(actions);
		MixedStrategy r2 = new MixedStrategy(actions);
		MixedStrategy result = new MixedStrategy(actions);
		//result = SolverUtils.computeQuantalBestResponse(mg, playerNumber, s1, lambda);
		//System.out.println(result.toString());
		//mg.printMatrix();
		mg.printMatrix();
		int i = 0;
		lambda = 0;
		for(i = 0; i <= 30; i++){
			r1 = SolverUtils.computeQuantalBestResponse(mg, playerNumber, s1, lambda);
			r2 = SolverUtils.computeQuantalBestResponse(mg, 1-playerNumber, s2, lambda);
			s1.setProbs(r1);
			s2.setProbs(r2);
			//System.out.println(result.toString());
			//result.normalize();
			//s1.normalize();
		}
		if(playerNumber == 0)
			result = s1;
		else
			result = s2;
		result.normalize();
		System.out.println(result.toString());
    	return result;
    }*/
	protected MixedStrategy solveGame(MatrixGame mg, int playerNumber){
		lambda = 11;
		int actions = mg.getNumActions(playerNumber);
		MixedStrategy s1 = new MixedStrategy(actions);//default uniform random strategy
		MixedStrategy s2 = new MixedStrategy(actions);
		MixedStrategy result = new MixedStrategy(actions);
		MixedStrategy opponent = s1;
		if(playerNumber == 1)
			opponent = s2;
		result = SolverUtils.computeQuantalBestResponse(mg,playerNumber,opponent,lambda);
		return result;
	}
}
