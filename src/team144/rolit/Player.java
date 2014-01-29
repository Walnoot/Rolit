package team144.rolit;


public class Player {
    private Tile tile;
    private String name;
    private Game game;
    private Strategy strategy; //if null -> is human
    
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

    public void requestMove() {
        if(strategy!=null){
            game.makeMove(this.getName(), strategy.findMove(game, name)); 
        }else{
            //wait till user pressed a button
        }
    }

    public void setGame(Game game) {
        this.game = game;
        
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
}
