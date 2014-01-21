package team144.rolit.network;

import java.net.Socket;

import team144.rolit.Game;
import team144.rolit.Player;
import team144.rolit.RolitView;
import team144.rolit.Tile;
import team144.util.Util;

public class Client implements NetworkListener {

	private Socket socket;
	private Peer peer;
	private String name;

	public static void main(String[] args) {
		Client client = new Client("127.0.0.1", 1337, "Michiel");
	}

	public void sendCommand(String cmd, String[] parameters) {
		printMessage("sendCommand()\t" + cmd + " " + Util.concat(parameters));
		peer.write(cmd, parameters);
	}

	public void sendCommand(String cmd, String parameter) {
		printMessage("sendCommand()\t" + cmd + " " + parameter);
		peer.write(cmd, new String[] { parameter });
	}

	public Client(String ip, int port, String name) {
		try {
			this.name = name;
			socket = new Socket(ip, port);
			peer = new Peer(socket, this);
			peer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		sendCommand("NGAME", "S3");
	}

	private void printMessage(String m) {
		System.out.println(name + ":\t" + m);
	}

	@Override
	public boolean executeCommand(String cmd, String[] parameters) {
		printMessage("ExecuteCommand()\t" + cmd + " " + Util.concat(parameters));
		switch (cmd) {
		case ("START"): // START [(Bob,RED,F),(Alice,GREEN,F),(PC,BLUE,T)]
			Player[] players = new Player[parameters.length];
			for (int i = 0; i < parameters.length; i ++) {
				players[i] = new Player(Tile.values()[i+1] ,parameters[i]);
			}
			Game game = new Game(players);
			RolitView view = new RolitView(game, this);
			break;
		}

		return false;
	}

}
