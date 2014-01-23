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

public class RolitView extends Panel implements Observer {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private static final String FRAME_TITLE = "Rolit";
    
    private Button[] buttonArray = new Button[Board.DIMENSION * Board.DIMENSION];
    private TextArea textArea;
    private TextField textField;
    private Label infoLabel;
    private RolitController controller;
    
    public RolitView(Game game, Client client) {
//        JFrame frame = new JFrame(FRAME_TITLE);
        game.addObserver(this);
        
        setLayout(new BorderLayout());
        
        Panel playPanel = new Panel();
        playPanel.setLayout(new GridLayout(Board.DIMENSION, Board.DIMENSION));
        add(playPanel, BorderLayout.CENTER);
        
        controller = new RolitController(game, client, this);
        
        for (int i = 0; i < Board.DIMENSION * Board.DIMENSION; i++) {
            Button button = new Button();
            button.addActionListener(controller);
            button.setBackground(Tile.EMPTY.getColor());
            buttonArray[i] = button;
            playPanel.add(button);
        }
        
        infoLabel = new Label("test");
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
                controller.showMessage(textField.getText());
            }
        });
        
        //update initial state
        update(game, null);
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
            
            if (game.isGameOver()) {
                infoLabel.setText("Game over");
            } else {
                infoLabel.setText(String.format("%s's turn (%s)", game.getCurrentPlayer().getName(), game
                        .getCurrentPlayer().getTile().name()));
            }
            
        } else {
            throw new IllegalStateException("Can only observe a Game object");
        }
    }
    
    public RolitController getController() {
        return controller;
    }
    
    public class RolitController implements ActionListener {
        private Game game;
        private Client client;
        private RolitView view;
        
        public RolitController(Game game, Client client, RolitView rolitView) {
            this.game = game;
            this.client = client;
            this.view = rolitView;
        }
        
        @Override
        public void actionPerformed(ActionEvent event) {
            for (int i = 0; i < buttonArray.length; i++) {
                if (buttonArray[i] == event.getSource()) {
                    if (game.isValidMove(i)) {
                        Board board = game.getBoard();
                        String[] par =
                            new String[] { Integer.toString(board.getX(i)), Integer.toString(board.getY(i)) };
                        client.sendCommand("GMOVE", par);
                    }
                }
            }
        }
        
        public void makeMove(int x, int y) {
            Board board = game.getBoard();
            game.makeMove(game.getCurrentPlayer(), board.getIndex(x, y));
        }
        
        public void showMessage(String msg) {
            view.textArea.append(msg + "\n");
            view.textField.setText(null);
        }
    }
    
    public static void main(String[] args) {
        Game game = new Game(new Player(Tile.BLUE, "Michiel"), new Player(Tile.GREEN, "Willem"));
        RolitView rolitView = new RolitView(game, null);
        
        JFrame frame = new JFrame(FRAME_TITLE);
        
        frame.setContentPane(rolitView);
//        frame.setContentPane(new LoginPanel());
        
        //set frame size, position, and close operation
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
}
