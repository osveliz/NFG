package tournament;

import games.MixedStrategy;
import util.*;

/**
 * Creates timed threads for the Player.
 * Called by GameMaster when updating the player and getting the player's action.
 * @author Marcus
 */
public class PlayerDriver implements Runnable {
	
	/**Used to know which Player method to call*/
	public PlayerState state;
	/**Used to know which Player subclass to communicate with*/
	private Player player;
	/**Result to give to player**/
	private MixedStrategy opponentStrat;
	/**Save solution**/
	private MixedStrategy solution;
	/**Result to give to player**/
	private double expected[];
	/**Parameters**/
	private Parameters param;

	/**
	 * General constructor
	 * @param state a PlayerState
	 * @param player a Player
	 */
	public PlayerDriver(PlayerState state, Player player){
		param = new Parameters();
		this.state = state;
		this.player = player;
		opponentStrat = new MixedStrategy(param.getNumActions());
		expected = new double[2];
		solution = new MixedStrategy(param.getNumActions());
	}
	
	/**
	 * Constructor starting at intialize
	 * @param player a Player
	 */
	public PlayerDriver(Player player){
		param = new Parameters();
		state = PlayerState.INIT;
		this.player = player;
		opponentStrat = new MixedStrategy(param.getNumActions());
		expected = new double[2];
		solution = new MixedStrategy(param.getNumActions());
	}
	
	/**
	 * GameMaster will create a thread to run this class that will call a Player subclass'
	 * methods. Any exceptions or time outs will only harm this thread and will not affect GameMaster
	 */
	public void run() {
		try{
			switch(state){
			case INIT:
				player.initialize();
				state = PlayerState.SOLVE;
				break;
			case RESULT:
				player.addStrategy(player.getGameNumber(),opponentStrat,player.getOpponentNumber());
				state = PlayerState.SOLVE;
				break;
			case SOLVE:
				solution = player.solveGame();
				player.addStrategy(player.getGameNumber(), solution, player.getPlayerNumber());
				state = PlayerState.RESULT;
				break;
			default:
				System.out.println("Unknown state");
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		/*try{
			MixedStrategy ms = player.solveGame();
			player.addStrategy(player.getGameNumber(), ms, player.getPlayerNumber());
			state = PlayerState.COMPLETE;
		}catch(Exception e){
			e.printStackTrace();
		}*/
	}
	/**
	 * Save the results
	 * @param ms opponent's strategy
	 * @param payoffs the expected results
	 */
	public void saveResults(MixedStrategy ms, double payoffs[]){
		opponentStrat = ms;
		expected[0] = payoffs[0];
		expected[1] = payoffs[1];
	}
	
	/**
	 * Return saved solution
	 * @return saved solution
	 */
	public MixedStrategy getSolution(){
		return solution;
	}

}
