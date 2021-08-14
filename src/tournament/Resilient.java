package tournament;
import util.*;
import games.*;

/**
 * Sample agent that always picks Rock (the first action). Remember to put your name in the author field,
 * change the name of the agent, and make your own solveGame(MatrixGame,PlayerNumber) method.
 * @author Oscar
 * @version 2015.10.04
 */
public class Resilient extends Player{
	protected final String newName = "R"; //Overwrite this variable in your player subclass
	private double lambda = 0.0;
	private double alpha = 0.0;
	private MixedStrategy opp;

	/**Your constructor should look just like this*/
	public Resilient() {
		super();
        playerName = newName;
	}

	public Resilient(double lam, double alp, MixedStrategy opponent) {
		super();
        playerName = newName+":"+lam+":"+alp;
		lambda = lam;
		alpha = alp;
		opp = opponent;
	}

	/**Your constructor should look just like this*/
	public Resilient(double lam, double alp) {
		this(lam,alp, new MixedStrategy(1));
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
    	if(lambda < 0){
			System.out.println("\u03BB should be positive");
			lambda = 0;
		}
		int actions = mg.getNumActions(playerNumber);
		opp = new MixedStrategy(actions);
		double maxvalue = mg.getExtremePayoffs(playerNumber)[0]; //0 is max, 1 is min
		double[] v1 = new double[actions];
		double[] v2 = new double[actions];
		MixedStrategy ownStrategy = new MixedStrategy(actions);
		ownStrategy.setZeros();
		double expected = 0.0;
		for(int i = 1; i <= actions; i++){
			ownStrategy.setProb(i, 1.0);
			if (playerNumber == 0)
				expected = SolverUtils.expectedPayoffs(ownStrategy, opp, mg)[playerNumber];
			else
				expected = SolverUtils.expectedPayoffs(opp, ownStrategy, mg)[playerNumber];
			v1[i-1] = expected;
			v2[i-1] = maxvalue-expected;
			ownStrategy.setProb(i, 0.0);
		}
		double[] v3 = new double[actions];
		for(int i = 0; i < actions; i++){
			v3[i] = alpha*v1[i] + (1-alpha)*v2[i];
		}
		//return SolverUtils.logit(v3, lambda);
		//return SolverUtils.computeBestResponse(mg,playerNumber, SolverUtils.computeBestResponse(mg, 1-playerNumber, SolverUtils.logit(v3, lambda)));
		//return SolverUtils.computeBestResponse(mg, playerNumber, SolverUtils.logit(v3, lambda));
		return SolverUtils.computeQuantalBestResponse(mg,playerNumber, SolverUtils.computeBestResponse(mg, 1-playerNumber, SolverUtils.logit(v3, lambda)),lambda);
    }

}
