package team144.rolit.network;

import java.net.Socket;

import team144.util.Util;

public class Client implements NetworkListener {

	private Socket socket;
	private Peer peer;
	private String name;

	public static void main(String[] args) {
		Client client = new Client("127.0.0.1", 1337, "Michiel");
		client.sendCommand("SHOW", new String[] {"halloooo"});
	}
	
	private void sendCommand(String cmd, String[] parameters) {
		printMessage("sendCommand()\t"+cmd+" "+Util.concat(parameters));
		peer.write(cmd, parameters);
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
	}

	private void printMessage(String m) {
		System.out.println(name + ":\t" + m);
	}

	@Override
	public boolean executeCommand(String cmd, String[] parameters) {
		printMessage("ExecuteCommand()\t"+cmd+" "+Util.concat(parameters));
		switch (cmd) {
			case ("MSG"): {
				printMessage(parameters[0]);
				break;
			}
		}
		return false;
	}
	
}
