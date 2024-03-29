package team144.rolit.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import team144.rolit.Board;
import team144.rolit.Game;
import team144.rolit.Player;
import team144.rolit.Tile;
import team144.util.Util;

public class Room {
	
	/**
	 * This class keeps track of all the players that are either waiting to be
	 * added to a room, or are actually in a room.
	 * the room.type field is used to match just connected users to a room.
	 * It also handles all players wanting to join a room, and can send commands
	 * to all players in some room
	 * roomMap can be used to get a Room matching a connection
	 * 
	 * NGAME flags:
	 * D - default (2 players multiplayer)
	 * H - 1 random humans
	 * I - 2 random humans
	 * J - 3 random humans
	 * 
	 * INVIT flags:
	 * A - accept
	 * R - request (list of players)
	 * F - fail (player not in Lobby)
	 * D - deny (requested player explicitly refused to play)
	 */
	
	/**
	 * Which players the room accepts, see above
	 */
	private String type;
	/**
	 * Amount of players to fit in room
	 */
	private int roomSize;
	/**
	 * Connections of all players within a room
	 */
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	/**
	 * Whether or not the game has started
	 */
	private boolean isPlaying = false;
	/**
	 * keeps track of the game the room players are playing
	 */
	private Game game;
	
	/**
	 * contains all rooms on the server
	 */
	private static ArrayList<Room> rooms = new ArrayList<Room>();
	/**
	 * maps all connections to a room, for easy lookup
	 */
	private static HashMap<Connection, Room> roomMap = new HashMap<Connection, Room>();
	
	/**
	 * Creates a new room for player (no the client wanted already existed)
	 * 
	 * @param player
	 * - who requested the game
	 * @param type
	 * - the type of game player requested
	 */
	private Room(Connection player, String type) {
		this.type = type;
		if (type.equals("D") || type.equals("H")) {
			roomSize = 2;
		} else if (type.equals("I")) {
			roomSize = 3;
		} else if (type.equals("J")) {
			roomSize = 4;
		} else if (type.startsWith("INVIT")) {
			String[] params = type.split(" ");
			
			roomSize = params.length;
		}
		
		addPlayer(player);
	}
	
	/**
	 * Determines the type identifier of a requested room/game
	 * 
	 * @param cmd
	 * - game request cmd
	 * @param params
	 * - parameters
	 * @return a type identifier to match players into rooms
	 */
	private static String parseType(String cmd, String[] params) {
		String type = "";
		
		if (cmd.equals("NGAME")) {
			type = params[0];
			if (type.equals("D")) type = "H";
		} else if (cmd.equals("INVIT")) {
			type = cmd + " " + Util.concat(Arrays.copyOfRange(params, 1, params.length));
		}
		
		return type;
	}
	
	/**
	 * Looks if player fits in any existing room
	 * If not, creates a new rooms and puts him in it
	 * 
	 * @param player
	 * - Connection of the player that requested a new game
	 * @param cmd
	 * - NGAME or INVIT
	 * @param params
	 * - flags (gametype/player)
	 */
	public static void assignRoom(Connection player, Server server, String cmd, String[] params) {
		Room previousRoom = roomMap.get(player);
		if (previousRoom != null) previousRoom.removeConnection(player);
		
		roomMap.remove(player);
		
		String wantedType = parseType(cmd, params);
		
		if (cmd.equals("NGAME")) {
			for (Room r : rooms) {
				if (r.type.equals(wantedType) && !r.isPlaying) {
					r.addPlayer(player);
					roomMap.put(player, r);
					return;
				}
			}
		} else if (cmd.equals("INVIT")) {
			if (params[0].equals("R")) {
				for (int i = 1; i < params.length; i++) {
					String invitee = params[i];
					
					Connection invitedConnection = server.getPlayer(invitee);
					
					if (invitedConnection == null || isInRoom(invitedConnection)) {
						player.write("INVIT", "F");
					} else {
						invitedConnection.write("INVIT", "R", player.getName());
					}
				}
			} else {
				for (Room r : rooms) {
					if (r.type.contains(player.getName())) {
						if (params[0].equals("A")) {
							r.addPlayer(player);
						} else {
							r.removeConnection(player);
							
							r.sendCommand(cmd, player, params[0]);
						}
						return;
					}
				}
			}
		}
		//no rooms exists yet so make one
		Room room = new Room(player, wantedType);
		rooms.add(room);
		roomMap.put(player, room);
	}
	
