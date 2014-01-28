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
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;

import team144.rolit.network.Client;

public class RolitView extends Panel implements Observer {
    /**
     * 
     */
    private static final long serialVersionUID = 4223398494630548955L;
    
    private static final int WIDTH = 600;
    private static final int HEIGHT = 800;
    private static final String FRAME_TITLE = "Rolit";
    
    private Button[] buttonArray = new Button[Board.DIMENSION * Board.DIMENSION];
    private TextArea textArea;
    private TextField textField;
    private Label infoLabel;
    
    /**
     * The Player of this process.
     */
    private final Player player;
    
    public RolitView(Game game, Client client) {
        player = client.getPlayer();
        
        game.addObserver(this);
        
        setLayout(new BorderLayout());
        
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
                showMessage(textField.getText());
                textField.setText(null);
            }
        });
        
        //update initial state
        update(game, null);
    }
    
    public void showMessage(String msg) {
        textArea.append(msg + "\n");
    }
    
    @Override
    public void update(Observable observable, Object arg) {
        if (observable instanceof Game) {
            Game game = (Game) observable;
            
            for (int i = 0; i < buttonArray.length; i++) {
                Tile tile = game.getBoard().getTile(i);
                if (tile == Tile.EMPTY && player == game.getCurrentPlayer()) 
                    buttonArray[i].setBackground(game.isValidMove(i) ? Color.LIGHT_GRAY : Tile.EMPTY.getColor());
                else buttonArray[i].setBackground(tile.getColor());
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
                    if (client.getPlayer() == game.getCurrentPlayer() && game.isValidMove(i)) {
                        Board board = game.getBoard();
                        String[] par = new String[] { Integer.toString(board.getX(i)), Integer.toString(board.getY(i)) };
                        client.sendCommand("GMOVE", par);
                    }
                }
            }
        }
        
        public void makeMove(int x, int y) {
            Board board = game.getBoard();
            game.makeMove(game.getCurrentPlayer(), board.getIndex(x, y));
        }
    }
    
    public static void main(String[] args) {
//        Game game = new Game(new Player(Tile.BLUE, "Michiel"), new Player(Tile.GREEN, "Willem"));
//        RolitView rolitView = new RolitView(game, null);
        
        JFrame frame = new JFrame(FRAME_TITLE);
        
//        frame.setContentPane(rolitView);
        frame.setContentPane(new LoginPanel(frame));
//        frame.setContentPane(new LobbyPanel(frame));
        
        //set frame size, position, and close operation
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
}
