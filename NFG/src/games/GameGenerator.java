package games;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
}
