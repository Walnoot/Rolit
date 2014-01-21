package team144.rolit;

public class Player {
    private Tile tile;
    private String name;
    
    public Player(Tile tile, String name) {
        this.tile = tile;
        this.name = name;
    }
    
    public Tile getTile() {
        return tile;
    }
    
    public String getName() {
        return name;
    }
}
