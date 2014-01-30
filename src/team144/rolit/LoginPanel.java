package team144.rolit;

import java.awt.Button;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import team144.rolit.network.Client;
import team144.rolit.network.Client.ClientListener;
import team144.rolit.network.Connection;
import team144.rolit.network.Server;
import team144.util.Util;

import com.esotericsoftware.tablelayout.swing.Table;

public class LoginPanel extends Panel implements ActionListener, ClientListener {
	private static final long serialVersionUID = 6951020372025683118L;
	
	private static final float TEXTFIELD_WIDTH = 200f;
	
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JTextField ipField;
//    private JComboBox<GameType> gameTypeBox;
	
	private Label infoLabel;
	
	private Client client;
	
	private final JFrame frame;
	
	public LoginPanel(JFrame frame) {
		this.frame = frame;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		Table table = new Table();
		add(table);
		
		table.addCell(new Label("Username:"));
		usernameField = new JTextField("test1");
		usernameField.addActionListener(this);
		table.addCell(usernameField).width(TEXTFIELD_WIDTH);
		table.row();
		
		table.addCell(new Label("Password:"));
		passwordField = new JPasswordField("test1");
		passwordField.addActionListener(this);
		table.addCell(passwordField).width(TEXTFIELD_WIDTH);
		table.row();
		
		table.addCell(new Label("Server IP:"));
		ipField = new JTextField("127.0.0.1:" + Server.DEFAULT_PORT);
		ipField.addActionListener(this);
		table.addCell(ipField).width(TEXTFIELD_WIDTH);
		table.row();
		
		Button startButton = new Button("Start Game");
		startButton.addActionListener(this);
		table.addCell(startButton).colspan(2);
		table.row();
		
		infoLabel = new Label();
		table.addCell(infoLabel).colspan(2);
	}
	
	private void setInfoText(String message) {
		infoLabel.setText(message);
		validate();
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		try {
			String ipInput = ipField.getText();
			
			String ip;
			int port;
			
			if (ipInput.contains(":")) {
				String[] split = ipInput.split(":");
				
				ip = split[0];
				port = Integer.parseInt(split[1]);
			} else {
				ip = ipInput;
				port = Server.DEFAULT_PORT;
			}
			
			if (!Util.isValidName(usernameField.getText())) {
				setInfoText("Invalid name");
				return;
			}
			
			setInfoText("Waiting for server response");
			client = new Client(ip, port, usernameField.getText());
			client.setClientListener(this);
			client.login(passwordField.getText());
			//now wait for onHello() or loginError()
			
		} catch (UnknownHostException e1) {
			setInfoText("Unknown host");
		} catch (Exception e) {
			setInfoText(e.getMessage());
		}
	}
	
	/**
	 * Called if the server has logged in the user successfully
	 */
	@Override
	public void onHello(String flag) {
		setInfoText("Login successful! Server supports the " + flag);
		
		frame.setContentPane(new LobbyPanel(frame, client));
		frame.validate();
	}
	
	@Override
	public void closeGame(Client client, boolean gameOver, Game game) {
	}
	
	@Override
	public void shutDown() {
	}
	
	@Override
	public void lobbyJoin(String player) {
	}
	
	@Override
	public void leave(String player) {
	}
	
	@Override
	public void playerList(String[] players) {
	}
	
	public void gameReady() {
	}
	
	@Override
	public void loginError() {
		setInfoText("Login failed! Please try again...");
		client.shutdown();
	}
	
	@Override
	public void chatMessage(String[] message) {
	}
	
	@Override
	public void onTurn(Connection conn, int player) {
	}
}
