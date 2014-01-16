package team144.rolit.network;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private Socket socket;
	private ServerSocket serverSocket;

	public Server(int port) {
		try {
			serverSocket = new ServerSocket(port);
			socket = serverSocket.accept();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
