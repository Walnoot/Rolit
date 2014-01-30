package team144.rolit;

/**
 * not even random :O
 */
public class LinearStrategy implements Strategy {
	
	@Override
	public String getName() {
		return "linear";
	}
	
	@Override
	public int findMove(Game game, String playerName) {
		for (int i = 0; i < Board.DIMENSION * Board.DIMENSION; i++) {
			if (game.isValidMove(i)) { return i; }
		}
		return -1;
	}
}
