package team144.rolit;

import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Panel;

import javax.swing.JFrame;

public class RolitView {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String FRAME_TITLE = "Rolit";
    
    public RolitView() {
        JFrame frame = new JFrame(FRAME_TITLE);
        
        Panel panel = new Panel();
        panel.setLayout(new GridLayout(Board.DIMENSION, Board.DIMENSION));
        frame.add(panel);
        
        for (int i = 0; i < Board.DIMENSION * Board.DIMENSION; i++) {
            panel.add(new Button("LOL ROLIT"));
        }
        
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        new RolitView();
    }
}
