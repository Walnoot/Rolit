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
	private BufferedWriter out;
	private BufferedReader in;

	public Peer(Socket socket, NetworkListener listener) {
		this.listener = listener;
		this.socket = socket;
		setName(listener.getName());
		try {
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			boolean running = true;
			while (running) {
				synchronized (in) {
					String message;
					while ((message = in.readLine()) != null) {
						String[] contents = decryptMessage(message);
						running = listener
								.executeCommand(contents[0], Arrays.copyOfRange(contents, 1, contents.length));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0); //ez
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

	private String encryptMessage(String cmd, String[] message) {
		String result = cmd + " " + message[0];
		for (int i = 1; i < message.length; i++) {
			result += " " + message[i];
		}
		return result;

	}

	public void write(String cmd, String[] parameters) {
		String output = encryptMessage(cmd, parameters);
		try {
//			System.out.println("write: " + output);
			out.write(output + System.lineSeparator());
			out.flush(); // /!!!!!!
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void terminate() {
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
