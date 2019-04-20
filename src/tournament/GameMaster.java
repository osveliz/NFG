package tournament;

import util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import games.*;

/**
 * Normal Form Round-Robin Tournament Simulator (NFRRTS for short, we'll keep working on the name)
 * 
 * @author Marcus Gutierrez and Oscar Veliz
 * @version 2019.04.15
 */
public class GameMaster {

	private static boolean verbose = false; //Set to false if you do not want the details
	private static int numGames = 1; //test with however many games you want
	private static boolean zeroSum = false; //when true use zero sum games, when false use general sum
	private static ArrayList<MatrixGame> games = new ArrayList<MatrixGame>();
	private static Parameters param = new Parameters();
	/**
	 * Runs the tournament. Add your agent(s) to the list.
	 * 
	 * @param args not using any command line arguments
	 */
	public static void main(String[] args) {
		ArrayList<Player> players = new ArrayList<Player>();
		players.add(new UniformRandom());
		players.add(new SolidRock());
		//add your agent(s) here
		
		
		for(int setting = 0; setting < 6; setting++){
			switch(setting){
				case 0:
					System.out.println("Zero Sum Tournament - no uncertainty");
					param = new Parameters(100,10,0,0,GameType.ZERO_SUM);
					break;
				case 1:
					System.out.println("General Sum Tounament - no uncertainty");
					param = new Parameters(100,10,0,0,GameType.GENERAL_SUM);
					break;
				case 2:
					System.out.println("Risk v Reward Tournament - no uncertainty");
					param = new Parameters(100,10,0,0,GameType.RISK);
					break;
				case 3:
					System.out.println("Risk v Reward Tournament - some uncertainty");
					param = new Parameters(100,10,4,10,GameType.RISK);
					break;
				case 4:
					System.out.println("Risk v Reward Tournament - a lot of uncertainty");
					param = new Parameters(100,10,20,20,GameType.RISK);
					break;
				default:
					System.out.println("Basic small general sum game no uncertainty");
					param = new Parameters();
					break;
			}
			games = GameGenerator.generate(numGames,param);
			if(games.size()==0){//safety net
				System.out.println("Could Not Create Games");
				System.exit(0);
			}
			computeStrategies(players);
			
			//obfuscate (will not change if outcome uncertainty is zero)
			GameGenerator.obfuscate(games,param);
			
			//compute expected payoffs
			double[][] payoffMatrix = new double[players.size()][players.size()];
			double[] wins = new double[players.size()];
			int numPlayers = players.size();
			for(int p1 = 0; p1 < numPlayers; p1++) {
				for(int p2 = p1; p2 < numPlayers; p2++) {
					for (int game = 0; game < numGames; game++) {
						Player player1 = players.get(p1);
						Player player2 = players.get(p2);
						if(verbose)	System.out.println("Game number" + game);
						if(verbose) System.out.println(player1.getName()+" vs "+player2.getName());
						double[] payoffs = match(player1,player2,game);
						updateResults(payoffMatrix,payoffs,p1,p2,wins);
						if(verbose) System.out.println(payoffs[0]);
						if(verbose) System.out.println(payoffs[1]);
						if(verbose) System.out.println(player2.getName()+" vs "+player1.getName());
						payoffs = match(player2,player1,game);
						updateResults(payoffMatrix,payoffs,p2,p1,wins);
						if(verbose) System.out.println(payoffs[0]);
						if(verbose) System.out.println(payoffs[1]);
						if(verbose) System.out.println();
					}
				}
			}
			//average the payoff matrix
			for(int i = 0; i < payoffMatrix.length; i++)
				for(int j= 0; j < payoffMatrix[i].length; j++)
					payoffMatrix[i][j] = payoffMatrix[i][j]/(2*numGames*payoffMatrix.length);	
			if(verbose) printMatrix(payoffMatrix,players);
			
			//compute results
			double[] expPayoff = calculateAverageExpectedPayoffs(payoffMatrix);
			//double[] regrets = calculateRegrets(payoffMatrix);
			double[] stabilities = calculateStabilities(payoffMatrix);
			//double[] reverse = calculateReversePayoffs(payoffMatrix);
			//print summary regardless of verbose
			playerArrayPrinter("Total Wins",players, wins);
			playerArrayPrinter("Overall Average Expected Utility", players, expPayoff);
			//playerArrayPrinter("Tournament Regret",players,regrets);
			playerArrayPrinter("Tournament Stabilities",players,stabilities);
			//playerArrayPrinter("Expected Reverse Utility",players,reverse);
		}
		System.exit(0);//just to make sure it exits
	}

