package team144.rolit;

import java.util.Observable;

public class Game extends Observable {
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 4;
    
    private final Board board = new Board();
    
    private final Player[] players;
    private int currentPlayerIndex = 0;
    
    /**
     * @param players
     *            - An array of Players with length between MIN_PLAYERS and
     *            MAX_PLAYERS. The order is the play order.
     */
    /*@
     * requires players.length >= MIN_PLAYERS & players.length <= MAX_PLAYERS;
     */
    public Game(Player... players) {
        if (players.length < MIN_PLAYERS || players.length > MAX_PLAYERS)
            throw new IllegalArgumentException("Too few or much players: " + players.length);
        
        for (int i = 0; i < players.length; i++) {
            board.setTile(Board.DIMENSION / 2 + i % 2 - 1, Board.DIMENSION / 2 + i / 2 - 1, players[i].getTile());
        }
        
        this.players = players;
    }
    
    public Board getBoard() {
        return board;
    }
    
    public void makeMove(Player player, int index) {
        if (players[currentPlayerIndex] == player) {
            int x = board.getX(index);
            int y = board.getY(index);
            
//            testDirection(x, y, 0, 1, player.getTile());
//            testDirection(x, y, 1, 1, player.getTile());
//            testDirection(x, y, 1, 0, player.getTile());
//            testDirection(x, y, 1, -1, player.getTile());
//            testDirection(x, y, 0, -1, player.getTile());
//            testDirection(x, y, -1, -1, player.getTile());
//            testDirection(x, y, -1, 0, player.getTile());
//            testDirection(x, y, -1, 1, player.getTile());
            
            for (Direction dir : Direction.values()) {
                testDirection(x, y, dir, player.getTile());
            }
            
            board.setTile(index, player.getTile());
            
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
            
            setChanged();
            notifyObservers();
        }
    }
    
    private void testDirection(int x, int y, Direction dir, Tile tile) {
        int steps = getSteps(x, y, dir, tile);
        
        if (steps != -1) {
            for (int i = 1; i < steps; i++) {
                board.setTile(x + i * dir.xOffset, y + i * dir.yOffset, tile);
            }
        }
    }
    
    /**
     * @param x
     * @param y
     * @param dir
     * @param tile
     * @return - The number of steps the first equal tile is when walking from
     *         the coordinates in the specified direction.
     */
    private int getSteps(int x, int y, Direction dir, Tile tile) {
        int steps = -1;
        
        for (int i = 1; i < Board.DIMENSION; i++) {
            int testX = x + i * dir.xOffset;
            int testY = y + i * dir.yOffset;
            
            if (testX < 0 || testY < 0 || testX >= Board.DIMENSION || testY >= Board.DIMENSION) {
                break;
            } else {
                if (board.getTile(testX, testY) == tile) {
                    steps = i;
                    break;
                }
            }
        }
        
        return steps;
    }
    
    /**
     * Test if the current player can make the move at the specified index.
     * 
     * @param index
     * @return - True iff the move is valid.
     */
    public boolean isValidMove(int index) {
        if (board.getTile(index) != Tile.EMPTY) return false;
        
        int x = board.getX(index);
        int y = board.getY(index);
        
        boolean hasNeighbour = false;
        for(int i = 0; i < Direction.values().length && !hasNeighbour; i++){
            Tile tile = board.getTile(x + Direction.values()[i].xOffset, y + Direction.values()[i].yOffset);
            if(!(tile == null || tile == Tile.EMPTY)) hasNeighbour = true;
        }
        
        if(!hasNeighbour) return false;
    }
    
    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }
    
    private enum Direction {
        NORTH(0, 1), NORTH_EAST(1, 1), EAST(1, 0), SOUTH_EAST(1, -1), SOUTH(0, -1), SOUTH_WEST(-1, -1), WEST(-1, 0),
        NORTH_WEST(-1, 1);
        
        private int xOffset, yOffset;
        
        private Direction(int xOffset, int yOffset) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }
}
