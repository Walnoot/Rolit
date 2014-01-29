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
        int maxTakeOvers = 0;
        int bestMoveIndex = 0;
        
        //getLegalMoves() things
        for (int i = 0; i < Board.DIMENSION * Board.DIMENSION; i++) {
            if (game.isValidMove(i)) {
                Game newGame = new Game(game);
                Player player = newGame.findPlayer(playerName);
                
                int oldTiles = getNumTiles(newGame, player);
                
                newGame.makeMove(playerName, i);
                
                int takeOverTiles = oldTiles - getNumTiles(newGame, player);
                if(takeOverTiles > maxTakeOvers){
                    maxTakeOvers = takeOverTiles;
                    bestMoveIndex = i;
                }
            }
        }
        
        return bestMoveIndex;
    }
    
    private int getNumTiles(Game game, Player player){
        int numTiles = 0;
        
        for(int i = 0; i < Board.DIMENSION * Board.DIMENSION; i++){
            if(game.getBoard().getTile(i) == player.getTile()) numTiles++;
        }
        
        return numTiles;
    }
}
