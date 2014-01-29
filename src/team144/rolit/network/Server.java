package team144.rolit.network;

import java.io.IOException;
import java.net.BindException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import team144.util.Util;

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
        
        int port = 0;
        String input = JOptionPane.showInputDialog("Type port");
        
        try {
            
            if (input.trim().equals("")) {
                port = DEFAULT_PORT;
            } else {
                port = Integer.parseInt(input);
            }
            
            new Server(port);
        } catch (BindException |NumberFormatException f) {
            main(null);
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Server(int port) throws IOException, BindException {
        monitor = new ServerMonitor(this);
        authenticator = new Authenticator();
        serverSocket = new ServerSocket(port);
        
        monitor.showCommand("Server's ip-address is: ", Inet4Address.getLocalHost().getHostAddress());
        
        while (true) {
            Connection conn = new Connection(serverSocket.accept(), this);
            connections.add(conn);
            conn.start();
        }
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
                Room.assignRoom(peer, this, cmd, parameters);
                break;
            case ("INVIT"):
                Room.assignRoom(peer, this, cmd, parameters);
                break;
            case ("GMOVE"): //GMOVE x y
                sendCommandToRoom(peer, cmd, parameters);
                break;
            case ("CHATM"): //CHATM from message
                String[] params = (peer.getName() + " " + Util.concat(parameters)).split(" ");
                if (Room.isInRoom(peer)) {
                    sendCommandToRoom(peer, cmd, params); //to game
                }
                sendCommandToAll(cmd, params); //to lobby
                break;
            case ("PLIST"):
                ArrayList<String> playerList = new ArrayList<String>();
                
                for (Connection c : authorizedConnections) {
                    if (!Room.isInRoom(c)) playerList.add(c.getName());
                }
                
                sendCommand(peer, "PLIST", playerList.toArray(new String[0]));
                break;
            case ("PROTO"):
                sendCommand(peer, "PROTO", Info.NAME, Info.VERSION);
                break;
            case ("SINFO"):
                sendCommand(peer, "SINFO", Info.PROGRAM_NAME, Info.PROGRAM_VERSION);
                break;
            case ("ALIVE"):
                break;
            default:
                System.out.println("Command not implemented: " + cmd);
                break;
        }
        
        return false;
    }
    
    @Override
    public void endConnection(Connection c) {
        connections.remove(c);
        authorizedConnections.remove(c);
    }
    
    /**
     * @param invitee - player name, not null.
     * @return - The connection of the player with that name, or null if he's not online.
     */
    public Connection getPlayer(String invitee) {
        for(Connection c : authorizedConnections){
            if(c.getName().equals(invitee)) return c;
        }
        
        return null;
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
