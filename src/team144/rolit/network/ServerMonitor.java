package team144.rolit.network;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import team144.rolit.Game;
import team144.util.Util;

public class ServerMonitor{
	
	Game game;
	
	public static void main(String[] args) {
		new ServerMonitor(null);
	}
	
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final String FRAME_TITLE = "Server Monitor";
	final TextArea textArea;

	public ServerMonitor(final Server server) {
		JFrame frame = new JFrame(FRAME_TITLE);

		frame.getContentPane().setLayout(new BorderLayout());

		Panel textPanel = new Panel();
		textPanel.setLayout(new BorderLayout());
		frame.add(textPanel, BorderLayout.CENTER);

		textArea = new TextArea();
		// textArea.setEditable(false);
		textArea.setFocusable(false);
		textPanel.add(textArea, BorderLayout.CENTER);

		final TextField textField = new TextField();
		textPanel.add(textField, BorderLayout.SOUTH);

		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String message = textField.getText();
				textArea.append(message + "\n");
				server.sendCommand("SHOW",new String[]{message});
				textField.setText(null);
			}
		});

		// set frame size, position, and close operation
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void executeCommand(String cmd, String[] parameters) {
		textArea.append(cmd+ " " +Util.concat(parameters) + "\n");
//		if(cmd.equals("MOVE" ){
//		game.makeMove(parameters[0], parameters[1]);
//		}
	}
}
