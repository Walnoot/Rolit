package team144.rolit.network;

import java.io.IOException;
import java.net.BindException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import team144.rolit.network.Client.ClientListener;
import team144.util.Util;

public class Server implements NetworkListener {
	
	/**
	 * The default port by protocol for INF-B Rolit
	 */
	public static final int DEFAULT_PORT = 2014;
	
	/**
	 * connections - not logged in yet.
	 */
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	
	/**
	 * authorizedConnections - logged in.
	 */
	private ArrayList<Connection> authorizedConnections = new ArrayList<Connection>();
	
	/**
	 * see: {@link ServerMonitor}.
	 */
	private ServerMonitor monitor;
	
	/**
	 * see: {@link Authenticator}.
	 */
	private Authenticator authenticator;
	
	/**
	 * randomly generated text by Authenticator during logon of a client.
	 */
	private String randomText;
	
	/**
	 * Prompt for a port to start server on, uses {@link DEFAULT_PORT} 2014 if
	 * nothing is specified.
	 * Then instantiates a {@link Server} on the port.
	 * 
	 * @param args
	 *            - ignored
	 */
	public static void main(String[] args) {
		
		int port = 0;
		String input = JOptionPane.showInputDialog("Type port");
		try {
			if (input.trim().equals("")) {
				port = DEFAULT_PORT;
			} else {
				port = Integer.parseInt(input);
			}
			new Server(port);
		} catch (BindException | NumberFormatException f) {
			main(null);
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialize a {@link Server} object for @link Client} to connect to.
	 * Shows a {@link ServerMonitor} to observe commands it receives, and
	 * creates {@link Authenticator} object for the pki-server.
	 * After creation it always listens on specified port for clients to connect
	 * 
	 * @param port
	 *            - port the server listens on
	 * @throws BindException
	 *             - if the specified port is not available
	 * @throws IOException
	 *             - if Server could not be created
	 */
	public Server(int port) throws IOException, BindException {
		monitor = new ServerMonitor(this);
		authenticator = new Authenticator();
		ServerSocket serverSocket = new ServerSocket(port);
		
		monitor.showCommand("Server's ip-address is: ", Inet4Address.getLocalHost().getHostAddress());
		
		while (true) {
			Connection conn = new Connection(serverSocket.accept(), this);
			connections.add(conn);
			conn.start();
		}
	}
	
	/**
	 * Write a command to a specific {@link Client}'s {@link Connection}
	 * 
	 * @param client
	 *            - to who you want to send the command
	 * @param cmd
	 *            - command from the protocol you want to send
	 * @param parameters
	 *            - parameters matching the command
	 */
	public void sendCommand(Connection client, String cmd, String... parameters) {
		client.write(cmd, parameters);
	}
	
	/**
	 * Write a command to connected {@link Client}'s {@link Connection}s
	 * 
	 * @param cmd
	 *            - command from the protocol you want to send
	 * @param parameters
	 *            - parameters matching the command
	 */
	public void sendCommandToAll(String cmd, String... parameters) {
		for (int i = 0; i < connections.size(); i++) {
			sendCommand(connections.get(i), cmd, parameters);
		}
	}
	
	/**
	 * Write a command to all {@link Client}'s {@link Connection} in the
	 * {@link Room} that the {@link Client} is in.
	 * 
	 * @param cmd
	 *            - command from the protocol you want to send
	 * @param client
	 *            - {@link Connection} from who the command comes
	 * @param parameters
	 *            - parameters matching the command
	 */
	public void sendCommandToRoom(Connection client, String cmd, String... parameters) {
		Room.getRoom(client).sendCommand(cmd, parameters);
	}
	
	/**
	 * {@link ClientListener} Handles the commands that are sent to the
	 * {@link Server}.
	 * Preferably delegates it to other methods or classes
	 * 
	 * @param cmd
	 *            - the received command (preferably specified in the protocol)
	 * @param parameters
	 *            - parameters sent along with that command
	 * @param client
	 *            - which {@link Connection} sent this to the server
	 */
	@Override
	public boolean executeCommand(String cmd, String[] parameters, Connection client) {
		monitor.showCommand(cmd, parameters);
		switch (cmd) {
			case ("LOGIN"):
				randomText = Authenticator.generateRandomString(10);
				client.setName(parameters[0]);
				sendCommand(client, "VSIGN", randomText);
				break;
			case ("VSIGN"):
				boolean legit = authenticator.verifySignature("player_" + client.getName(), randomText, parameters[0]);
				if (legit) {
					if (!hasConnectionWithName(client.getName())) {
						authorizedConnections.add(client);
						sendCommand(client, "HELLO", "CL"); //default
						sendCommandToAll("LJOIN", client.getName());
						
						System.out.println("Player " + client.getName() + " logged in.");
					} else {
						sendCommand(client, "ERROR", "An player with that name is already logged in");
					}
				} else {
					sendCommand(client, "ERROR", "Text signed incorrectly");
					System.out.println("Player " + client.getName() + " failed to log in.");
				}
				break;
			case ("NGAME"):
				Room.assignRoom(client, this, cmd, parameters);
				break;
			case ("INVIT"):
				Room.assignRoom(client, this, cmd, parameters);
				break;
			case ("GMOVE"): //GMOVE x y
				Room room = Room.getRoom(client);
				if(room != null){
					int index = room.getGame().findPlayer(client.getName()).index;
					
					sendCommandToRoom(client, cmd, Integer.toString(index), parameters[0], parameters[1]);
				}
				break;
			case ("GTURN"): //GTURN player
				sendCommandToRoom(client, cmd, parameters);
				break;
			case ("CHATM"): //CHATM from message
				String[] params = (client.getName() + " " + Util.concat(parameters)).split(" ");
				if (Room.isInRoom(client)) {
					sendCommandToRoom(client, cmd, params); //to game
				} else{
					sendCommandToAll(cmd, params); //to lobby
				}
				break;
			case ("PLIST"):
				ArrayList<String> playerList = new ArrayList<String>();
				
				for (Connection c : authorizedConnections) {
					if (!Room.isInRoom(c)) playerList.add(c.getName());
				}
				
				sendCommand(client, "PLIST", playerList.toArray(new String[0]));
				break;
			case ("PROTO"):
				sendCommand(client, "PROTO", Info.NAME, Info.VERSION);
				break;
			case ("SINFO"):
				sendCommand(client, "SINFO", Info.PROGRAM_NAME, Info.PROGRAM_VERSION);
				break;
			case ("ALIVE"):
				break;
			case ("BOARD"):
				sendCommandToRoom(client, "BOARD", parameters);
				break;
			default:
				System.out.println("Command not implemented: " + cmd);
				break;
		}
		
		return false;
	}
	
	/**
	 * removes a {@link Connection} from the pool, if this Connection is in a
	 * room, removes it from the Room too.
	 * 
	 * @param c
	 *            - {@link Connection} to remove
	 */
	@Override
	public void endConnection(Connection c) {
		connections.remove(c);
		
		if (authorizedConnections.contains(c)) sendCommandToAll("LEAVE", c.getName());
		
		authorizedConnections.remove(c);
		Room.remove(c);
	}
	
	/**
	 * Returns the {@link Connection} of a player given the name of this player
	 * 
	 * @param name
	 *            - player name, not null.
	 * @return - The {@link Connection} of the player with that name, or null if
	 *         he's
	 *         not online.
	 */
	public Connection getPlayer(String name) {
		for (Connection c : authorizedConnections) {
			if (c.getName().equals(name)) return c;
		}
		return null;
	}
	
	/**
	 * Checks if there already is a {@link Client} connected with a given name.
	 * 
	 * @param name
	 *            - name of the {@link Connection} to check for.
	 * @return <code>true</code> if someone with that name is already connected,
	 *         else returns <code>false</code>.
	 */
	private boolean hasConnectionWithName(String name) {
		for (Connection c : authorizedConnections) {
			if (c.getName().equals(name)) return true;
		}
		return false;
	}
	
	/**
	 * see: {@link NetworkListener} returns the name of the Server
	 */
	@Override
	public String getName() {
		return "server";
	}
}
