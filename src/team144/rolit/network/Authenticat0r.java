package team144.rolit.network;

import java.net.InetAddress;
import java.net.Socket;

import team144.util.Util;

public class Authenticat0r implements NetworkListener {

	private Socket socket;
	private Peer peer;
	private String name;

	public static void main(String[] args) {
		Authenticat0r auth = new Authenticat0r("lololo");
		auth.idPlayer("willem", "wiwawo");
	}

	public Authenticat0r(String name) {
		try {
			this.name = name;
			InetAddress address = InetAddress.getByName("ss-security.student.utwente.nl");
			socket = new Socket(address, 2013);
			System.out.println(socket.isConnected());
			peer = new Peer(socket, this);
			peer.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printMessage(String m) {
		System.out.println(name + ":\t" + m);
	}
	
	private void idPlayer(String name, String password) {
		sendCommand("IDPLAYER",new String[]{name,password});
	}

	private void sendCommand(String cmd, String[] parameters) {
		peer.write(cmd, parameters);
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
