package team144.rolit.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import team144.rolit.Game;
import team144.rolit.Player;
import team144.rolit.Tile;

public class Server implements NetworkListener {
    public static final int DEFAULT_PORT = 2014;
    
    /**
     * connections - not logged in yet
     */
    private ArrayList<Connection>  connections = new ArrayList<Connection>();
    /**
     * authorizedConnections - logged in
     */
   private ArrayList<Connection>  authorizedConnections = new ArrayList<Connection>();
    
    private ServerSocket serverSocket;
    private ServerMonitor monitor;
    
    private Authenticator authenticator;
    private Connection logginInPeer;//peer currently in login session
    
    private String randomText;
    
    public static void main(String[] args) {
        Server server = new Server(DEFAULT_PORT);
        server.createRoom(3);
    }
    
    public Server(int port) {
        monitor = new ServerMonitor(this);
        authenticator = new Authenticator();
        try {
            serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createRoom(int roomSize) {
        try {
            while (connections.size() < roomSize) {
                Connection conn = new Connection(serverSocket.accept(), this);
                connections.add(conn);
                conn.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        startGame();
    }
    
    private void startGame() {
        int roomSize  = authorizedConnections.size();
        Player[] players = new Player[roomSize];
        for (int i = 0; i < roomSize; i++) {
            players[i] = new Player(Tile.values()[i + 1], authorizedConnections.get(i).getName());
        }
        Game game = new Game(players);
        monitor.setGame(game);
        
        String[] playerNames = new String[players.length];
        
        for (int i = 0; i < players.length; i++) {
            playerNames[i] = players[i].getName();
        }
        
        sendCommandToAll("START", playerNames);
    }
    
    public void sendCommand(Connection client, String cmd, String...parameters) {
        client.write(cmd, parameters);
    }
    
    public void sendCommandToAll(String cmd, String...parameters){
        for (int i = 0; i < connections.size(); i++) {
            sendCommand(connections.get(i), cmd, parameters);
        }
    }
    
    @Override
    public boolean executeCommand(String cmd, String[] parameters, Connection peer) {
        monitor.executeCommand(cmd, parameters);
//		System.out.println("ExecuteCommand()\t"+cmd+" "+Util.concat(parameters));
        switch (cmd) {
            case("LOGIN"):
                randomText = Authenticator.generateRandomString(10);
                logginInPeer = peer;
                peer.setName(parameters[0]);
                sendCommand(peer, "VSIGN", randomText);
                break;
            case("VSIGN"):
                boolean legit = authenticator.verifySignature("player_"+logginInPeer.getName(), randomText , parameters[0]);
                if(legit){
                    authorizedConnections.add(peer);
                    sendCommand(peer, "HELLO", "D"); //default
                    System.out.println("Player " + logginInPeer.getName() +" logged in.");
                }else{
                    sendCommand(peer, "ERROR", "Text signed incorrectly");
                    System.out.println("Player " + logginInPeer.getName() +" failed to log in.");
                }
                //if !legit sendMessage fuckoff no legit
                break;
            case("NGAME"):
                Room.assignRoom(peer, parameters);
                break;
            case ("SHOW"):  //message to server, otherwise needs clientname argument
                monitor.executeCommand(cmd, parameters);
                break;
            case ("GMOVE"): //GMOVE x y
                sendCommandToAll(cmd, parameters);
                monitor.executeCommand(cmd, parameters);
                break;
        }
        
        return false;
    }

    @Override
    public String getName() {
        return "server";
    }
    
}
