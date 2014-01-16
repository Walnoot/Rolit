package team144.rolit;

/**
 * Class representing the game board.
 */
public class Board {
    /**
     * The dimension of the board, so that width and height equals DIMENSION.
     */
    public static final int DIMENSION = 8;

    /**
     * 1D array representing the 2D game board.
     */
    private Ball[] field = new Ball[DIMENSION * DIMENSION];

    /**
     * Construct a new Board with an empty play field.
     */
    public Board() {
        for (int i = 0; i < field.length; i++) {
            field[i] = Ball.EMPTY;
        }
    }

    /**
     * Gets the ball state at the specified index.
     * @param i - The index.
     * @return - The ball state at the specified index.
     */
    public final Ball getBall(final int i) {
        return field[i];
    }

    /**
     * Gets the ball state at the specified coordinates.
     * @param x - The x coordinate.
     * @param y - The y coordinate.
     * @return - The ball state at the specified coordinates.
     */
    public final Ball getBall(final int x, final int y) {
        return field[x + y * DIMENSION];
    }
}
