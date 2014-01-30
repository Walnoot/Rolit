package team144.rolit;

import java.awt.Color;

/**
 * Represents the state of a place on the board.
 * 
 * @author Michiel
 */
public enum Tile {
	EMPTY(Color.WHITE, 0), RED(Color.RED, 1), YELLOW(Color.YELLOW, 2), GREEN(Color.GREEN, 3), BLUE(Color.BLUE, 4);
	
	private final Color color;
	private int index;
	
	private Tile(Color color, int index) {
		this.color = color;
		this.index = index;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getIndex() {
		return index;
	}
}
