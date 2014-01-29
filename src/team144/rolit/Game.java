package team144.rolit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;

public class Game extends Observable {
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 4;
    
    private final Board board;
    
    private final Player[] players;
    private int currentPlayerIndex = 0;
    private boolean gameOver = false;
    /**
     * 1D array representing the legal moves on the board currently.
     */
    private boolean[] legalMoves = new boolean[Board.DIMENSION
        * Board.DIMENSION];
    
    /**
     * @param players
     *            - An array of Players with length between MIN_PLAYERS and
     *            MAX_PLAYERS. The order is the play order.
     */
    /*@
     * requires players.length >= MIN_PLAYERS & players.length <= MAX_PLAYERS;
     */
    public Game(Player... players) {
        board = new Board();
        
        if (players.length < MIN_PLAYERS || players.length > MAX_PLAYERS)
            throw new IllegalArgumentException("Too few or much players: "
                + players.length);
        
        for (int i = 0; i < players.length; i++) {
            players[i].setGame(this);
            players[i].index = i + 1;
        }
        
        board.setTile(Board.DIMENSION / 2 - 1, Board.DIMENSION / 2 - 1,
                Tile.RED);
        board.setTile(Board.DIMENSION / 2, Board.DIMENSION / 2 - 1, Tile.YELLOW);
        board.setTile(Board.DIMENSION / 2 - 1, Board.DIMENSION / 2, Tile.BLUE);
        board.setTile(Board.DIMENSION / 2, Board.DIMENSION / 2, Tile.GREEN);
        
        this.players = players;
        
        calculateLegalMoves();
    }
    
    /**
     * @param game
     *            - The game to be copied.
     */
    public Game(Game game) {
        board = new Board(game.board);
        this.players = game.players;
        this.currentPlayerIndex = game.currentPlayerIndex;
        this.gameOver = game.gameOver;
        this.legalMoves = Arrays.copyOf(legalMoves, legalMoves.length);
    }
    
    public Board getBoard() {
        return board;
    }
    
    public void makeMove(int playerIndex, int x, int y) {
        makeMove(findPlayer(playerIndex), board.getIndex(x, y), playerIndex);
    }
    
    public void makeMove(String playerName, int x, int y) {
        makeMove(playerName, board.getIndex(x, y));
    }
    
    public void makeMove(String playerName, int index) {
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            
            if (player.getName().equals(playerName)) {
                makeMove(player, index, i);
                
                return;
            }
        }
        
        System.out.println("Player " + playerName + "not found, wtf is this?"
            + Arrays.toString(players));
    }
    
    public void makeMove(Player player, int boardIndex, int playerIndex) {
        int x = board.getX(boardIndex);
        int y = board.getY(boardIndex);
        
        for (Direction dir : Direction.values()) {
            testDirection(x, y, dir, player.getTile());
        }
        
        board.setTile(boardIndex, player.getTile());
        
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        
        boolean boardFull = true;
        for (int j = 0; j < Board.DIMENSION * Board.DIMENSION; j++) {
            if (board.getTile(j) == Tile.EMPTY) boardFull = false;
        }
        
        if (boardFull) gameOver = true;
        
        calculateLegalMoves();
        
        setChanged();
        notifyObservers();
    }
    
    public Player findPlayer(String playerName) {
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            if (player.getName().equals(playerName)) return player;
        }
        
        return null;
    }
    
    public Player findPlayer(int index) {
        for (int i = 0; i < players.length; i++) {
            if (players[i].index == index) return players[i];
        }
        
        return null;
    }
    
    private void calculateLegalMoves() {
        for (int i = 0; i < legalMoves.length; i++) {
            int x = board.getX(i);
            int y = board.getY(i);
            
            if (board.getTile(x, y) != Tile.EMPTY) {
                legalMoves[i] = false;
            } else {
                boolean hasOtherColorNeighbour = false;//not that there's anything wrong with that of course
                boolean canMove = false;//whether this moves takes over any tiles
                for (Direction dir : Direction.values()) {
                    Tile tile = board.getTile(x + dir.xOffset, y + dir.yOffset);
                    if (tile != null && tile != Tile.EMPTY
                        && tile != getCurrentPlayer().getTile()) {
                        hasOtherColorNeighbour = true;
                        
                        if (getSteps(x, y, dir, getCurrentPlayer().getTile()) > 1)
                            canMove = true;
                    }
                }
                
                legalMoves[i] = hasOtherColorNeighbour && canMove;
            }
        }
        
        //check if the player can move at all
        boolean hasMove = false;
        for (int i = 0; i < legalMoves.length; i++) {
            if (legalMoves[i]) {
                hasMove = true;
                break;
            }
        }
        
        if (!hasMove) {//no move possible, so any neighbouring tile is valid
            for (int i = 0; i < legalMoves.length; i++) {
                if (board.getTile(i) == Tile.EMPTY) {
                    int x = board.getX(i);
                    int y = board.getY(i);
                    
                    boolean hasNeighbour = false;//not that there's anything wrong with that of course
                    for (Direction dir : Direction.values()) {
                        Tile tile =
                            board.getTile(x + dir.xOffset, y + dir.yOffset);
                        if (tile != null && tile != Tile.EMPTY) {
                            hasNeighbour = true;
                        }
                    }
                    
                    legalMoves[i] = hasNeighbour;
                } else {
                    legalMoves[i] = false;
                }
            }
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
            
            if (testX < 0 || testY < 0 || testX >= Board.DIMENSION
                || testY >= Board.DIMENSION) {
                break;
            } else {
                Tile testTile = board.getTile(testX, testY);
                if (testTile == Tile.EMPTY) return -1;
                if (testTile == tile) {
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
        if (index < 0 || index >= Board.DIMENSION * Board.DIMENSION) return false;
        else return legalMoves[index];
    }
    
    public int getNumPlayers() {
        return players.length;
    }
    
    public boolean isGameOver() {
        return gameOver;
    }
    
    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }
    
    public void setCurrentPlayer(int playerIndex) {
        currentPlayerIndex = playerIndex - 1;
        calculateLegalMoves();
        
        setChanged();
        notifyObservers();
    }
    
    private enum Direction {
        NORTH(0, 1), NORTH_EAST(1, 1), EAST(1, 0), SOUTH_EAST(1, -1), SOUTH(0,
                -1), SOUTH_WEST(-1, -1), WEST(-1, 0), NORTH_WEST(-1, 1);
        
        private int xOffset, yOffset;
        
        private Direction(int xOffset, int yOffset) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }
    
    public ArrayList<Integer> getLegalMoves() {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < legalMoves.length; i++) {
            if (legalMoves[i]) {
                result.add(i);
            }
        }
        return result;
    }
    
}
