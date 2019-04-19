package games;

import util.GenericTensor;
import java.util.Arrays;
import util.*;

/**
 * Class implements the straightforward normal form game.
 */

public class MatrixGame extends Game {

  private final GenericTensor<Payoffs> payoffs;

  /**
   * Constructor for a new matrix game.
   * @param numPlayers the number of players
   * @param numActions the number of actions
   */
  public MatrixGame(int numPlayers, int[] numActions){
    super(numPlayers, numActions);
    payoffs = new GenericTensor<Payoffs>(numActions);
    init();
  }

  /**
   * Create a copy of the given game as a matrix game
   * @param game convert type Game to MatrixGame
   */
  public MatrixGame(Game game) {
    super(game.getNumPlayers(), game.getNumActions());
    payoffs = new GenericTensor<Payoffs>(nActions);
    init();

    OutcomeIterator itr = game.iterator();
    while(itr.hasNext()) {
      int[] outcome = itr.next();
      setPayoffs(outcome, game.getPayoffs(outcome));
    }
  }

  /**
   * Create the initial payoff objects in the data structure
   */
  private void init() {
    // we need to initialize all of the payoff objects because the tensor does not
    // have access to this information
    for (int i = 0; i < payoffs.size(); i++) {
      payoffs.setValue(new Payoffs(nPlayers), i);
    }
  }

  /**
   * Returns the payoffs for a particular outcome
   * @param outcome   an array containing the actions chosen by each player
   * @return the payoffs
   */
  public double[] getPayoffs(int[] outcome){
    return payoffs.getValue(outcome).getPayoffs();
  }

  /**
   * Sets the payoff for a player for a given outcome.
   *
   * @param outcome   an array containing the actions chosen by each player
   * @param player    the player whose payoff should be returned.
   * @param value     the amount of the payoff
   */
  public void setPayoff(int[] outcome, int player, double value){
    payoffs.getValue(outcome).setPayoff(player, value);
  }

  /**
   * Sets the payoffs for all players
   * @param outcome action choices for each player
   * @param values payoff values for each player
   */
  public void setPayoffs(int[] outcome, double[] values){
    payoffs.getValue(outcome).setPayoffs(values);
  }
  /**
   * print all of the outcomes plus payoffs
   */
  public void printGame(){
	 OutcomeIterator itr = iterator();
	 while(itr.hasNext()) {
		 int[] outcome = itr.next();
		 System.out.println(Arrays.toString(outcome)+" "+Arrays.toString(getPayoffs(outcome)));
		 //System.out.println(Arrays.toString(getPayoffs(outcome, getPayoffs(outcome))));
	 }
  }
  
	/**
	 * Print a game as if it was a 2x2 matrix
	 */
	public void printMatrix(){
		int act = nActions[1];
		int i = 0;
		OutcomeIterator itr = iterator();
		while(itr.hasNext()) {
			int[] outcome = itr.next();
			System.out.print(Arrays.toString(SolverUtils.roundTenths(getPayoffs(outcome)))+" ");
			i++;
			if(i == act)
				System.out.println();
		}
	}
}

