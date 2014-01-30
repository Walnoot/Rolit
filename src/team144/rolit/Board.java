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
	private Tile[] field = new Tile[DIMENSION * DIMENSION];
	
	/**
	 * Construct a new Board with an empty play field.
	 */
	public Board() {
		for (int i = 0; i < field.length; i++) {
			field[i] = Tile.EMPTY;
		}
	}
	
	public Board(Board board) {
		for (int i = 0; i < field.length; i++) {
			field[i] = board.field[i];
		}
	}
	
	public void setTile(int index, Tile tile) {
		field[index] = tile;
	}
	
	public void setTile(int x, int y, Tile tile) {
		field[getIndex(x, y)] = tile;
	}
	
	/**
	 * Gets the tile state at the specified index.
	 * 
	 * @param i
	 *            - The index.
	 * @return - The tile state at the specified index, or null if the index is
	 *         out of bounds.
	 */
	public final Tile getTile(final int i) {
		if (i < 0 || i >= field.length) return null;
		return field[i];
	}
	
	/**
	 * Gets the tile state at the specified coordinates.
	 * 
	 * @param x
	 *            - The x coordinate.
	 * @param y
	 *            - The y coordinate.
	 * @return - The tile state at the specified coordinates, or null if the
	 *         coordinates are out of bounds.
	 */
	public final Tile getTile(final int x, final int y) {
		if (x < 0 || y < 0 || x >= DIMENSION || y >= DIMENSION) return null;
		return field[getIndex(x, y)];
	}
	
	public int getIndex(int x, int y) {
		return x + y * DIMENSION;
	}
	
	public int getX(int index) {
		return index % DIMENSION;
	}
	
	public int getY(int index) {
		return index / DIMENSION;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(2 * DIMENSION * DIMENSION);
		
		for (Tile tile : field) {
			builder.append(tile.getIndex());
			builder.append(' ');
		}
		
		return builder.toString();
	}
}
