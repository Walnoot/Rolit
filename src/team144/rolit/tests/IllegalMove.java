//todo: maak tests

//package team144.rolit.tests;
//
//import javax.swing.JFrame;
//
//import team144.rolit.Game;
//import team144.rolit.Player;
//import team144.rolit.RolitView;
//import team144.rolit.Tile;
//import team144.rolit.network.Client;
//import team144.rolit.network.Server;
//
//public class IllegalMove {
//	
//	public static void main(String[] args) {
//		Client alice = null;
//		Client bob = null;
//		final Server server = n;
//		/*
//		 * setup
//		 */{
//			try {
//				new Thread(new Runnable() {
//					
//					@Override
//					public void run() {
//						server.accept();
//					}
//				})
//				server = new Server(1969);
//				alice = new Client("127.0.0.1", 1969, "Alice");
//				bob = new Client("127.0.0.1", 1969, "Bob");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//			Game game = new Game(new Player(Tile.RED, alice.getName()), new Player(Tile.BLUE, bob.getName()));
//			
//			RolitView aliceView = new RolitView(game, alice);
//			RolitView bobView = new RolitView(game, bob);
//
//			JFrame aliceFrame = new JFrame("ClientAlice");
//			aliceFrame.setContentPane(aliceView);
//			aliceFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			System.out.println(aliceFrame.getSize().width);
//			aliceFrame.setLocationRelativeTo(null);
//			aliceFrame.setSize(500, 600);
//			
//			System.out.println(aliceFrame.getSize().width);
//			aliceFrame.setVisible(true);
//			System.out.println(aliceFrame.getSize().width);
//			
//		}
//	}
//}
