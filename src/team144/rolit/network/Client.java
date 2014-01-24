package team144.rolit.network;

import java.net.Socket;

import javax.swing.JFrame;

import team144.rolit.Game;
import team144.rolit.Player;
import team144.rolit.RolitView;
import team144.rolit.RolitView.RolitController;
import team144.rolit.Tile;
import team144.util.Util;

public class Client implements NetworkListener {
    
    private Socket socket;
    private Peer peer;
    private Authenticator authenticator;
    private String name;
    private RolitController controller;
    private RolitView view;
    
    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 1337, "player_willemsiers");
        client.login();
//        client.requestNewGame(2);
    }
    
    public void sendCommand(String cmd, String[] parameters) {
        printMessage("sendCommand()\t" + cmd + " " + Util.concat(parameters));
        peer.write(cmd, parameters);
    }
    
    public void sendCommand(String cmd, String parameter) {
        System.out.println("ExecuteCommand()\t"+cmd+" "+parameter);
        sendCommand(cmd, new String[] { parameter });
    }
    
    public Client(String ip, int port, String name) {
        try {
            this.name = name;
            socket = new Socket(ip, port);
            peer = new Peer(socket, this);
            
            authenticator = new Authenticator();
            authenticator.login("player_willemsiers","Ouleid9E");
            peer.start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void login(){
        sendCommand("LOGIN", name);
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
                String signature = authenticator.signMessage(parameters[0]);
                sendCommand("VSIGN", signature);
                break;
            case ("START"): //  START [Bob, Alice, Lol]
                Player[] players = new Player[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    players[i] = new Player(Tile.values()[i + 1], parameters[i]);
                }
                Game game = new Game(players);
                JFrame frame = new JFrame("CLIENT");
                view = new RolitView(game, this);
                controller = view.getController();
                frame.add(view);
                frame.setSize(500, 500);
                frame.setVisible(true);
                break;
            case ("GMOVE"): //GMOVE x y
                int x = Integer.parseInt(parameters[0]);
                int y = Integer.parseInt(parameters[1]);
                controller.makeMove(x, y);
                break;
            case ("BCAST"): //BCAST text text to client text
                view.showMessage(Util.concat(parameters));
                break;
        }
        
        return false;
    }
    
    @Override
    public String getName() {
        return name;
    }
}
