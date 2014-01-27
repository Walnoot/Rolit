package team144.rolit.network;

import java.util.ArrayList;
import java.util.HashMap;

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
    public ArrayList<Connection> players = new ArrayList<Connection>();
    
    private static ArrayList<Room> rooms = new ArrayList<Room>();
    private static HashMap<Connection, Room> roomMap = new HashMap<Connection, Room>();
    
    private Room(Connection player, String[] params) {
        type = parseType(params);
        if(type.equals("C") || type.equals("D")|| type.equals("H")){
            roomSize = 2;
        }else if(type.equals("I")){
            roomSize = 3;
        }else{
            roomSize = 4;
        }
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
     * @param player - Connection of the player that requested a new game
     * @param params - flags (gametype/player)
     */
    public static void assignRoom(Connection player, String[] params) {
        String wantedType = parseType(params);
        
        for (Room r : rooms) {
            if (r.type.equals(wantedType)) {
                r.addPlayer(player);
                roomMap.put(player, r);
                
                return;
            }
        }
        
        //no rooms exists yet so make one
        Room room = new Room(player,params);
        rooms.add(room);
        roomMap.put(player, room);
    }
    
    /**
     * Gets the room of the specified connection, or null if it doesnt exist.
     * @param c
     * @return
     */
    public static Room getRoom(Connection c){
        return roomMap.get(c);
    }
    
    private void addPlayer(Connection player) {
        players.add(player);
        if(players.size()==roomSize){
            String[] names = new String[players.size()];
            for (int i = 0; i<roomSize; i++) {
                names[i]=players.get(i).getName();
            }
            System.out.println("Room starting: "+Util.concat(names));
            
            for (Connection c : players) {
                c.write("START", names);
            }
        }
    }
    
    public void sendCommand(String cmd, String...parameters){
        for(Connection c : players){
            c.write(cmd, parameters);
        }
    }
}
