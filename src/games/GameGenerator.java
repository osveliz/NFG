package games;

import java.io.File;
import java.util.*;
import games.*;
import util.*;
/**
 * Uses Gamut.jar to generate games. Make sure Gamut.jar is saved where you found it do not move it.
 * @author Oscar
 */

public class GameGenerator {
	/**
	 * Generates zero sum games using Gamut.
	 * By default games have 20 actions for both players with payoffs ranging from -100 to 100
	 * @param numberOfGames the number of games
	 */
	public static void zeroSum(int numberOfGames) {
		System.out.println("Generating Zero Sum Games");
		for(int g = 0; g < numberOfGames; g++){
			String gamePath = "ZeroSum-"+g+".game";
			List<String> cmd = new ArrayList<String>();
			cmd.add("java");
			cmd.add("-jar");
			cmd.add("gamut.jar");
			cmd.add("-g");
			cmd.add("RandomZeroSum");
			cmd.add("-f");
			cmd.add(gamePath);
			cmd.add("-actions");
			for (int pl = 0; pl < 2; pl++)
				cmd.add("20");
			cmd.add("-random_seed");
			cmd.add("" + g);	
			int tries = 0;
			try {
				ProcessBuilder pb = new ProcessBuilder(cmd);
				pb.directory(new File("."));
				Process process = pb.start();
				while (true) {
					try {
						Thread.sleep(1000);
						process.exitValue();
						break;
					} catch (IllegalThreadStateException we) {
						if (tries++ <= 100) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException ex) {}
						} else {
							process.destroy();
							break;
						}
					}
				}
				process.getOutputStream().close();
				process.getErrorStream().close();
				process.destroy();
			} catch (Exception e) {}
		}
	}
	/**
	 * Generates general sum games
	 * @param numberOfGames the number of games to generate.
	 */
	public static void generalSum(int numberOfGames) {
		System.out.println("Generating General Sum Games");
		for(int g = 0; g < numberOfGames; g++){
			String gamePath = "GeneralSum-"+g+".game";
			List<String> cmd = new ArrayList<String>();
			cmd.add("java");
			cmd.add("-jar");
			cmd.add("gamut.jar");
			cmd.add("-g");
			cmd.add("RandomGame");
			cmd.add("-normalize");
			cmd.add("-min_payoff");
			cmd.add("-100");
			cmd.add("-max_payoff");
			cmd.add("100");
			cmd.add("-f");
			cmd.add(gamePath);
			cmd.add("-players");
			cmd.add("2");
			cmd.add("-actions");
			for (int pl = 0; pl < 2; pl++)
				cmd.add("20");
			cmd.add("-random_seed");
			cmd.add("" + g);
	
			int tries = 0;
			try {
				ProcessBuilder pb = new ProcessBuilder(cmd);
				pb.directory(new File("."));
				Process process = pb.start();
				while (true) {
					try {
						Thread.sleep(1000);
						process.exitValue();
						break;
					} catch (IllegalThreadStateException we) {
						if (tries++ <= 10) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException ex) {}
						} else {
							process.destroy();
							break;
						}
					}
				}
				process.getOutputStream().close();
				process.getErrorStream().close();
				process.destroy();
			} catch (Exception e) {}
		}
	}
	/**
	 * Generates Class A Games
	 * @param numGames the number of games to generate.
	 * @return the games
	 */
	public static ArrayList<MatrixGame> classA(int numGames) {
		return classA(numGames, new Parameters());
	}

	public static MatrixGame prisonners(){
		int[] actions = {2,2};
		MatrixGame mg = new MatrixGame(2, actions);
		int[] outcome = {1,1};
		double[] payoffs = {-1,-1};
		mg.setPayoffs(outcome, payoffs);
		outcome[1] = 2;
		payoffs[0] = -4;
		payoffs[1] = 0;
		mg.setPayoffs(outcome, payoffs);
		outcome[0] = 2;
		payoffs[0] = -3;
		payoffs[1] = -3;
		mg.setPayoffs(outcome, payoffs);
		outcome[1] = 1;
		payoffs[0] = 0;
		payoffs[1] = -4;
		mg.setPayoffs(outcome, payoffs);

		return mg;

	}
	
	/**
	 * Generates Class A Games
	 * @param numGames the number of games to generate.
	 * @param p Settings
	 * @return the games
	 */
	public static ArrayList<MatrixGame> classA(int numGames, Parameters p) {
		Random r = new Random();
		ArrayList<MatrixGame> games = new ArrayList<MatrixGame>();
		int[] actions = {10,10};
		int[] outcome = {0,0};
		for(int i = 0; i < numGames; i++){
			r.setSeed(i);
			MatrixGame g = new MatrixGame(2,actions);
			g.setDescription(""+i);
			for(int row = 1; row <= actions[0]; row++){
				double base = 50 - row / 2;
				for(int col = 1; col <= actions[1]; col++){
					outcome[0] = row;
					outcome[1] = col;
					g.setPayoff(outcome,0, base + r.nextGaussian()*(row+1));
				}
			}
			for(int col = 1; col <= actions[1]; col++){
				double base = 50 - col / 2;
				for(int row = 1; row <= actions[0]; row++){
					outcome[0] = row;
					outcome[1] = col;
					g.setPayoff(outcome,1, base + r.nextGaussian()*(col+1));
				}
			}
			games.add(g);
		}	
		return games;
	}
	
	/**
	 * Generates general sum games with default parameters
	 * @param numGames the number of games to generate.
	 * @return the games
	 */
	public static ArrayList<MatrixGame> classG(int numGames) {
		return classG(numGames, new Parameters());
	}
	
	/**
	 * Generates general sum games
	 * @param numGames the number of games to generate.
	 * @param p set of parameters
	 * @return the games
	 */
	public static ArrayList<MatrixGame> classG(int numGames, Parameters p) {
		Random r = new Random();
		ArrayList<MatrixGame> games = new ArrayList<MatrixGame>();
		int[] actions = {p.getNumActions(),p.getNumActions()};
		int[] outcome = {0,0};
		double[] values = {0.0,0.0};
		int max = p.getMaxPayoff();
		for(int i = 0; i < numGames; i++){
			r.setSeed(i);
			MatrixGame g = new MatrixGame(2,actions);
			g.setDescription(""+i);
			for(int row = 1; row <= actions[0]; row++){
				for(int col = 1; col <= actions[1]; col++){
					outcome[0] = row;
					outcome[1] = col;
					values[0] = r.nextDouble()*max;
					values[1] = r.nextDouble()*max;
					g.setPayoffs(outcome,values);
				}
			}
			games.add(g);
		}
		return games;
	}
	
	/**
	 * Generates general sum games with default parameters
	 * @param numGames the number of games to generate.
	 * @return the games
	 */
	public static ArrayList<MatrixGame> classZ(int numGames) {
		return classZ(numGames, new Parameters());
	}
	
	/**
	 * Generates zero sum games
	 * @param numGames the number of games to generate.
	 * @param p set of parameters
	 * @return the games
	 */
	public static ArrayList<MatrixGame> classZ(int numGames, Parameters p) {
		Random r = new Random();
		ArrayList<MatrixGame> games = new ArrayList<MatrixGame>();
		int[] actions = {p.getNumActions(),p.getNumActions()};
		int[] outcome = {0,0};
		double[] values = {0.0,0.0};
		int max = p.getMaxPayoff();
		for(int i = 0; i < numGames; i++){
			r.setSeed(i);
			MatrixGame g = new MatrixGame(2,actions);
			g.setDescription(""+i);
			for(int row = 1; row <= actions[0]; row++){
				for(int col = 1; col <= actions[1]; col++){
					outcome[0] = row;
					outcome[1] = col;
					values[0] = r.nextDouble()*max*2.0 - max;
					values[1] = values[0] * -1;
					g.setPayoffs(outcome,values);
				}
			}
			games.add(g);
		}
		return games;
	}
	/**
	 * Add uncertainty to a single game
	 * @param mg the game to change
	 * @param p settings
	 */
	public static void obfuscate(MatrixGame mg, Parameters p){
		Set<Integer> s = new HashSet<Integer>();
		Random r = new Random(Integer.parseInt(mg.getDescription()));
		int act = p.getNumActions();
		if(p.getOutcomeUncertainty() >= act*act)//change all actions
			for(int i = 0; i < act*act; i++)
				s.add(i);
		else{
			while(s.size() < p.getOutcomeUncertainty()){
				s.add(r.nextInt(act*act));
			}
		}
		int[] outcome = {0,0};
		int globe = 0;
		double[] temp = {0.0, 0.0};
		Iterator<Integer> itr = s.iterator();
		while(itr.hasNext()){
			globe = itr.next();
			//System.out.println("globe "+ globe);//debug
			outcome[0] = globe/act+1;
			outcome[1] = globe%act+1;
			//System.out.println(Arrays.toString(outcome));//debug
			temp = mg.getPayoffs(outcome);
			temp[0] -= r.nextDouble()*p.getPayoffUncertainty();
			temp[1] -= r.nextDouble()*p.getPayoffUncertainty();
			mg.setPayoffs(outcome,temp);
		}
	}
	/**
	 * Add uncertainty to a all games
	 * @param games the games to change
	 * @param p settings
	 */
	public static void obfuscate(ArrayList<MatrixGame> games, Parameters p){
		for(int i = 0; i < games.size(); i++)
			obfuscate(games.get(i),p);
	}
	
	/**
	 * Generates zero sum games
	 * @param numGames the number of games to generate.
	 * @param p set of parameters
	 * @return the games
	 */
	public static ArrayList<MatrixGame> generate(int numGames, Parameters p) {
		Random r = new Random();
		GameType type = p.getGameType();
		ArrayList<MatrixGame> games = new ArrayList<MatrixGame>();
		int[] actions = {p.getNumActions(),p.getNumActions()};
		int[] outcome = {0,0};
		double[] values = {0.0,0.0};
		int max = p.getMaxPayoff();
		for(int i = 0; i < numGames; i++){
			r.setSeed(i);
			MatrixGame g = new MatrixGame(2,actions);
			g.setDescription(""+i);
			if(type == GameType.RISK){
				for(int row = 1; row <= actions[0]; row++){
					double base = 50 - (row-1) * 2;
					for(int col = 1; col <= actions[1]; col++){
						outcome[0] = row;
						outcome[1] = col;
						g.setPayoff(outcome,0, base + r.nextGaussian()*(row*2));
						outcome[0] = col;
						outcome[1] = row;
						g.setPayoff(outcome,1, base + r.nextGaussian()*(row*2));
					}
				}
				/*for(int col = 1; col <= actions[1]; col++){
					double base = 50 - col / 2;
					for(int row = 1; row <= actions[0]; row++){
						outcome[0] = row;
						outcome[1] = col;
						g.setPayoff(outcome,1, base + r.nextGaussian()*(col+1));
					}
				}*/
			}
			else{
				for(int row = 1; row <= actions[0]; row++){
					for(int col = 1; col <= actions[1]; col++){
						outcome[0] = row;
						outcome[1] = col;
						if(type == GameType.ZERO_SUM){
							values[0] = r.nextDouble()*max*2.0 - max;
							values[1] = values[0] * -1;
						}
						else{
							values[0] = r.nextDouble()*max;
							values[1] = r.nextDouble()*max;
						}
						g.setPayoffs(outcome,values);	
					}
				}
			}
			games.add(g);
		}
		return games;
	}
}
