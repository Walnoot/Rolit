package team144.rolit.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;

/**
 * Handles writing to- and receiving from {@link NetworkListener}s, but delegates further handling to the {@link NetworkListener}.
 * 
 * @author Willem
 */
public class Connection extends Thread {
	
	/**
	 * Separates commands from parameters
	 */
	private static final String SEPERATOR = " ";
	
	/**
	 * socket can be written to or read from if it receives data
	 */
	private Socket socket;
	/**
	 * listener can be used to actually handle the commands
	 */
	private NetworkListener listener;
	
	/**
	 * Output Stream
	 */
	private BufferedWriter out;
	
	/**
	 * Input Stream
	 */
	private BufferedReader in;
	
	/**
	 * whether or not the connection is open
	 */
	private boolean running;
	
	/**
	 * Creates a Connection between both ends of {@link Socket}
	 * 
	 * @param socket
	 * - connection between two clients
	 * @param listener
	 * - receives updates if socket receives something
	 */
	public Connection(Socket socket, NetworkListener listener) {
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
	
	/**
	 * Listens on the socket for data being received while running
	 */
	@Override
	public void run() {
		try {
			running = true;
			while (running) {
				synchronized (in) {
					String message;
					while ((message = in.readLine()) != null) {
						String[] contents = deConcatMessage(message);
						running =
							listener.executeCommand(contents[0], Arrays.copyOfRange(contents, 1, contents.length), this);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			running = false;
			
			listener.endConnection(this);
			
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e) {
				//could happen, but is not important because the stream would close anyway
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Splits a message in [command, parameters...] format
	 * 
	 * @param message
	 * - a space separated message in format "COMMAND par1... parN"
	 * @return a String array with the first element being the command and the rest are the parameters
	 */
	private String[] deConcatMessage(String message) {
		String[] contents = message.split(SEPERATOR);
		String[] parameters = new String[contents.length - 1];
		for (int i = 1; i < contents.length; i++) {
			parameters[i - 1] = contents[i];
		}
		return contents;
	}
	
	/**
	 * Concat's a command and it's parameters
	 * 
	 * @param cmd
	 * - Command from the protocol
	 * @param message
	 * - parameters matching cmd
	 * @return a String to be written to the other side of the {@link Socket}
	 */
	private String concatMessage(String cmd, String[] message) {
		if (message.length == 0) return cmd;
		
		String result = cmd + " " + message[0];
		for (int i = 1; i < message.length; i++) {
			result += " " + message[i];
		}
		return result;
		
	}
	
	/**
	 * Writes a Command with parameters to the other side of the Socket
	 * 
	 * @param cmd
	 * - a command, preferably from the protocol
	 * @param parameters
	 * - parameters matching this command
	 */
	public void write(String cmd, String... parameters) {
		String output = concatMessage(cmd, parameters);
		write(output);
	}
	
	/**
	 * Writes a Command with parameters to the other side of the Socket
	 * 
	 * @param cmd
	 * - a command, preferably from the protocol, and matching parameters
	 */
	public void write(String combined) {
		try {
			out.write(combined + System.lineSeparator());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * sets whether or not it should keep running
	 */
	public void setRunning(boolean b) {
		running = b;
	}
	
	/**
	 * @return wheter or not it is still running
	 */
	public boolean isRunning() {
		return running;
	}
}
