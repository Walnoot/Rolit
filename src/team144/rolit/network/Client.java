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
    private Peer peer;
    private Authenticator authenticator;
    private String name;
    private Game game;
    
    public static void main(String[] args) throws UnknownHostException, IOException {
        Client client = new Client("127.0.0.1", Server.DEFAULT_PORT, "player_willemsiers");
        
        client.login();
//        client.requestNewGame(2);
    }
    
    public void sendCommand(String cmd, String...parameters) {
        printMessage("sendCommand()\t" + cmd + " " + Util.concat(parameters));
        peer.write(cmd, parameters);
    }
    
    public Client(String ip, int port, String name) throws UnknownHostException, IOException {
        this.name = name;
        socket = new Socket(ip, port);
        peer = new Peer(socket, this);
        
        authenticator = new Authenticator();
        authenticator.login(name, "Ouleid9E");
        peer.start();
    }
    
    /**
     * Login to game server
     */
    private void login() {
        sendCommand("LOGIN", this.name);
    }
    
    private void requestNewGame(int numPlayers) {
        sendCommand("NGAME", "S" + Integer.toString(numPlayers));
    }
    
    private void printMessage(String m) {
        System.out.println(name + ":\t" + m);
    }
    
    @Override
    public boolean executeCommand(String cmd, String[] parameters, Peer peer) {
        printMessage("ExecuteCommand()\t" + cmd + " " + Util.concat(parameters));
        switch (cmd) {
            case ("VSIGN"): //   VSIGN TEXT
                System.out.println("text "+ parameters[0]);
                String signature = authenticator.signMessage(parameters[0]);
                sendCommand("VSIGN", signature);
                break;
            case ("START"): //  START [Bob, Alice, Lol]
                Player[] players = new Player[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    players[i] = new Player(Tile.values()[i + 1], parameters[i]);
                }
                
                game = new Game(players);
//                JFrame frame = new JFrame("CLIENT");
//                RolitView view = new RolitView(game, this);
//                setController(view.getController());
//                frame.add(view);
//                frame.setSize(500, 500);
//                frame.setVisible(true);
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
        }
        
        return false;
    }
    
    public Game getGame() {
        return game;
    }
    
    @Override
    public String getName() {
        return name;
    }
}
