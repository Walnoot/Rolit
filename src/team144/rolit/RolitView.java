package team144.rolit;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;

import team144.rolit.network.Client;

public class RolitView implements Observer {
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final String FRAME_TITLE = "Rolit";

	private Button[] buttonArray = new Button[Board.DIMENSION * Board.DIMENSION];
	private Label playerTurnLabel;
	
	public RolitView(final Game game, final Client client) {
		JFrame frame = new JFrame(FRAME_TITLE);

		frame.getContentPane().setLayout(new BorderLayout());

		Panel playPanel = new Panel();
		playPanel.setLayout(new GridLayout(Board.DIMENSION, Board.DIMENSION));
		frame.add(playPanel, BorderLayout.CENTER);

		for (int i = 0; i < Board.DIMENSION * Board.DIMENSION; i++) {
			Button button = new Button();
			button.addActionListener(new RolitController(game, client));
			button.setBackground(Tile.EMPTY.getColor());
			buttonArray[i] = button;
			playPanel.add(button);
		}

		playerTurnLabel = new Label("test");
		frame.add(playerTurnLabel, BorderLayout.NORTH);

		Panel textPanel = new Panel();
		textPanel.setLayout(new BorderLayout());
		frame.add(textPanel, BorderLayout.SOUTH);

		final TextArea textArea = new TextArea();
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
				client.sendCommand("SHOW",new String[]{message});
				textField.setText(null);
			}
		});

		// update initial state
		update(game, null);

		// set frame size, position, and close operation
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	@Override
	public void update(Observable observable, Object arg) {
		if (observable instanceof Game) {
			Game game = (Game) observable;

			for (int i = 0; i < buttonArray.length; i++) {
				Tile tile = game.getBoard().getTile(i);
				buttonArray[i].setLabel(tile.name());
				buttonArray[i].setBackground(tile.getColor());
			}

			playerTurnLabel.setText(game.getCurrentPlayer().getTile().name());
		} else {
			throw new IllegalStateException("Can only observe a Game object");
		}
	}

	private class RolitController implements ActionListener {
		private Game game;
		private Client client;

		public RolitController(Game game, Client client) {
			this.game = game;
			this.client = client;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			for (int i = 0; i < buttonArray.length; i++) {
				if (buttonArray[i] == event.getSource())
					game.makeMove(game.getCurrentPlayer(), i);
			}
		}
	}

	public static void main(String[] args) {
		Game game = new Game(new Player(Tile.BLUE, "henk"), new Player(Tile.GREEN, "niet henk"), new Player(Tile.RED,
				"sdfsd"), new Player(Tile.YELLOW, "afgsadf"));
		RolitView rolitView = new RolitView(game,null);
		game.addObserver(rolitView);
	}
}
