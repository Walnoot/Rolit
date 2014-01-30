package team144.rolit.tests;

import java.util.ArrayList;
import java.util.Random;

import team144.rolit.Board;
import team144.rolit.Game;
import team144.rolit.Player;
import team144.rolit.Tile;

public class IllegalMoveTest {
	
	static Game game;
	static Random rand;
	static Player alice;
	static Player bob;
	
	public static void main(String[] args) {
		setup();
		startTest();
	}

	private static void setup() {
		rand = new Random();
		alice = new Player(Tile.RED, "alice");
		bob = new Player(Tile.YELLOW, "bob");
		game = new Game(alice, bob);
	}
	
	/**
	 * try to make some illegal moves, eg wrong position or it's not player's turn,
	 * and the simulate the server's behaviour, in th.e same way
	 */
	private static void startTest() {
		System.out.println("Try: Make legal move");
		makeLegalMove(alice);
		System.out.println("Try: Make legal move");
		makeLegalMove(bob);
		
		System.out.println("Try: Make Illegal move");
		makeIllegalMove(alice);
		System.out.println("Try: Make legal move");
		makeLegalMove(alice);
		System.out.println("Try: Make legal move, but not my turn");
		makeLegalMove(alice);
		System.out.println("Try: Make illegal move, but not my turn");
		makeIllegalMove(bob);
		System.out.println("Try: Make legal move");
		makeLegalMove(bob);
	}


	/**
	 * Simulate legal move
	 */
	private static void makeLegalMove(Player player) {
		ArrayList<Integer> legalMoves = game.getLegalMoves();
		int move = legalMoves.get(rand.nextInt(legalMoves.size()));
		writeMove(player, Board.getX(move), Board.getY(move));
	}
	
	/**
	 * Simulate illegal move
	 */
	private static void makeIllegalMove(Player player) {
		ArrayList<Integer> legalMoves = game.getLegalMoves();
		int move = rand.nextInt(64);
		while(legalMoves.contains(move)){
			move = legalMoves.get(rand.nextInt(legalMoves.size()));
		}
		writeMove(player, Board.getX(move), Board.getY(move));
	}
	
	/**
	 * simulate server behavior; 
	 * Make move if allowed otherwise give an error
	 * @param p - player making the move
	 * @param x - position
	 * @param y - position
	 */
	private static void writeMove(Player p, int x, int y){
		try {
			Thread.sleep(300); 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		boolean valid = game.isValidMove(Board.getIndex(x, y)) && game.getCurrentPlayer().getName().equals(p.getName());
		if (valid) {
			game.makeMove(p.getTile().getIndex(), x, y);
			System.out.println("Made a move!");
//			System.out.println(p.getName() + " made a legal move!" );
		}else{
			System.out.println("NO MOVE SET!");
//			System.out.println(p.getName() + " tried to make an illegal move!" );
		}
	}
}
