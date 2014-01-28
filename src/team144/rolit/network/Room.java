package team144.rolit.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import team144.util.Util;

public class Room {
    
    /**
     * This class keeps track of all the players that are either waiting to be added to a room, or are actually in a room.
     * the room.type field is used to match just connected users to a room.
     * It also handles all players wanting to join a room, and can send commands to all players in some room
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
    
    private String type;
    private int roomSize;
    private ArrayList<Connection> players = new ArrayList<Connection>();
    private boolean isPlaying = false;
    
    private static ArrayList<Room> rooms = new ArrayList<Room>();
    private static HashMap<Connection, Room> roomMap = new HashMap<Connection, Room>();
    
    
    private Room(Connection player, String type) {
        this.type = type;
        if (type.equals("D") || type.equals("H")) {
            roomSize = 2;
        } else if (type.equals("I")) {
            roomSize = 3;
        } else if(type.equals("J")){
            roomSize = 4;
        } else if(type.startsWith("INVIT")){
            String[] params = type.split(" ");
            
            roomSize = params.length;
        }
        
        addPlayer(player);
    }
    
    /**
     * @param cmd
     * @param params
     * @return
     */
    private static String parseType(String cmd, String[] params) {
        String type = "";
        
        if (cmd.equals("NGAME")) {
            type = params[0];
            if(type.equals("D")) type = "H";
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
     *            - Connection of the player that requested a new game
     * @param cmd
     *            - NGAME or INVIT
     * @param params
     *            - flags (gametype/player)
     */
    public static void assignRoom(Connection player, Server server, String cmd, String[] params) {
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
                    
                    if(invitedConnection == null || isInRoom(invitedConnection)){
                        player.write("INVIT", "F");
                    }else{
                        invitedConnection.write("INVIT", "R", player.getName());
                    }
                }
            } else {
                for (Room r : rooms) {
                    if (r.type.contains(player.getName())) {
                        if (params[0].equals("A")) {
                            r.addPlayer(player);
                        } else {
                            //Player Denied request (invite failed not implemented yet)
                            r.sendCommand(cmd, params[0]);
                            
                            rooms.remove(r);
                            roomMap.remove(r);
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
     * @return
     */
    public static Room getRoom(Connection c) {
        return roomMap.get(c);
    }
    
    public static boolean isInRoom(Connection c) {
        return roomMap.get(c) != null;
    }
    
    //@requires player != null
    private void addPlayer(Connection player) {
        players.add(player);
        roomMap.put(player, this);
        if (players.size() == roomSize) {
            isPlaying = true;
            startGame();
        }
    }
    
    /**
     * is called if room has all required players
     * starts new game by sending the START command to all players in the room
     */
    private void startGame() {
        String[] names = new String[players.size()];
        for (int i = 0; i < roomSize; i++) {
            names[i] = players.get(i).getName();
        }
        
        isPlaying = true;
        for (Connection c : players) {
            c.write("START", names);
        }
    }
    
    /**
     * sends command to all connections matching the players in the room
     * @param cmd - command
     * @param parameters - parameters
     */
    public void sendCommand(String cmd, String... parameters) {
        for (Connection c : players) {
            c.write(cmd, parameters);
        }
    }
}