	/**
	 * Gets the room of the specified connection, or null if it doesn't exist.
	 * 
	 * @param c
	 * - connection between the server and client requesting the game
	 * @return a Room the player is in
	 */
	public static Room getRoom(Connection c) {
		return roomMap.get(c);
	}
	
	/**
	 * @param c
	 * - connection of client
	 * @return whether or not this connection is in the Room
	 */
	public static boolean isInRoom(Connection c) {
		return roomMap.get(c) != null;
	}
	
	/**
	 * adds a player to the room, and starts the game if room is full
	 * 
	 * @param player
	 * - connection to add
	 */
	private void addPlayer(Connection player) {
		connections.add(player);
		roomMap.put(player, this);
		if (connections.size() == roomSize) {
			isPlaying = true;
			startGame();
		}
	}
	
	/**
	 * is called if room has all required players
	 * starts new game by sending the START command to all players in the room
	 */
	private void startGame() {
		String[] names = new String[roomSize];
		Player[] players = new Player[roomSize];
		Tile[] colors = Tile.values();
		
		for (int i = 0; i < roomSize; i++) {
			names[i] = connections.get(i).getName();
			players[i] = new Player(colors[i + 1], names[i]);
		}
		
		for (Connection c : connections) {
			c.write("START", names);
			c.write("GTURN", "1");
		}
		
		isPlaying = true;
		game = new Game(players);
	}
	
	/**
	 * Removes connection from it's room, if any
	 * 
	 * @param c
	 * - connection to remove
	 */
	public static void remove(Connection c) {
		Room room = roomMap.get(c);
		if (room != null) {
			room.removeConnection(c);
		}
	}
	
	/**
	 * remove connection, and checks if room is empty.
	 * Deletes Room if it's empty
	 * 
	 * @param c
	 * - connection to remove
	 */
	private void removeConnection(Connection c) {
		connections.remove(c);
		roomMap.remove(c);
		if (connections.size() == 0 || isPlaying) rooms.remove(this);
	}
	
	/**
	 * sends command to all connections matching the players in the room
	 * 
	 * @param cmd
	 * - command
	 * @param parameters
	 * - parameters
	 */
	public void sendCommand(String cmd, Connection con, String... parameters) {
		if (cmd.equals("GTURN")) {
			for (Connection c : connections) {
				c.write(cmd, Integer.toString(game.getCurrentPlayer().index));
			}
			return;
		}
		if (cmd.equals("GMOVE")) {
			System.out.println(Arrays.toString(parameters));
			
			int x = Integer.parseInt(parameters[1]);
			int y = Integer.parseInt(parameters[2]);
			boolean valid = game.isValidMove(Board.getIndex(x, y)) && game.getCurrentPlayer().getName().equals(con.getName());
			if (valid) {
				game.makeMove(Integer.parseInt(parameters[0]), x, y);
				for (Connection c : connections) {
					c.write(cmd, parameters);
				}
				sendCommand("GTURN", con, parameters[0]);
				
				if (game.isGameOver()) {
					sendCommand("STATE", con, "STOPPED");
					System.out.println("GAME OVER!");
					
					for(Connection c : connections){
						removeConnection(c);
					}
				}
				return;
			} else {
				sendCommand("ERROR",con, "Invalid Move!");
				return;
			}
		}
		if (cmd.equals("BOARD")) {
			sendCommand("BOARD",con, game.getBoard().toString());
			return;
		}
		
		for (Connection c : connections) {
			c.write(cmd, parameters);
		}
	}
	
	/**
	 * return the {@link Game} the Room is playing
	 */
	public Game getGame() {
		return game;
	}
}
