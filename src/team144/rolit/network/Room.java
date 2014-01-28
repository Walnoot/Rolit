package team144.rolit.network;

import java.util.ArrayList;

import team144.util.Util;

public class Room {
    
    /**
     * C - 1v1 (requires name)
     * D - default (2 players multiplayer)
     * H - 1 random humans
     * I - 2 random humans
     * J - 3 random humans
     */
    public String type;
    public int roomSize;
    public ArrayList<Connection> players;
    
    public static ArrayList<Room> rooms;
    
    private Room(Connection player, String[] params) {
        type = parseType(params);
        if (type.equals("C") || type.equals("D") || type.equals("H")) {
            roomSize = 2;
        } else if (type.equals("I")) {
            roomSize = 3;
        } else {
            roomSize = 4;
        }
        players = new ArrayList<Connection>(roomSize);
        addPlayer(player);
    }
    
    private static String parseType(String[] params) {
        String type;
        if (params[0].equals("C")) {
            type = params[1];
        } else {
            type = params[0];
        }
        return type;
    }
    
    /**
     * Looks if player fits in any existing room
     * If not, creates a new rooms and puts him in it
     * 
     * @param player
     *            - Connection of the player that requested a new game
     * @param params
     *            - flags (gametype/player)
     */
    public static void assignRoom(Connection player, String[] params) {
        if (rooms == null) rooms = new ArrayList<Room>();
        
        String wantedType = parseType(params);
        
        for (Room r : rooms) {
            if (r.type.equals(wantedType)) {
                r.addPlayer(player);
            }
        }
        
        //no rooms exists yet so make one
        rooms.add(new Room(player, params));
    }
    
    private void addPlayer(Connection player) {
        players.add(player);
        if (players.size() == roomSize) {
            String[] names = new String[players.size()];
            for (int i = 0; i < roomSize; i++) {
                names[i] = players.get(i).getName();
            }
            System.out.println("Room starting: " + Util.concat(names));
            
            for (Connection c : players) {
                c.write("START", names);
            }
        }
    }
    
    public void executeCommand(String cmd, String... parameters){
        for (Connection c : players) {
            c.executeCommand(cmd,parameters);
        }
    }

    public void sendCommand(String cmd, String... parameters) {
        for (Connection c : players) {
            c.write(cmd, parameters);
        }
    }
    
}
