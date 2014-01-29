//package team144.rolit.tests;
//
//import javax.swing.JFrame;
//
//import team144.rolit.Game;
//import team144.rolit.LoginPanel;
//import team144.rolit.Player;
//import team144.rolit.RolitView;
//import team144.rolit.Tile;
//import team144.rolit.network.Client;
//import team144.rolit.network.Server;
//
//public class IllegalMove {
//    
//    public static void main(String[] args) {
//        /*
//         * setup
//         */
//        Client alice = new Client("127.0.0.1", Server.DEFAULT_PORT, "Alice");
//        Client bob = new Client("127.0.0.1", Server.DEFAULT_PORT, "Bob");
//        Server server = new Server(Server.DEFAULT_PORT);
//        
//        Game game = new Game(new Player(Tile.RED,alice.getName())   , new Player(Tile.BLUE,bob.getName()));
//        
//        
//        JFrame frame = new JFrame(FRAME_TITLE);
//        
////      frame.setContentPane(rolitView);
//      frame.setContentPane(new LoginPanel(frame));
////      frame.setContentPane(new LobbyPanel(frame));
//      
//      //set frame size, position, and close operation
//      frame.setSize(WIDTH, HEIGHT);
//      frame.setLocationRelativeTo(null);
//      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//      frame.setVisible(true);
//        
//        RolitView aliceView = new RolitView(game, alice);        
//        RolitView bobView = new RolitView(game, bob);       
//        
//        bobView.
//    }
//}
