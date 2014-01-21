package team144.rolit.network;

import java.net.ServerSocket;
import java.net.Socket;

import team144.util.Util;

public class Server implements NetworkListener {

	private Socket socket;
	private ServerSocket serverSocket;
	private Peer listener;
	private String name;
	
	public static void main(String[] args) {
		Server server = new Server(1337, "Willem");
	}

	public Server(int port, String name) {
		try {
			this.name = name;
			serverSocket = new ServerSocket(port);
			socket = serverSocket.accept();
			printMessage("Client connected");
			listener = new Peer(socket, this);
			listener.start();
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
		case ("SHOW"): {
			printMessage(parameters[0]);
			break;
		}
		}

		return false;
	}

}
