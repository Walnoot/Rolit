package team144.rolit.network;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JFrame;

import team144.util.Util;

public class ServerMonitor {
	public static void main(String[] args) {
		new ServerMonitor(null);
	}
	
	private static final int WIDTH = 400;
	private static final int HEIGHT = 200;
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
				String[] contents = message.split(" ");
				server.sendCommandToAll(contents[0], Arrays.copyOfRange(contents, 1, contents.length));
//              server.sendCommand("SHOW", new String[] { message });
				textField.setText(null);
			}
		});
		
		// set frame size, position, and close operation
		frame.setSize(WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public void showCommand(String cmd, String... parameters) {
		textArea.append(cmd + " " + Util.concat(parameters) + "\n");
	}
}
