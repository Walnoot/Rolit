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

public class RolitView extends Panel implements Observer {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String FRAME_TITLE = "Rolit";
    
    private Button[] buttonArray = new Button[Board.DIMENSION * Board.DIMENSION];
    private Label infoLabel;
    
    public RolitView(Game game) {
//        JFrame frame = new JFrame(FRAME_TITLE);
        game.addObserver(this);
        
        setLayout(new BorderLayout());
        
        Panel playPanel = new Panel();
        playPanel.setLayout(new GridLayout(Board.DIMENSION, Board.DIMENSION));
        add(playPanel, BorderLayout.CENTER);
        
        for (int i = 0; i < Board.DIMENSION * Board.DIMENSION; i++) {
            Button button = new Button();
            button.addActionListener(new RolitController(game));
            button.setBackground(Tile.EMPTY.getColor());
            buttonArray[i] = button;
            playPanel.add(button);
        }
        
        infoLabel = new Label("test");
        add(infoLabel, BorderLayout.NORTH);
        
        Panel textPanel = new Panel();
        textPanel.setLayout(new BorderLayout());
        add(textPanel, BorderLayout.SOUTH);
        
        final TextArea textArea = new TextArea();
//        textArea.setEditable(false);
        textArea.setFocusable(false);
        textPanel.add(textArea, BorderLayout.CENTER);
        
        final TextField textField = new TextField();
        textPanel.add(textField, BorderLayout.SOUTH);
        
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                textArea.append(textField.getText() + "\n");
                textField.setText(null);
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
    
    private class RolitController implements ActionListener {
        private Game game;
        
        public RolitController(Game game) {
            this.game = game;
        }
        
        @Override
        public void actionPerformed(ActionEvent event) {
            for (int i = 0; i < buttonArray.length; i++) {
                if (buttonArray[i] == event.getSource()) {
                    if (game.isValidMove(i)) game.makeMove(game.getCurrentPlayer(), i);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        Game game = new Game(new Player(Tile.BLUE, "Michiel"), new Player(Tile.GREEN, "Willem"));
        RolitView rolitView = new RolitView(game);
        
        JFrame frame = new JFrame(FRAME_TITLE);
        
//        frame.setContentPane(rolitView);
        frame.setContentPane(new LoginPanel());
        
        //set frame size, position, and close operation
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
