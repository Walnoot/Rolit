package team144.rolit.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import team144.rolit.Game;
import team144.rolit.Player;
import team144.rolit.Tile;
import team144.util.Util;

public class Server implements NetworkListener {
    public static final int DEFAULT_PORT = 2014;
    
    private ArrayList<Peer> clients;
    private Player[] players;
    private ServerSocket serverSocket;
    private ServerMonitor monitor;
    
    private Authenticator authenticator;
    private Peer logginInPeer;//peer currently in login session
    
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
        clients = new ArrayList<Peer>();
        players = new Player[roomSize];
        
        try {
            while (clients.size() < roomSize) { //kan fout gaan
                Peer client = new Peer(serverSocket.accept(), this);
                clients.add(client);
                //sendCommand(client, "je bent verbonden" , "testtstes" );
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        startGame();
    }
    
    private void startGame() {
        for (int i = 0; i < clients.size(); i++) {
            players[i] = new Player(Tile.values()[i + 1], clients.get(i).getName());
        }
        Game game = new Game(players);
        monitor.setGame(game);
        
        String[] playerNames = new String[players.length];
        
        for (int i = 0; i < players.length; i++) {
            playerNames[i] = players[i].getName();
        }
        
        sendCommandToAll("START", playerNames);
    }
    
//	private void printMessage(String m) {
//		monitor.print(name + ":\t" + m);
//	}
    
    public void sendCommand(Peer client, String cmd, String...parameters) {
        //printMessage("sendCommand()\t" + cmd + " " + Util.concat(parameters));
        client.write(cmd, parameters);
    }
    
    public void sendCommandToAll(String cmd, String...parameters){
        for (int i = 0; i < clients.size(); i++) {
            sendCommand(clients.get(i), cmd, parameters);
        }
    }
    
    @Override
    public boolean executeCommand(String cmd, String[] parameters, Peer peer) {
        monitor.executeCommand(cmd, parameters);
		System.out.println("ExecuteCommand()\t"+cmd+" "+Util.concat(parameters));
        switch (cmd) {
            case("LOGIN"):
                logginInPeer = peer;
                peer.setName(parameters[0]);
                sendCommand(peer, "VSIGN", "VerySecureRandomText");
                break;
            case("VSIGN"):
                System.out.println("VSIGN");
                boolean legit = authenticator.verifySignature(logginInPeer.getName(), "VerySecureRandomText" , parameters[0]);
                System.out.println("Player " + logginInPeer.getName() +" is legit:"+legit);
                //if !legit sendMessage fuckoff no legit
                break;
            case ("SHOW"):  //message to server, otherwise needs clientname argument
                monitor.executeCommand(cmd, parameters);
                break;
//            case ("NGAME"):
//                if(parameters[0].startsWith("C")){
//                    createRoom(parameters[0].charAt(1));
//                }
//                break;
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
