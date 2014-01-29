package team144.rolit.network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

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
    
    public void sendCommand(String combined){
        printMessage("sendCommand()\t" + combined);
        peer.write(combined);
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
    
    public void requestNewGame(String...flags) {
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
                }
                
                if (player == null) System.out.println("Controlled player not found?!");
                
                game = new Game(players);
                clientListener.gameReady();
                
                sendCommand("LOL");
                break;
            case("GTURN"): //GTURN player
                System.out.println("GTURN"+parameters[0]);
                clientListener.onTurn(peer, parameters[0]);
                break;
            case ("GMOVE"): //GMOVE x y
                int x = Integer.parseInt(parameters[1]);
                int y = Integer.parseInt(parameters[2]);
                game.makeMove(parameters[0], x, y);
                break;
            case ("BCAST"): //BCAST text text to client text
//                controller.showMessage(Util.concat(parameters));
                System.out.println(Util.concat(parameters));
                break;
            case("CHATM"):
                clientListener.chatMessage(parameters);
                break;
            case ("ERROR"):
                if (clientListener != null) {
                    clientListener.loginError();
                }
                System.out.println("ERROR: " + Util.concat(parameters));
                break;
            case ("LJOIN"):
                clientListener.lobbyJoin(parameters[0]);
                break;
            case ("LEAVE"):
                clientListener.leave(parameters[0]);
                break;
            case ("PLIST"):
                clientListener.playerList(parameters);
                break;
            case ("INVIT"):
                if(parameters[0].equals("R")){
                    if(JOptionPane.showConfirmDialog(null, "Accept invitation from " + parameters[1] + "?") == JOptionPane.OK_OPTION){
                        sendCommand("INVIT", "A");
                    }else{
                        sendCommand("INVIT", "D");
                    }
                }else{
                    if(parameters[0].equals("F")) JOptionPane.showMessageDialog(null, "Invitation failed");
                    else if(parameters[0].equals("D")) JOptionPane.showMessageDialog(null, "Invitation denied");
                    System.out.println("?");
                }
            break;
            case ("PROTO"):
                sendCommand("PROTO", Info.NAME, Info.VERSION);
                break;
            case ("SINFO"):
                sendCommand("SINFO", Info.PROGRAM_NAME, Info.PROGRAM_VERSION);
            break;
            case ("ALIVE"):
                break;
            case("SCORE"):
                break;
        }
        
        return false;
    }
    
    @Override
    public void endConnection(Connection c) {
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
        
        public void playerList(String[] players);

        public void leave(String player);

        public void lobbyJoin(String player);

        public void gameReady();
        
        //from player - message
        public void chatMessage(String[] message);

        //public void error(String message);
        public void loginError();

        void onTurn(Connection conn, String player);
    }
}
