package team144.rolit;

/**
 * With bitmasks and everything.
 */
public class RecursiveStrategy implements Strategy {
	private static final int NUM_ITERATIONS = 5;
	
	@Override
	public String getName() {
		return "recursive";
	}
	
	@Override
	public int findMove(Game game, String playerName) {
		return getMove(getBestMoveQuality(game, game.findPlayer(playerName), NUM_ITERATIONS));
	}
	
	public static int getBestMoveQuality(Game game, Player player, int iterations) {
		if (iterations == 0) return -1;
		
		int bestMove = -1;
		int bestQuality = 0;
		
		int legalMove = 0;//any move that is valid
		
		int startQuality = getQuality(game, player);
		
		for (int i = 0; i < Board.DIMENSION * Board.DIMENSION; i++) {
			if (game.isValidMove(i)) {
				legalMove = i;
				
				Game newGame = new Game(game);
				
				newGame.makeMove(player.getName(), i);
				
				int quality = (getQuality(newGame, player) - startQuality) * newGame.getNumPlayers();
				
				for (int j = 0; j < newGame.getNumPlayers() - 1; j++) {
					int enemyMove = getBestMoveQuality(newGame, newGame.getCurrentPlayer(), iterations - 1);
					
					if (enemyMove != -1) quality -= getQuality(enemyMove);
				}
				
				if (quality >= bestQuality) {
					bestQuality = quality;
					bestMove = i;
				}
			}
		}
		
		if (bestMove == -1) bestMove = legalMove;
		return combine(bestMove, bestQuality);
	}
	
	private static int getQuality(Game game, Player player) {
		int quality = 0;
		
		for (int i = 0; i < Board.DIMENSION * Board.DIMENSION; i++) {
			if (game.getBoard().getTile(i) == player.getTile()) {
				quality++;
				
				int x = Board.getX(i);
				int y = Board.getY(i);
				
				quality += qualityTable[x][y];
			}
		}
		
		return quality;
	}
	
	/**
	 * A table determining the quality of a tile on the board
	 */
	private static final int[][] qualityTable = new int[][] { { 10000, -3000, 1000, 800, 800, 1000, -3000, 10000 },
		{ -3000, -5000, -450, -500, -500, -450, -5000, -3000 }, { 1000, -450, 30, 10, 10, 30, -450, 1000 },
		{ 800, -500, 10, 50, 50, 10, -500, 800 }, { 800, -500, 10, 50, 50, 10, -500, 800 },
		{ 1000, -450, 30, 10, 10, 30, -450, 1000 }, { -3000, -5000, -450, -500, -500, -450, -5000, -3000 },
		{ 10000, -3000, 1000, 800, 800, 1000, -3000, 10000 }, };
	
	/**
	 * The move and quality of said move are packed into one int for performance reasons. (Java can only return 1 thing, and allocating a
	 * new object would be costly).
	 * 
	 * @param move
	 * @param quality
	 * @return
	 */
	private static int combine(int move, int quality) {
		return move | Math.min(0, quality) << 16;
	}
	
	private static int getQuality(int combined) {
		return (combined & 0xFFFF0000) >> 16;
	}
	
	public static int getMove(int combined) {
		return combined & 0x0000FFFF;
	}
}
