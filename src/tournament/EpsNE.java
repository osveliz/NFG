package tournament;

import games.*;
import java.util.*;

/**
 * Sample agent that always picks Rock (the first action). Remember to put your name in the author field,
 * change the name of the agent, and make your own solveGame(MatrixGame,PlayerNumber) method.
 * @author Oscar
 * @version 2015.10.04
 */
public class EpsNE extends Player{
	protected final String newName = "EpsNE"; //Overwrite this variable in your player subclass

	/**Your constructor should look just like this*/
	public EpsNE() {
		super();
        playerName = newName;
	}
	
	/**
	 * Initialize is called at beginning of tournament.
	 * Use this time to decide on a strategy depending
	 * on the parameters.
	 */
	public void initialize(){
		//System.out.println("Solid Rock "+getParameters().getDescription());
	}
	
	/**
     * THIS METHOD SHOULD BE OVERRIDDEN 
     * GameMaster will call this to compute your strategy.
     * @param mg The game your agent will be playing
     * @param playerNumber Row Player = 0, Column Player = 1
     */
    protected MixedStrategy solveGame(MatrixGame mg, int playerNumber){
		//mg.printMatrix();
		ArrayList<int[]> psne = new ArrayList<int[]>();
		int actions = mg.getNumActions(playerNumber);
		MixedStrategy ms = new MixedStrategy(actions);
		boolean rowHighest[][] = new boolean[actions+1][actions+1];
		boolean colHighest[][] = new boolean[actions+1][actions+1];
		double payoffs[] = new double[2];
		double rowPay,colPay;
		double tempPayoff[] = new double[2];
		int outcome[] = new int[2];
		int tempOutcome[] = new int[2];
		int row,col;
		boolean highest = true;
		
		for(row = 1; row <= actions; row++){
			for(col = 1; col <= actions; col++){
				outcome[0] = row;
				outcome[1] = col;
				payoffs = mg.getPayoffs(outcome);
				rowPay = payoffs[0];
				colPay = payoffs[1];
				highest = true;
				tempOutcome[1] = col;
				for(int i = 1; i <= actions; i++){
					tempOutcome[0] = i;
					payoffs = mg.getPayoffs(tempOutcome);
					if(rowPay < payoffs[0])
						highest = false;
				}
				rowHighest[row][col] = highest;
				highest = true;
				tempOutcome[0] = row;
				for(int i = 1; i <= actions; i++){
					tempOutcome[1] = i;
					payoffs = mg.getPayoffs(tempOutcome);
					if(colPay < payoffs[1])
						highest = false;
				}
				colHighest[row][col] = highest;
			}
		}
		//add when both match
		/*System.out.println("Row highest");
		for(row = 1; row <= actions; row++){
			for(col = 1; col <= actions; col++){
				if(rowHighest[row][col])
					System.out.print("1 ");
				else
					System.out.print("0 ");
			}
			System.out.println();
		}
		System.out.println("Col highest");
		for(row = 1; row <= actions; row++){
			for(col = 1; col <= actions; col++){
				if(colHighest[row][col])
					System.out.print("1 ");
				else
					System.out.print("0 ");
			}
			System.out.println();
		}*/
		
		for(row = 1; row <= actions; row++){
			for(col = 1; col <= actions; col++){
				if(rowHighest[row][col] && colHighest[row][col]){
					int psneOutcome[] = new int[2];
					psneOutcome[0] = row;
					psneOutcome[1] = col;
					psne.add(psneOutcome);
				}
			}
		}
		if(!psne.isEmpty()){
			int psneOutcome[] = new int[2];
			outcome = psne.get(0);//when only one this is the one that is played
			double mybest = mg.getPayoffs(outcome)[playerNumber];
			for(int i = 1; i < psne.size(); i++){//when multiple pick one that has best for self
				double temp = mg.getPayoffs(psne.get(i))[playerNumber];
				if(mybest < temp){
					mybest = temp;
					outcome = psne.get(i);
				}
			}
			for(int i = 1; i <= actions; i++)
				ms.setProb(i, 0);
			ms.setProb(outcome[playerNumber],1.0);
			return ms;
		}
		else{
			double eps = .1;
			do{
				//copy highest values
				double rh[] = new double[actions+1];
				double ch[] = new double[actions+1];
				for(row = 1; row <= actions; row++){
					for(col = 1; col <= actions; col++){
						outcome[0] = row;
						outcome[1] = col;
						payoffs = mg.getPayoffs(outcome);
						rowPay = payoffs[0];
						colPay = payoffs[1];
						if(rowHighest[row][col])
							rh[col] = rowPay;
						if(colHighest[row][col])
							ch[row] = colPay;
					}
				}
				for(row = 1; row <= actions; row++){
					for(col = 1; col <= actions; col++){
						outcome[0] = row;
						outcome[1] = col;
						payoffs = mg.getPayoffs(outcome);
						rowPay = payoffs[0];
						colPay = payoffs[1];
						if(Math.abs(rh[col] - rowPay) < eps)
							rowHighest[row][col] = true;
						if(Math.abs(ch[row] - colPay) < eps)
							colHighest[row][col] = true;
					}
				}
				for(row = 1; row <= actions; row++){
					for(col = 1; col <= actions; col++){
						if(rowHighest[row][col] && colHighest[row][col]){
							int psneOutcome[] = new int[2];
							psneOutcome[0] = row;
							psneOutcome[1] = col;
							psne.add(psneOutcome);
						}
					}
				}
				eps = eps*2;
			}while(psne.isEmpty());
			/*System.out.println("Row highest eps");
			for(row = 1; row <= actions; row++){
				for(col = 1; col <= actions; col++){
					if(rowHighest[row][col])
						System.out.print("1 ");
					else
						System.out.print("0 ");
				}
				System.out.println();
			}
			System.out.println("Col highest eps");
			for(row = 1; row <= actions; row++){
				for(col = 1; col <= actions; col++){
					if(colHighest[row][col])
						System.out.print("1 ");
					else
						System.out.print("0 ");
				}
				System.out.println();
			}*/
			int psneOutcome[] = new int[2];
			outcome = psne.get(0);//when only one this is the one that is played
			double mybest = mg.getPayoffs(outcome)[playerNumber];
			for(int i = 1; i < psne.size(); i++){//when multiple pick one that has best for self
				double temp = mg.getPayoffs(psne.get(i))[playerNumber];
				if(mybest < temp){
					mybest = temp;
					outcome = psne.get(i);
				}
			}
			for(int i = 1; i <= actions; i++)
				ms.setProb(i, 0);
			ms.setProb(outcome[playerNumber],1.0);
			//return ms;
		}
    	return ms;
    }
    public double[] copy(double a[]){
		double b[] = new double[a.length];
		for(int i = 0; i < a.length; i++)
			b[i] = a[i];
		return b;
	}
	public int[] copy(int a[]){
		int b[] = new int[a.length];
		for(int i = 0; i < a.length; i++)
			b[i] = a[i];
		return b;
	}
}
