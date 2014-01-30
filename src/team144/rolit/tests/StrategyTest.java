package team144.rolit.tests;

import team144.rolit.Game;
import team144.rolit.Player;
import team144.rolit.RecursiveStrategy;
import team144.rolit.Tile;

public class StrategyTest {
	public static void main(String[] args) {
		Game game = new Game(new Player(Tile.RED, "1"), new Player(Tile.GREEN, "2"));
		
		for(int i = 0; i < 20; i++){
			setMove(game);
		}
	}
	
	private static void setMove(Game game) {
		Player player = game.getCurrentPlayer();
		
		int move = RecursiveStrategy.getMove(RecursiveStrategy.getBestMoveQuality(game, player, 4));
		if (!game.isValidMove(move)) System.out.println("oeps");
		System.out.println(move);
		game.makeMove(player.getName(), move);
	}
}