	/**
	 * Tries to execute a Player class' method by using threads for protection in case
	 * the Player subclasses crash or time out.
	 * 
	 * @param pDriver The thread that will execute the player
	 */
	private static void tryPlayer(PlayerDriver pDriver){
		int timeLimit = 2000;//2s or 2000ms
		Thread playerThread = new Thread(pDriver);
		playerThread.start();
		for(int sleep = 0; sleep < timeLimit; sleep+=10){
			if(playerThread.isAlive())
				try {Thread.sleep(10);} catch (Exception e) {e.printStackTrace();}
			else
				return;
		}
	}

	/**
	 * Figures out every agent strategy for every game for all players within a game
	 * @param p The agents being run in the tournament
	 */
	private static void computeStrategies(ArrayList<Player> p){
		ArrayList<PlayerDriver> drivers = new ArrayList<PlayerDriver>();
		for(int pd = 0; pd < p.size(); pd++)
			drivers.add(new PlayerDriver(PlayerState.SOLVE,p.get(pd)));
		for(int gameNumber = 0; gameNumber < numGames; gameNumber++){
			MatrixGame mg = new MatrixGame(games.get(gameNumber));//gives the agent a copy of the game
			for(int playerIndex = 0; playerIndex < p.size(); playerIndex++){
				Player player = p.get(playerIndex);
				for(int playerNumber = 1; playerNumber <= 2; playerNumber++){
					player.setGame(gameNumber);//legacy
					player.setGame(new MatrixGame(mg));
					player.setPlayerNumber(playerNumber);
					tryPlayer(new PlayerDriver(PlayerState.SOLVE,player));
				}
			}				
		}
	}
	
	/**
	 * Reads all the games used in the tournament
	 * ***DEPRICATED***
	 */
	public static void readGames(){
		String type = "";
		if(zeroSum)
			type = "ZeroSum-";
		else
			type = "GeneralSum-";
		for(int i = 0; i < numGames; i++){
			try{
				games.add(new MatrixGame(GamutParser.readGamutGame(type+i+".game")));
			}catch(Exception e){System.out.println("Could not read "+type+i+".game");}
		}
	}
	
	/**
	 * A single individual match between to players, the first player is row the second is column
	 * 
	 * If a strategy for a player is invalid it will assign a payoff of -1337 to that player
	 * @param p1 row player
	 * @param p2 column player
	 * @param gameNumber game
	 * @return the payoffs of the match
	 */
	public static double[] match(Player p1, Player p2, int gameNumber){
		MixedStrategy[] strats = new MixedStrategy[2];
		int player = 1;
		strats[0] = p1.getStrategy(gameNumber, player);
		player = 2;
		strats[1] = p2.getStrategy(gameNumber, player);
		List<MixedStrategy> list = new ArrayList<MixedStrategy>();
		list.add(strats[0]);
		list.add(strats[1]);
		OutcomeDistribution distro = new OutcomeDistribution(list);
		if(strats[0].isValid() && strats[1].isValid())
			return SolverUtils.computeOutcomePayoffs(games.get(gameNumber),distro);
		else if(!strats[0].isValid() && !strats[1].isValid()){
			System.out.println("Invalid strategy for Players: "+p1.getName()+" and "+p2.getName()+" on game number "+gameNumber);
			double[] payoffs = {-1337,-1337};
			return payoffs;
		}
		else if(!strats[0].isValid()){
			System.out.println("Invalid strategy for Player: "+p1.getName()+" on game number "+gameNumber);
			double[] payoffs = {-1337,0};
			return payoffs;
		}
		else{//strats[1] must be invalid
			System.out.println("Invalid strategy for Player: "+p2.getName()+" on game number "+gameNumber);
			double[] payoffs = {0,-1337};
			return payoffs;
		}
	
	}
	/**
	 * Computes and stores the results of a match given the expected payoffs
	 * @param matrix Payoff Matrix
	 * @param payoffs results of a match
	 * @param p1 Player 1
	 * @param p2 Player 2
	 * @param wins data structure that stores wins
	 */
	public static void updateResults(double[][] matrix, double[] payoffs, int p1, int p2, double[]wins){
		matrix[p1][p2] = matrix[p1][p2] + payoffs[0];
		matrix[p2][p1] = matrix[p2][p1] + payoffs[1];
		if(payoffs[0]==payoffs[1]){//tie
			wins[p1]+=0.5;
			wins[p2]+=0.5;
		}
		else if(payoffs[0] > payoffs[1])
			wins[p1]++;
		else
			wins[p2]++;
			
	}
	
