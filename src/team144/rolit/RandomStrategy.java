package team144.rolit;

public class RandomStrategy implements Strategy{

	@Override
	public String getName() {
		return "random";
	}

	@Override
	public HumanPlayer getPlayer() {
		return null;
	}

	@Override
	public void determineMove() {
	    
	}
	
}
