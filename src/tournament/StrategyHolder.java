package tournament;

import games.MixedStrategy;
/**
 * Auxiliary class for NFG. Keeps the Strategies nice and organized.
 * @author Oscar
 *
 */
public class StrategyHolder {
	private MixedStrategy ms[];
	/**
	 * Default Constructor
	 */
	public StrategyHolder(){
		ms = new MixedStrategy[2];
	}
	/**
	 * Store a strategy for a player
	 * @param ms A strategy
	 * @param playerNum Row Player = 1, Column Player = 2
	 */
	public void addStrategy(MixedStrategy ms, int playerNum){
		/*if(playerNum == 1)
			ms1 = deepCopy(ms);
		else
			ms2 = deepCopy(ms);*/
		this.ms[playerNum - 1] = deepCopy(ms);
	}
	/**
	 * Creates a deep copy of a strategy. Avoid accidentally editing a reference later.
	 * @param ms Strategy to be copied
	 * @return copied Strategy
	 */
	private MixedStrategy deepCopy(MixedStrategy ms){
		MixedStrategy m = new MixedStrategy(ms.getNumActions());
		for(int a = 1; a <= ms.getNumActions(); a++)//actions always start at 1
			m.setProb(a, ms.getProb(a));
		return m;				
	}
	
	/**
	 * Get the stored strategy for a player
	 * @param playerNum Row Player = 1, Column Player = 2
	 * @return the mixed strategy
	 */
	public MixedStrategy getStrategy(int playerNum){
		/*if(playerNum == 1)
			return ms1;
		return ms2;*/
		return ms[playerNum -1];
	}
}
