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
    
    public static void main(String[] args) throws UnknownHostException, IOException {
        Client client = new Client("127.0.0.1", Server.DEFAULT_PORT, "player_willemsiers");
        
        client.login();
//        client.requestNewGame(2);
    }
    
    public Client(String ip, int port, String name) throws UnknownHostException, IOException {
        this.name = name;
        socket = new Socket(ip, port);
        peer = new Connection(socket, this);
        
        authenticator = new Authenticator();
        authenticator.login(name, "Ouleid9E");
        peer.start();
    }
    
    public void sendCommand(String cmd, String...parameters) {
        printMessage("sendCommand()\t" + cmd + " " + Util.concat(parameters));
        peer.write(cmd, parameters);
    }
    
    /**
     * Login to game server
     */
    private void login() {
        sendCommand("LOGIN", this.name);
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
            case("HELLO"):
                System.out.println("Logged in!");
                sendCommand("HELLO", "D");
                break;
            case ("START"): //  START [Bob, Alice, Lol]
                Player[] players = new Player[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    Player player = new Player(Tile.values()[i + 1], parameters[i]);
                    players[i] = player;
                    
                    if(player.getName().equals(name)) this.player = player;
                }
                
                if(player == null) System.out.println("Controlled player not found?!");
                
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
                System.out.println("ERROR: "+Util.concat(parameters));
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
}
