package tournament;

import util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
	private static int maxPayoff = 50; //100 is usually pretty good
	private static int numGames = 0; //use small number when developing, increase when ready to really test
	private static int numActions = 3; //use small number when developing, increase when ready to run tests
	private static boolean zeroSum = false; //when true use zero sum games, when false use general sum
	private static ArrayList<MatrixGame> games = new ArrayList<MatrixGame>();
	private static Parameters param = new Parameters();
	private static final int timeLimit = 1000; //1000 milliseconds
	
	/**
	 * Runs the tournament. Add your agent(s) to the list.
	 * @param args not using any command line arguments
	 */
	public static void main(String[] args) {
		MatrixGame pris = GameGenerator.prisonners();
		//pris.printGame();
		double lambda = 10;
		System.out.println("lambda = "+lambda);
		MixedStrategy opponentStrat = new MixedStrategy(2);
		opponentStrat.setZeros();
		opponentStrat.setProb(1, 1);
		MixedStrategy qbr = SolverUtils.computeQuantalBestResponse(pris, 0, opponentStrat, lambda);
		System.out.println(qbr.toString());





		ArrayList<Player> players = new ArrayList<Player>();
		players.add(new UniformRandom());
		//players.add(new SolidRock());
		//players.add(new Linear());
		//players.add(new HalfHalf());
		//players.add(new EpsNE());
		//CognitiveHierarchy quant = new CognitiveHierarchy();
		//quant.updateName("qlk");
		//players.add(quant);
		//players.add(new QuantalLevelK());
		//players.add(new CognitiveHierarchy());
		players.add(new Quantal());
		//players.add(new ManualOverride());
		//add your agent(s) here
		
		ArrayList<Parameters> settings = new ArrayList<Parameters>();
		settings.add(new Parameters(maxPayoff,numActions,0,0,0,GameType.ZERO_SUM));
		//settings.add(new Parameters(maxPayoff,numActions,0,0,0,GameType.GENERAL_SUM));
		//settings.add(new Parameters(maxPayoff,numActions,0,0,0,GameType.RISK));
		//settings.add(new Parameters(maxPayoff,numActions,4,5,0,GameType.RISK));
		//settings.add(new Parameters(maxPayoff,numActions,5,1,0,GameType.GENERAL_SUM));
		//settings.add(new Parameters(maxPayoff,numActions,numActions*numActions,20,0,GameType.RISK));
		//settings.add(new Parameters(maxPayoff,numActions,0,0,4,GameType.GENERAL_SUM));
		//settings.add(new Parameters(maxPayoff,numActions,numActions*numActions/2,20,5,GameType.RISK));
		for(int setting = 0; setting < settings.size(); setting++){
			param = settings.get(setting);
			System.out.println(param.getDescription());
			games = GameGenerator.generate(numGames,param);
			if(games.isEmpty()){//safety net
				System.out.println("Could Not Create Games");
				System.exit(0);
			}			
			//update agents with parameters
			for(int c = 0; c < players.size(); c++){
				players.get(c).setParameters(param.copy());
				tryPlayer(new PlayerDriver(players.get(c)));//run init
			}
			if(settings.get(setting).getNumRepeat() == 0)
				computeStrategies(players);
			//obfuscate (will not change if outcome uncertainty is zero)
			ArrayList<MatrixGame> gamesCopy = new ArrayList<MatrixGame>();
			Iterator<MatrixGame> itr = games.iterator();
			while(itr.hasNext()){
				gamesCopy.add(new MatrixGame(itr.next()));
			}
			GameGenerator.obfuscate(games,param);
			
			//compute expected payoffs
			double[][] payoffMatrix = new double[players.size()][players.size()];
			double[] wins = new double[players.size()];
			int numPlayers = players.size();
			double[] payoffs = new double[2];
			MixedStrategy strats[] = new MixedStrategy[2];
			for(int p1 = 0; p1 < numPlayers; p1++) {
				for(int p2 = p1; p2 < numPlayers; p2++) {
					for (int game = 0; game < numGames; game++) {
						if(param.getNumRepeat() < 1){
							Player player1 = players.get(p1);
							Player player2 = players.get(p2);
							if(verbose)	System.out.println("Game number" + game);
							if(verbose) System.out.println(player1.getName()+" vs "+player2.getName());
							payoffs = match(player1,player2,game,gamesCopy.get(game));
							updateResults(payoffMatrix,payoffs,p1,p2,wins);
							if(verbose) System.out.println(payoffs[0]);
							if(verbose) System.out.println(payoffs[1]);
							if(verbose) System.out.println(player2.getName()+" vs "+player1.getName());
							payoffs = match(player2,player1,game,gamesCopy.get(game));
							updateResults(payoffMatrix,payoffs,p2,p1,wins);
							if(verbose) System.out.println(payoffs[0]);
							if(verbose) System.out.println(payoffs[1]);
							if(verbose) System.out.println();
						}
						else if(p1 != p2){
							MatrixGame mg = new MatrixGame(gamesCopy.get(game));//gives the agent a copy of the game
							Player player1 = players.get(p1);
							Player player2 = players.get(p2);
							player1.resetHistory();
							player2.resetHistory();
							player1.setGame(game);//legacy
							player2.setGame(game);//legacy
							player1.setGame(new MatrixGame(mg));
							player2.setGame(new MatrixGame(mg));
							player1.setPlayerNumber(1);
							player2.setPlayerNumber(2);
							if(verbose)	System.out.println("Game number" + game);
							if(verbose) System.out.println(player1.getName()+" vs "+player2.getName());
							for(int repeat = 0; repeat < param.getNumRepeat(); repeat++){
								if(verbose)System.out.println("repeat "+repeat);
								payoffs = repeater(player1,player2,games.get(game));
								updateResults(payoffMatrix,payoffs,p1,p2,wins);
								if(verbose) System.out.println(payoffs[0]);
								if(verbose) System.out.println(payoffs[1]);
								if(verbose) System.out.println(player2.getName()+" vs "+player1.getName());
							}
							player1.resetHistory();
							player2.resetHistory();
							player1.setGame(new MatrixGame(mg));
							player2.setGame(new MatrixGame(mg));
							player1.setPlayerNumber(2);
							player2.setPlayerNumber(1);
							for(int repeat = 0; repeat < param.getNumRepeat(); repeat++){
								if(verbose)System.out.println("repeat "+repeat);
								payoffs = repeater(player2,player1,games.get(game));
								updateResults(payoffMatrix,payoffs,p2,p1,wins);
								if(verbose) System.out.println(payoffs[0]);
								if(verbose) System.out.println(payoffs[1]);
								if(verbose) System.out.println(player2.getName()+" vs "+player1.getName());
							}
						}
					}
				}
			}
			//average the payoff matrix
			for(int i = 0; i < payoffMatrix.length; i++)
				for(int j= 0; j < payoffMatrix[i].length; j++)
					payoffMatrix[i][j] = payoffMatrix[i][j]/(2*numGames*payoffMatrix.length*(param.getNumRepeat()+1));	
			if(verbose) printMatrix(payoffMatrix,players);
			
			//compute results
			double[] expPayoff = calculateAverageExpectedPayoffs(payoffMatrix);
			//double[] regrets = calculateRegrets(payoffMatrix);
			double[] stabilities = calculateStabilities(payoffMatrix);
			double[] minimums = findMinimums(payoffMatrix);
			//double[] reverse = calculateReversePayoffs(payoffMatrix);
			//print summary regardless of verbose
			playerArrayPrinter("Total Wins",players, wins);
			playerArrayPrinter("Overall Average Expected Utility", players, expPayoff);
			//playerArrayPrinter("Tournament Regret",players,regrets);
			playerArrayPrinter("Tournament Stabilities",players,stabilities);
			//playerArrayPrinter("Expected Reverse Utility",players,reverse);
			playerArrayPrinter("Minimum Per Player",players,minimums);
			
			System.out.println();
		}
		System.exit(0);//just to make sure it exits
	}
	/**
	 * Execute two agents, add to their history, return the payoffs
	 * @param p1 player 1
	 * @param p2 player 2
	 * @param game the game they're playing (don't need to copy)
	 * @return the expected payoffs
	 */
	private static double[] repeater(Player p1, Player p2, MatrixGame game){
		double payoffs[] = new double[2];
		MixedStrategy strats[] = new MixedStrategy[2];
		PlayerDriver pd1 = new PlayerDriver(PlayerState.SOLVE,p1);
		tryPlayer(pd1);
		strats[0] = pd1.getSolution();
		PlayerDriver pd2 = new PlayerDriver(PlayerState.SOLVE,p2);
		tryPlayer(pd2);
		strats[1] = pd2.getSolution();
		p1.addHistory(strats);
		p2.addHistory(strats);
		payoffs = match(strats[0],strats[1],game);		
		p1.saveLastPayoffs(payoffs);
		p2.saveLastPayoffs(payoffs);
		return payoffs;
	}

	/**
	 * Tries to execute a Player class' method by using threads for protection in case
	 * the Player subclasses crash or time out.
	 * 
	 * @param pDriver The thread that will execute the player
	 */
	private static void tryPlayer(PlayerDriver pDriver){
		//int timeLimit = 2000;//2s or 2000ms
		Thread playerThread = new Thread(pDriver);
		playerThread.start();
		if(!pDriver.isOverride()){
			for(int sleep = 0; sleep < timeLimit; sleep+=10){
				if(playerThread.isAlive())
					try {Thread.sleep(10);} catch (Exception e) {e.printStackTrace();}
				else
					return;
			}
		}
		else{
			while(playerThread.isAlive())
				try {Thread.sleep(1000);} catch (Exception e) {e.printStackTrace();}
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
		/*MixedStrategy[] strats = new MixedStrategy[2];
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
		}*/
		return SolverUtils.expectedPayoffs(p1.getStrategy(gameNumber,1),p2.getStrategy(gameNumber,2),games.get(gameNumber));
	}
	
	/**
	 * A single individual match between to players, the first player is row the second is column
	 * 
	 * If a strategy for a player is invalid it will assign a payoff of -1337 to that player
	 * @param p1 row player
	 * @param p2 column player
	 * @param gameNumber game
	 * @param game game if different from global
	 * @return the payoffs of the match
	 */
	public static double[] match(Player p1, Player p2, int gameNumber, MatrixGame game){
		return SolverUtils.expectedPayoffs(p1.getStrategy(gameNumber,1),p2.getStrategy(gameNumber,2),game);
	}
	
	/**
	 * Run match using strategies instead of players
	 * @param s1 player 1 strategy
	 * @param s2 player 2 strategy
	 * @param mg game to play
	 * @return expected payoffs
	 */
	public static double[] match(MixedStrategy s1, MixedStrategy s2, MatrixGame mg){
		return SolverUtils.expectedPayoffs(s1,s2,mg);
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
	public static int tester(Player p1, Player p2){
		int matching = 0;
		double d;
		for(int gameNumber = 0; gameNumber < numGames; gameNumber++){
			d = distance(p1.getStrategy(gameNumber,1),p2.getStrategy(gameNumber,1));
			if(Math.abs(d) < 0.000001)
				matching++;
			d = distance(p1.getStrategy(gameNumber,2),p2.getStrategy(gameNumber,2));
			if(Math.abs(d) < 0.000001)
				matching++;
		}
		return matching;
	}
	public static double[] findMinimums(double[][] matrix){
		double[] mins = new double[matrix.length];
		//Arrays.fill(stabilities, Double.NEGATIVE_INFINITY);
		for (int i = 0; i < matrix.length; i++) {
			mins[i] = matrix[i][0];
			for (int j = 1; j < matrix.length; j++) {
				if (mins[i] > matrix[i][j])
					mins[i] = matrix[i][j];
			}
		}
		return mins;
	}
	public static double distance(MixedStrategy s1, MixedStrategy s2){
		double sum = 0.0;
		for(int i = 1; i <= numActions; i++){
			//System.out.println(s1.getProb(i)+" "+ s2.getProb(i));
			sum += Math.pow(s1.getProb(i) - s2.getProb(i),2);
		}
		return Math.sqrt(sum);
	}
}
