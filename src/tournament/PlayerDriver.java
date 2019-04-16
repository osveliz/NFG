package tournament;

import games.MixedStrategy;

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

	/**
	 * Constructor used for Player's initialize() and makeAction() methods
	 * @param state a PlayerState
	 * @param player a Player
	 */
	public PlayerDriver(PlayerState state, Player player){
		this.state = state;
		this.player = player;
	}
	
	/**
	 * GameMaster will create a thread to run this class that will call a Player subclass'
	 * methods. Any exceptions or time outs will only harm this thread and will not affect GameMaster
	 */
	public void run() {
		try{
			MixedStrategy ms = player.solveGame();
			player.addStrategy(player.getGameNumber(), ms, player.getPlayerNumber());
			state = PlayerState.COMPLETE;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
