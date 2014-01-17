package team144.rolit.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MessageHandler extends Thread {
	private static final String SEPERATOR = " ";

	private Socket socket;
	private BufferedWriter output;
	private BufferedReader input;

	public MessageHandler(Socket socket) {
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
				String[] contents = message.split(SEPERATOR);
				running = executeCommand(contents[0], contents[1].toCharArray()) >= 0;
			}
		} catch (IOException e) {
			terminate();
			e.printStackTrace();
		}
		terminate();
		super.run();
	}

	private int executeCommand(String cmd, char[] flags) {
		switch(cmd){
		
		}
		return 0; //shutdown -> -1
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
