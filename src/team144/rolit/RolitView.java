package team144.rolit;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;

import team144.rolit.network.Client;
import team144.rolit.network.Client.ClientListener;
import team144.rolit.network.Connection;
import team144.rolit.network.Info;
import team144.util.Util;

public class RolitView extends Panel implements Observer, ClientListener {
	private static final long serialVersionUID = 4223398494630548955L;
	
	private static final int WIDTH = 500;
	private static final int HEIGHT = 700;

	private static final boolean COOLMODE = true;
	
	private Button[] buttonArray = new Button[Board.DIMENSION * Board.DIMENSION];
	private ViewApplication application;
	
	private final JFrame frame;
	private TextArea textArea;
	private TextField textField;
	private Label infoLabel;
	
	/**
	 * The Player of this process.
	 */
	private final Player player;


	
	public RolitView(JFrame frame, Game game, Client client) {
		this(frame, game, client, false);
	}
	
	public RolitView(JFrame frame, Game game, final Client client, boolean isBot) {
		this.frame = frame;
		player = client.getPlayer();
		if (isBot) {
			player.setStrategy(new RecursiveStrategy());
//            player.setStrategy(new RandomStrategy());
		}
		
		game.addObserver(this);
		
		setLayout(new BorderLayout());
		
		if(COOLMODE){
			application = new ViewApplication(game, client);
			add(application.getCanvas(), BorderLayout.CENTER);
		}else{
			Panel playPanel = new Panel();
			playPanel.setLayout(new GridLayout(Board.DIMENSION, Board.DIMENSION));
			add(playPanel, BorderLayout.CENTER);
			
			RolitController controller = new RolitController(game, client);
			
			for (int i = 0; i < Board.DIMENSION * Board.DIMENSION; i++) {
				Button button = new Button();
				button.addActionListener(controller);
				button.setBackground(Tile.EMPTY.getColor());
				buttonArray[i] = button;
				playPanel.add(button);
			}
		}
		
		infoLabel = new Label();
		add(infoLabel, BorderLayout.NORTH);
		
		Panel textPanel = new Panel();
		textPanel.setLayout(new BorderLayout());
		add(textPanel, BorderLayout.SOUTH);
		
		textArea = new TextArea();
//        textArea.setEditable(false);
		textArea.setFocusable(false);
		textPanel.add(textArea, BorderLayout.CENTER);
		
		textField = new TextField();
		textPanel.add(textField, BorderLayout.SOUTH);
		
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String text = textField.getText();
				if (text.startsWith("/")) {
					text = text.subSequence(1, text.length()).toString();
					String[] parsed = text.split(" ");
					client.sendCommand(parsed[0], Arrays.copyOfRange(parsed, 1, parsed.length));
				} else {
					client.sendCommand("CHATM", text);
				}
				textField.setText(null);
			}
		});
		
		//update initial state
		update(game, null);
		client.setClientListener(this);
	}
	
	public void showMessage(String msg) {
		textArea.append(msg + "\n");
	}
	
	@Override
	public void update(Observable observable, Object arg) {
		if (observable instanceof Game) {
			Game game = (Game) observable;
			
			if(COOLMODE){
				application.update();
			}else{
				for (int i = 0; i < buttonArray.length; i++) {
					Tile tile = game.getBoard().getTile(i);
					if (tile == Tile.EMPTY && player == game.getCurrentPlayer()) buttonArray[i].setBackground(game
							.isValidMove(i) ? Color.LIGHT_GRAY : Tile.EMPTY.getColor());
					else buttonArray[i].setBackground(tile.getColor());
				}
			}
			
			if (game.isGameOver()) {
				infoLabel.setText("Game over");
			} else {
				String currentPlayer =
					game.getCurrentPlayer() == player ? "Your" : game.getCurrentPlayer().getName() + "'s";
				
				infoLabel.setText(String
						.format("%s turn (%s)", currentPlayer, game.getCurrentPlayer().getTile().name()));
			}
			
		} else {
			throw new IllegalStateException("Can only observe a Game object");
		}
	}
	
	public class RolitController implements ActionListener {
		private Game game;
		private Client client;
		
		public RolitController(Game game, Client client) {
			this.game = game;
			this.client = client;
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			for (int i = 0; i < buttonArray.length; i++) {
				if (buttonArray[i] == event.getSource()) {
//					if (client.getPlayer() == game.getCurrentPlayer() && game.isValidMove(i)) {
//						Board board = game.getBoard();
//						client.sendCommand("GMOVE", Integer.toString(board.getX(i)), Integer.toString(board.getY(i)));
//					}
					
					player.trySendMove(client, i);
				}
			}
		}
		
		public void makeMove(int x, int y) {
			Board board = game.getBoard();
			game.makeMove(game.getCurrentPlayer().getName(), board.getIndex(x, y));
		}
	}
	
	public static void main(String[] args) {
//        Game game = new Game(new Player(Tile.BLUE, "Michiel"), new Player(Tile.GREEN, "Willem"));
//        RolitView rolitView = new RolitView(game, null);
		
		JFrame frame = new JFrame(Info.PROGRAM_NAME);
		
//        frame.setContentPane(rolitView);
		frame.setContentPane(new LoginPanel(frame));
//        frame.setContentPane(new LobbyPanel(frame));
		
		//set frame size, position, and close operation
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	@Override
	public void onHello(String flag) {
	}
	
	@Override
	public void shutDown() {
		frame.setContentPane(new LoginPanel(frame));
		frame.validate();
	}
	
	/**
	 * comes from GTURN
	 * neccesary to get bots to move
	 */
	@Override
	public void onTurn(Connection conn, int playerIndex) {
		if (player.index == playerIndex) {
			this.player.requestMove(conn);
		}
	}
	
	@Override
	public void closeGame(Client client) {
		frame.setContentPane(new LobbyPanel(frame, client));
		frame.validate();
	}
	
	@Override
	public void playerList(String[] players) {
	}
	
	@Override
	public void leave(String player) {
	}
	
	@Override
	public void lobbyJoin(String player) {
	}
	
	@Override
	public void gameReady() {
	}
	
	@Override
	public void chatMessage(String[] message) {
		textArea.append(message[0] + " says:\t" + Util.concat(Arrays.copyOfRange(message, 1, message.length)) + "\n");
	}
	
	@Override
	public void loginError() {
	}
	
}
