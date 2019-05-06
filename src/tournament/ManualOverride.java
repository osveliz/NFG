package tournament;

import games.*;
import java.util.*;

/**
 * Agent that allows the user to enter a strategy
 * @author Oscar
 * @version 2019.05.05
 */
public class ManualOverride extends Player{
	protected final String newName = "ManualOverride"; //Overwrite this variable in your player subclass

	/**Your constructor should look just like this*/
	public ManualOverride() {
		super();
        playerName = newName;
	}
	
	/**
	 * Initialize is called at beginning of tournament.
	 * Use this time to decide on a strategy depending
	 * on the parameters.
	 */
	public void initialize(){
		//System.out.println("Manual Override "+param.getDescription());
	}
	
	/**
     * Asks the user for probabilities to enter via console
     * @param mg The game your agent will be playing
     * @param playerNumber Row Player = 0, Column Player = 1
     */
    protected MixedStrategy solveGame(MatrixGame mg, int playerNumber){
		if(playerNumber == 0)
			System.out.println("You are row player");
		else
			System.out.println("You are column player");
		mg.printMatrix();
		if(history.size() > 0)
			System.out.println("Last payoffs ["+lastPayoffs[0]+","+lastPayoffs[1]+"]");
		System.out.println("History");
		MixedStrategy strats[] = new MixedStrategy[mg.getNumPlayers()];
		for(int i = 0; i < history.size(); i++){
			for(int p = 0; p < strats.length; p++){
				if(p == 0)
					System.out.print("row "+i+" [ ");
				else
					System.out.print("col "+i+" [ ");
				strats[p] = history.get(i)[p];
				for(int j = 1; j <= mg.getNumActions(playerNumber); j++)
					System.out.print(strats[p].getProb(j)+" ");
				System.out.println("]");
			}
		}
    	MixedStrategy ms = new MixedStrategy(mg.getNumActions(playerNumber));
    	boolean valid = true;
    	Scanner scan = new Scanner(System.in);
    	do{
			System.out.println("Enter each probaility followed by return:");
			for(int a = 1; a <= mg.getNumActions(playerNumber);a++){
				ms.setProb(a, scan.nextDouble());//set the rest of the strategy to 0
			}
			valid = ms.isValid();
			if(!valid)
				System.out.println("Invalid strategy");
		}while(!ms.isValid());
    	return ms;
    }

}
