package team144.rolit;

import team144.rolit.network.Client;
import team144.rolit.network.Connection;

public class Player {
	private Tile tile;
	private String name;
	private Game game;
	private Strategy strategy; //if null -> is human
	public int index;
	
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
	
	public void requestMove(Connection conn) {
		if (strategy != null) {
			int move = strategy.findMove(game, getName());
			conn.write("GMOVE", Integer.toString(game.getBoard().getX(move)),
					Integer.toString(game.getBoard().getY(move)));
		} else {
			//wait till user pressed a button
		}
	}
	
	public void trySendMove(Client client, int x, int y){
		if (this == game.getCurrentPlayer() && game.isValidMove(x, y)) {
			client.sendCommand("GMOVE", Integer.toString(x), Integer.toString(y));
		}
	}
	
	public void trySendMove(Client client, int i) {
		trySendMove(client, game.getBoard().getX(i), game.getBoard().getY(i));
	}
	
	public void setGame(Game game) {
		this.game = game;
		
	}
	
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
}
