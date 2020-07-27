package tournament;

public class QuantalLevelK extends CognitiveHierarchy{
	protected final String newName = "QLK"; //Overwrite this variable in your player subclass
	
	public QuantalLevelK() {
		super();
        playerName = newName;        
	}
	
	/**
	 * Sets the default values for variables.
	 */
	public void initialize(){
		level = 12;
		tau = 1.5;
		lambda = 10.0;
		quantal = true;
	}

}

