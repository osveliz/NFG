package tournament;

import java.util.ArrayList;

import games.*;

/**
 * Player agent.
 *
 * NOTE TO STUDENTS: The game master will only give player a copy of the game and tell you which player you are.
 * Your role is figure out your strategy.
 *
 * @author Marcus Gutierrez and Oscar Veliz
 * @version 04/15/2015
 */
public abstract class Player
{
    protected String playerName = "defaultPlayer"; //Overwrite this variable in your player subclass
    protected MatrixGame game;
    private int gameNumber;
    protected int playerNumber;
    private ArrayList<StrategyHolder> strategies;//interal saved for later use by GameMaster
    
    /**
     * Default Constructor
     */
    public Player()
    {
        strategies = new ArrayList<StrategyHolder>();
    }

    /**
     * Set game
     * @param the game in matrix form
     */
    public void setGame(MatrixGame game){
    	this.game = game;
    }
    /**
     * Sets the number of the game
     * @param gameNumber number of the game
     */
    public void setGame(int gameNumber){
    	this.gameNumber = gameNumber;
    }
    /**
     * Standard accessor
     * @return the game number
     */
    public int getGameNumber(){
    	return gameNumber;
    }
    /**
     * Set player number
     */
    public void setPlayerNumber(int playerNumber){
    	this.playerNumber = playerNumber;
    }
    /**
     * Standard accessor get current player number
     * @return
     */
    public int getPlayerNumber(){
    	return playerNumber;
    }

    /**
     * Get Agent Name used by GameMaster.
     * @return Name of player
     */
    public String getName(){return playerName;}

    /**
     * Player logic goes here in extended super agent. Do not try to edit this agent
     * @param mg the game
     * @param playerNum the player number
     * @return the mixed strategy
     */
    protected MixedStrategy solveGame(MatrixGame mg, int playerNum){
    	this.setGame(mg);
    	this.setPlayerNumber(playerNum);
    	return this.solveGame();
    
    }
    /**
     * Wrapper for the solveGame function
     * @return the mixed strategy developed by the player
     */
    protected MixedStrategy solveGame(){
    	return this.solveGame(this.game, this.playerNumber-1);
    }
    /**
     * Game Master stores a copy of the player strategies inside the player.
     * @param index Game number
     * @param ms Agent's strategy in the game when playing as playerNum
     * @param playerNum Row Player = 1, Column Player = 2
     */
    public void addStrategy(int index, MixedStrategy ms, int playerNum){
    	if(strategies.size() == index)
    		strategies.add(new StrategyHolder());
    	strategies.get(index).addStrategy(ms, playerNum);
    }
    /**
     * Standard accessor
     * @param index Game Number
     * @param playerNum Row Player = 1, Column Player = 2
     * @return the mixed strategy
     */
    public MixedStrategy getStrategy(int index, int playerNum){
    	if(index > strategies.size())
    		return null;
    	return strategies.get(index).getStrategy(playerNum);
    }
}
