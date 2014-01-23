package team144.rolit.network;

import java.io.IOException;
import java.net.ServerSocket;

import team144.rolit.Game;
import team144.rolit.Player;
import team144.rolit.Tile;

public class Server implements NetworkListener {
    public static final int DEFAULT_PORT = 2014;
    
    private Peer[] clients;
    private Player[] players;
    private int clientsConnected;
    private ServerSocket serverSocket;
    private String name;
    private ServerMonitor monitor;
    
    public static void main(String[] args) {
        Server server = new Server(DEFAULT_PORT, "Willem");
        server.createRoom(3);
    }
    
    public Server(int port, String name) {
        monitor = new ServerMonitor(this);
        try {
            this.name = name;
            serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createRoom(int roomSize) {
        clients = new Peer[roomSize];
        players = new Player[roomSize];
        
        try {
            while (clientsConnected < clients.length) { //kan fout gaan
                Peer client = new Peer(serverSocket.accept(), this);
                clients[clientsConnected++] = client;
                client.start();
                executeCommand("CONNECTED", new String[] { client.getName() });
                //printMessage("Client connected: "+client.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        startGame();
    }
    
    private void startGame() {
        for (int i = 0; i < clientsConnected; i++) {
            players[i] = new Player(Tile.values()[i + 1], clients[i].getName());
        }
        Game game = new Game(players);
        monitor.setGame(game);
        sendCommand("START", players);
    }
    
//	private void printMessage(String m) {
//		monitor.print(name + ":\t" + m);
//	}
    
    public void sendCommand(String cmd, Object[] parameters) {
        if (cmd.equals("START")) {
            String[] names = new String[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                names[i] = ((Player) parameters[i]).getName();
            }
            sendCommand(cmd, names);
        }
    }
    
    public void sendCommand(Peer client, String cmd, String[] parameters) {
        //printMessage("sendCommand()\t" + cmd + " " + Util.concat(parameters));
        client.write(cmd, parameters);
    }
    
    public void sendCommand(String cmd, String[] parameters) {
        for (Peer client : clients) {
            //printMessage("sendCommand()\t" + cmd + " " + Util.concat(parameters));
            client.write(cmd, parameters);
        }
    }
    
    @Override
    public boolean executeCommand(String cmd, String[] parameters) {
        monitor.executeCommand(cmd, parameters);
//		printMessage("ExecuteCommand()\t"+cmd+" "+Util.concat(parameters));
        switch (cmd) {
            case ("SHOW"):  //message to server, otherwise needs clientname argument
                monitor.executeCommand(cmd, parameters);
                break;
//            case ("NGAME"):
//                if(parameters[0].startsWith("C")){
//                    createRoom(parameters[0].charAt(1));
//                }
//                break;
            case ("GMOVE"): //GMOVE x y
                sendCommand(cmd, parameters);
                monitor.executeCommand(cmd, parameters);
        }
        
        return false;
    }
    
    @Override
    public String getName() {
        return name;
    }
}
