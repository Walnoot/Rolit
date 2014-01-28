package team144.rolit.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import team144.rolit.Player;
import team144.rolit.Tile;

public class Server implements NetworkListener {
    public static final int DEFAULT_PORT = 2014;
    
    /**
     * connections - not logged in yet
     */
    private ArrayList<Connection> connections = new ArrayList<Connection>();
    /**
     * authorizedConnections - logged in
     */
    private ArrayList<Connection> authorizedConnections = new ArrayList<Connection>();
    
    private ServerSocket serverSocket;
    private ServerMonitor monitor;
    
    private Authenticator authenticator;
    
    private String randomText;
    
    public static void main(String[] args) {
        //TODO: uncomment
//        int port = -1;
        int port = DEFAULT_PORT;//for debug purposes
        
        while (port == -1) {
            String input = JOptionPane.showInputDialog("Type port");
            try {
                port = Integer.parseInt(input);
            } catch (Exception e) {
            }
        }
        
        try {
            new Server(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Server(int port) throws IOException {
        monitor = new ServerMonitor(this);
        authenticator = new Authenticator();
        serverSocket = new ServerSocket(port);
        
        try {
            while (true) {
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
        int roomSize = authorizedConnections.size();
        Player[] players = new Player[roomSize];
        for (int i = 0; i < roomSize; i++) {
            players[i] = new Player(Tile.values()[i + 1], authorizedConnections.get(i).getName());
        }
        
        String[] playerNames = new String[players.length];
        
        for (int i = 0; i < players.length; i++) {
            playerNames[i] = players[i].getName();
        }
        
        sendCommandToAll("START", playerNames);
    }
    
    public void sendCommand(Connection client, String cmd, String... parameters) {
        client.write(cmd, parameters);
    }
    
    public void sendCommandToAll(String cmd, String... parameters) {
        for (int i = 0; i < connections.size(); i++) {
            sendCommand(connections.get(i), cmd, parameters);
        }
    }
    
    public void sendCommandToRoom(Connection c, String cmd, String... parameters) {
        Room.getRoom(c).sendCommand(cmd, parameters);
    }
    
    @Override
    public boolean executeCommand(String cmd, String[] parameters, Connection peer) {
        monitor.showCommand(cmd, parameters);
//		System.out.println("ExecuteCommand()\t"+cmd+" "+Util.concat(parameters));
        switch (cmd) {
            case ("LOGIN"):
                randomText = Authenticator.generateRandomString(10);
                peer.setName(parameters[0]);
                sendCommand(peer, "VSIGN", randomText);
                break;
            case ("VSIGN"):
                boolean legit = authenticator.verifySignature("player_" + peer.getName(), randomText, parameters[0]);
                if (legit) {
                    if (!hasConnectionWithName(peer.getName())) {
                        authorizedConnections.add(peer);
                        sendCommand(peer, "HELLO", "D"); //default
                        sendCommandToAll("LJOIN", peer.getName());
                        
                        System.out.println("Player " + peer.getName() + " logged in.");
                    } else {
                        sendCommand(peer, "ERROR", "An player with that name is already logged in");
                    }
                } else {
                    sendCommand(peer, "ERROR", "Text signed incorrectly");
                    System.out.println("Player " + peer.getName() + " failed to log in.");
                }
                break;
            case ("NGAME"):
                Room.assignRoom(peer, cmd, parameters);
                break;
            case ("INVIT"):
                Room.assignRoom(peer, cmd, parameters);
                break;
            case ("GMOVE"): //GMOVE x y
                sendCommandToRoom(peer, cmd, parameters);
                break;
            case("CHATM"):
                sendCommandToRoom(peer,cmd, peer.getName()+parameters );
                break;
            case("PLIST"):
                ArrayList<String> playerList = new ArrayList<String>();
                
                for(Connection c : authorizedConnections){
                    if(!Room.isInRoom(c)) playerList.add(c.getName());
                }
                
                sendCommand(peer, "PLIST", playerList.toArray(new String[0]));
                
                break;
            default:
                System.out.println("Command not implemented: " + cmd);
                break;
        }
        
        return false;
    }
    
    private boolean hasConnectionWithName(String name) {
        for (Connection c : authorizedConnections) {
            if (c.getName().equals(name)) return true;
        }
        
        return false;
    }
    
    @Override
    public String getName() {
        return "server";
    }
    
}
