package team144.rolit;

/**
 * With bitmasks and everything.
 */
public class RecursiveStrategy implements Strategy {
    private static final int NUM_ITERATIONS = 6;

    @Override
    public String getName() {
        return "recursive";
    }
    
    @Override
    public int findMove(Game game, String playerName) {
        return getMove(getBestMoveQuality(game, game.findPlayer(playerName), NUM_ITERATIONS));
    }
    
    public static void main(String[] args) {
        Game game = new Game(new Player(Tile.RED, "1"), new Player(Tile.GREEN, "2"));
        
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
        setMove(game);
    }
    
    private static void setMove(Game game) {
        Player player = game.getCurrentPlayer();
        
        int move = getMove(getBestMoveQuality(game, player, NUM_ITERATIONS));
        if (!game.isValidMove(move)) System.out.println("oeps");
        System.out.println(move);
        game.makeMove(player.getName(), move);
    }
    
    private static int getBestMoveQuality(Game game, Player player, int iterations) {
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
        
        if(bestMove == -1) bestMove = legalMove;
        return combine(bestMove, bestQuality);
    }
    
    private static int getQuality(Game game, Player player) {
//        if(game.isGameOver()) return getNumTiles(game, player) * 2;
        
        int quality = 0;
        int borderWeight = 5;//how much the algorithm likes border and corners
        
        for (int i = 0; i < Board.DIMENSION * Board.DIMENSION; i++) {
            if (game.getBoard().getTile(i) == player.getTile()) {
                quality++;
                
                int x = game.getBoard().getX(i);
                int y = game.getBoard().getY(i);
                
                //moves near the sides and corners are better in every way.
//                if (x == 0 || x == Board.DIMENSION - 1) quality += borderWeight;
//                if (y == 0 || y == Board.DIMENSION - 1) quality += borderWeight;
//                if (x == 1 || x == Board.DIMENSION - 2) quality -= borderWeight;
//                if (y == 1 || y == Board.DIMENSION - 2) quality -= borderWeight;
                
                quality += lt[x][y];
            }
        }
        
        return quality;
    }
    
    private static final int[][] lt = new int[][]{
        {10000, -3000,  1000,   800,    800,    1000,   -3000,  10000},
        {-3000, -5000,  -450,   -500,   -500,   -450,   -5000,  -3000},
        {1000,  -450,   30,     10,     10,     30,     -450,   1000},
        {800,   -500,   10,     50,     50,     10,     -500,   800},
        {800,   -500,   10,     50,     50,     10,     -500,   800},
        {1000,  -450,   30,     10,     10,     30,     -450,   1000},
        {-3000, -5000,  -450,   -500,   -500,   -450,   -5000,  -3000},
        {10000, -3000,  1000,   800,    800,    1000,   -3000,  10000},
    };
    
    private static int combine(int move, int quality) {
        return move | Math.min(0, quality) << 16;
    }
    
    private static int getQuality(int combined) {
        return combined & 0xFFFF0000;
    }
    
    private static int getMove(int combined) {
        return combined & 0x0000FFFF;
    }
    
    private static int getNumTiles(Game game, Player player) {
        int numTiles = 0;
        
        for (int i = 0; i < Board.DIMENSION * Board.DIMENSION; i++) {
            if (game.getBoard().getTile(i) == player.getTile()) numTiles++;
        }
        
        return numTiles;
    }
}
