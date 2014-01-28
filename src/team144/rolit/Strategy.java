package team144.rolit;

public interface Strategy {
	
	public String getName();
	public HumanPlayer getPlayer();
	public void determineMove();
	

}
