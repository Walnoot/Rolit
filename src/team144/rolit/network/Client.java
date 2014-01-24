package team144.rolit.network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import team144.rolit.Game;
import team144.rolit.Player;
import team144.rolit.Tile;
import team144.util.Util;

public class Client implements NetworkListener {
    
    private Socket socket;
    private Connection peer;
    private Authenticator authenticator;
    private String name;
    private Game game;
    private Player player;
    
    private ClientListener clientListener;
    
    public Client(String ip, int port, String name) throws UnknownHostException, IOException {
        this.name = name;
        socket = new Socket(ip, port);
        peer = new Connection(socket, this);
        
        authenticator = new Authenticator();
        peer.start();
        
    }
    
    public void sendCommand(String cmd, String... parameters) {
        printMessage("sendCommand()\t" + cmd + " " + Util.concat(parameters));
        peer.write(cmd, parameters);
    }
    
    /**
     * Login to game server
     */
    public void login(String password) {
        try {
            authenticator.login("player_" + name, password);
            sendCommand("LOGIN", this.name);
        } catch (Exception e) {
            clientListener.loginError();
        }
    }
    
    private void requestNewGame(String...flags) {
        sendCommand("NGAME", flags);
    }
    
    private void printMessage(String m) {
        System.out.println(name + ":\t" + m);
    }
    
    @Override
    public boolean executeCommand(String cmd, String[] parameters, Connection peer) {
        // printMessage("ExecuteCommand()\t" + cmd + " " + Util.concat(parameters));
        switch (cmd) {
            case ("VSIGN"): //   VSIGN TEXT
                String signature = authenticator.signMessage(parameters[0]);
                sendCommand("VSIGN", signature);
                break;
            case ("HELLO"):
                clientListener.onHello(parameters[0]);
                sendCommand("HELLO", "D");
                break;
            case ("START"): //  START [Bob, Alice, Lol]
                Player[] players = new Player[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    Player player = new Player(Tile.values()[i + 1], parameters[i]);
                    players[i] = player;
                    
                    if (player.getName().equals(name)) {
                        this.player = player;
                    }
                    ;
                }
                
                if (player == null) System.out.println("Controlled player not found?!");
                
                game = new Game(players);
                break;
            case ("GMOVE"): //GMOVE x y
                int x = Integer.parseInt(parameters[0]);
                int y = Integer.parseInt(parameters[1]);
                game.makeMove(game.getCurrentPlayer(), x, y);
                break;
            case ("BCAST"): //BCAST text text to client text
//                controller.showMessage(Util.concat(parameters));
                System.out.println(Util.concat(parameters));
                break;
            case ("ERROR"):
                if (clientListener != null) {
                    clientListener.loginError();
                }
                System.out.println("ERROR: " + Util.concat(parameters));
                break;
        }
        
        return false;
    }
    
    public Game getGame() {
        return game;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public void shutdown() {
        peer.setRunning(false);
    }
    
    public void setClientListener(ClientListener cl) {
        clientListener = cl;
    }
    
    public static interface ClientListener {
        public void onHello(String flag);
        
        public void gameReady();
        
        //public void error(String message);
        public void loginError();
    }
}