	/**
	 * Want to visualize the results of a tournament? Call this function.
	 * @param matrix Payoff Matrix
	 * @param players List of Players
	 */
	public static void printMatrix(double[][] matrix, ArrayList<Player> players){
		for(int i = 0; i < players.size(); i++)
			System.out.print("\t"+players.get(i).getName());
		System.out.println();
		for(int i = 0; i < matrix.length; i++){
			System.out.print(players.get(i).getName());
			for(int j = 0; j < matrix[i].length; j++){
				System.out.print("\t"+matrix[i][j]);
			}
			System.out.println();
		}
	}
	/**
	 * Calculate average expected payoffs for each player
	 * @param matrix the tournament payoff matrix
	 * @return average expected payoffs
	 */
	public static double[] calculateAverageExpectedPayoffs(double[][] matrix){
		double[] expPayoff = new double[matrix.length];
		for(int i = 0; i < matrix.length; i++){
			for(int j = 0; j < matrix[i].length; j++){
				expPayoff[i] = expPayoff[i] + matrix[i][j];
			}
			expPayoff[i] = expPayoff[i] / matrix[i].length;
		}
		return expPayoff;
	}
	/**
	 * Calculates the regrets for every agent and stores them in the array.
	 * @param matrix the tournament payoffs
	 * @return array of regrets in the tournament
	 */
	public static double[] calculateRegrets(double[][] matrix) {
		double[] regret = new double[matrix.length];
		for (int i = 0; i< matrix.length; i++){
			double max = maximum(matrix[i]);
			for (int j = 0; j < matrix[i].length; j++) {
				regret[i] += (max - matrix[i][j]) / regret.length;
			}
		}
		return regret;
	}
	
	/**
	 * Calculates the average payoffs scored against each agent (the reverse payoff)
	 * @param matrix array of tournament payoffs
	 * @return the reverse of how the players did
	 */
	public static double[] calculateReversePayoffs(double[][] matrix){
		double[] reverse = new double[matrix.length];
		for(int i = 0; i < reverse.length; i++){
			for(int j = 0; j < matrix.length; j++){
				reverse[i] = reverse[i] + matrix[j][i];
			}
			reverse[i] = reverse[i] / reverse.length;
		}
		return reverse;
	}
	
	/**
	 * Calculates the stabilities for every agents and stores them in the array.
	 * (Taken from  GameUtils class in ega.games package.)
	 * @param matrix the payoffs in the tournament
	 * @return stability value (how likely to switch strategies)
	 */
	public static double[] calculateStabilities(double[][] matrix){
		double[] stabilities = new double[matrix.length];
		Arrays.fill(stabilities, Double.NEGATIVE_INFINITY);
		for (int strat = 0; strat < matrix.length; strat++) {
			double base = matrix[strat][strat];
			for (int row = 0; row < matrix.length; row++) {
				if (row == strat)
					continue;
				double btd = matrix[row][strat] - base;
				stabilities[strat] = Math.max(stabilities[strat], btd);
			}
		}
		return stabilities;
	}

	/**
	 * Returns the maximum number in the given array of doubles.
	 * 
	 * @param a the array of doubles.
	 * @return the maximum number in the array.
	 */
	public static double maximum(double[] a) {
		double max = a[0];
		for (int i = 1; i < a.length; i++)
			if (max < a[i])
				max = a[i];
		return max;
	}
	
	/**
	 * Player Array printer
	 * @param text general text usually a heading
	 * @param players the list of players
	 * @param array the values to be printed
	 */
	public static void playerArrayPrinter(String text, ArrayList<Player> players, double[] array){
		System.out.println("\n"+text);
		for(int i = 0; i < array.length; i++)
			System.out.println(players.get(i).getName()+"\t"+array[i]);
	}
}
