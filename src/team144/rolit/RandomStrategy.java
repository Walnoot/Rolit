package team144.rolit;

import java.util.ArrayList;
import java.util.Random;

/**
 * random
 */

public class RandomStrategy implements Strategy {
	private static Random rand = new Random();
	
	@Override
	public String getName() {
		return "random";
	}
	
	@Override
	public int findMove(Game game, String playerName) {
		ArrayList<Integer> moves = game.getLegalMoves();
		return moves.get(rand.nextInt(moves.size()));
	}
	
}
