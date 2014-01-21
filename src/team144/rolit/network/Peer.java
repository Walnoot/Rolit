package team144.rolit.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;

public class Peer extends Thread {
	private static final String SEPERATOR = " ";

	private Socket socket;
	private NetworkListener listener;
	private BufferedWriter output;
	private BufferedReader input;

	public Peer(Socket socket, NetworkListener listener) {
		this.listener = listener;
		this.socket = socket;
		try {
			output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			boolean running = true;
			while (running) {
				String message = input.readLine();
				String[] contents = decryptMessage(message);
				running = listener.executeCommand(contents[0], Arrays.copyOfRange(contents, 1, contents.length));
			}
		} catch (IOException e) {
			terminate();
			e.printStackTrace();
		}
		terminate();
		super.run();
	}

	private String[] decryptMessage(String message) {
		String[] contents = message.split(SEPERATOR);
		String[] parameters = new String[contents.length - 1];
		for (int i = 1; i < contents.length; i++) {
			parameters[i - 1] = contents[i];
		}
		return contents;
	}

	public void terminate() {
		try {
			input.close();
			output.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
