package games;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import games.*;
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
		Random r = new Random();
		ArrayList<MatrixGame> games = new ArrayList<MatrixGame>();
		int[] actions = {10,10};
		int[] outcome = {0,0};
		for(int i = 0; i < numGames; i++){
			r.setSeed(i);
			MatrixGame g = new MatrixGame(2,actions);
			g.setDescription("A"+i);
			for(int row = 1; row <= actions[0]; row++){
				double base = 50 - row / 2;
				//double variance = r.nextGaussian()*(row+1);
				for(int col = 1; col <= actions[1]; col++){
					outcome[0] = row;
					outcome[1] = col;
					//System.out.println(Arrays.toString(outcome));
					g.setPayoff(outcome,0, base + r.nextGaussian()*(row+1));
				}
			}
			for(int col = 1; col <= actions[1]; col++){
				double base = 50 - col / 2;
				//double variance = r.nextGaussian()*(row+1);
				for(int row = 1; row <= actions[0]; row++){
					outcome[0] = row;
					outcome[1] = col;
					//System.out.println(Arrays.toString(outcome));
					g.setPayoff(outcome,1, base + r.nextGaussian()*(col+1));
				}
			}
			games.add(g);
		}	
		return games;
	}
	
	/**
	 * Generates general sum games
	 * @param numGames the number of games to generate.
	 * @return the games
	 */
	public static ArrayList<MatrixGame> classG(int numGames) {
		Random r = new Random();
		ArrayList<MatrixGame> games = new ArrayList<MatrixGame>();
		int[] actions = {10,10};
		int[] outcome = {0,0};
		double[] values = {0.0,0.0};
		for(int i = 0; i < numGames; i++){
			r.setSeed(i);
			MatrixGame g = new MatrixGame(2,actions);
			g.setDescription("G"+i);
			for(int row = 1; row <= actions[0]; row++){
				for(int col = 1; col <= actions[1]; col++){
					outcome[0] = row;
					outcome[1] = col;
					values[0] = r.nextDouble()*200 - 100;
					values[1] = r.nextDouble()*200 - 100;
					g.setPayoffs(outcome,values);
				}
			}
			games.add(g);
		}
		return games;
	}
	/**
	 * Generates zero sum games
	 * @param numGames the number of games to generate.
	 * @return the games
	 */
	public static ArrayList<MatrixGame> classZ(int numGames) {
		Random r = new Random();
		ArrayList<MatrixGame> games = new ArrayList<MatrixGame>();
		int[] actions = {10,10};
		int[] outcome = {0,0};
		double[] values = {0.0,0.0};
		for(int i = 0; i < numGames; i++){
			r.setSeed(i);
			MatrixGame g = new MatrixGame(2,actions);
			g.setDescription("G"+i);
			for(int row = 1; row <= actions[0]; row++){
				for(int col = 1; col <= actions[1]; col++){
					outcome[0] = row;
					outcome[1] = col;
					values[0] = r.nextDouble()*200 - 100;
					values[1] = values[0] * -1;
					//System.out.println(Arrays.toString(outcome));
					g.setPayoffs(outcome,values);
					//g.setPayoffs(
				}
			}
			games.add(g);
		}
		return games;
	}
}
