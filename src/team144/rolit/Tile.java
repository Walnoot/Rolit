package team144.rolit;

import java.awt.Color;

/**
 * Represents the state of a place on the board.
 * @author Michiel
 */
public enum Tile {
    EMPTY(Color.WHITE), RED(Color.RED), YELLOW(Color.YELLOW), GREEN(Color.GREEN), BLUE(Color.BLUE);
    
    private final Color color;

    private Tile(Color color){
        this.color = color;
    }
    
    public Color getColor() {
        return color;
    }
}
