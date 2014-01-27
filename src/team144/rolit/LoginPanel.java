package team144.rolit;

import java.awt.Button;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import team144.rolit.network.Client;
import team144.rolit.network.Client.ClientListener;
import team144.rolit.network.Server;
import team144.util.Util;

import com.esotericsoftware.tablelayout.swing.Table;

public class LoginPanel extends Panel implements ActionListener, ClientListener{
    private static final float TEXTFIELD_WIDTH = 200f;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField ipField;
    private JComboBox<GameType> gameTypeBox;
    
    private Label infoLabel;
    
    private Client client;

    private final JFrame frame;
    
    public LoginPanel(JFrame frame) {
        this.frame = frame;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        Table table = new Table();
        add(table);
        
        table.addCell(new Label("Username:"));
        usernameField = new JTextField("test1");
        table.addCell(usernameField).width(TEXTFIELD_WIDTH);
        table.row();
        
        table.addCell(new Label("Password:"));
        passwordField = new JPasswordField("test1");
        table.addCell(passwordField).width(TEXTFIELD_WIDTH);
        table.row();
        
        table.addCell(new Label("Server IP:"));
        ipField = new JTextField("127.0.0.1");
        table.addCell(ipField).width(TEXTFIELD_WIDTH);
        table.row();
        
        table.addCell(new Label("Game Type:"));
        gameTypeBox = new JComboBox<GameType>(GameType.values());
        table.addCell(gameTypeBox).width(TEXTFIELD_WIDTH);
        table.row();
        
        Button startButton = new Button("Start Game");
        startButton.addActionListener(this);
        table.addCell(startButton).colspan(2);
        table.row();
        
        infoLabel = new Label();
        table.addCell(infoLabel).colspan(2);
    }
    
    private void setInfoText(String message){
        infoLabel.setText(message);
        validate();
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        try {
            String ipInput = ipField.getText();
            
            String ip;
            int port;
            
            if (ipInput.contains(":")) {
                String[] split = ipInput.split(":");
                
                ip = split[0];
                port = Integer.parseInt(split[1]);
            } else {
                ip = ipInput;
                port = Server.DEFAULT_PORT;
            }
            
            if(!Util.isValidName(usernameField.getText())){
                setInfoText("Invalid name");
                return;
            }
            
            setInfoText("Waiting for server response");
            client = new Client(ip, port, usernameField.getText());
            client.setClientListener(this);
            client.login(passwordField.getText());
            //now wait for onHello() or loginError()
            
            //TODO: goede error messages
        } catch (UnknownHostException e1) {
            setInfoText("Unknown host");
        } catch (IOException e) {
            setInfoText("Something went wrong");
        } catch (Exception e) {
            setInfoText("Something went wrong");
        }
    }
    
    private static enum GameType{
        TWO_PLAYERS("Two Players", "H"), THREE_PLAYERS("Three Players", "I"), FOUR_PLAYERS("Four Players", "J"), CHALLENGE_PLAYER("Challenge Player", "C");
        
        private String uiName, protocolName;

        private GameType(String uiName, String protocolName) {
            this.uiName = uiName;
            this.protocolName = protocolName;
        }
        
        @Override
        public String toString() {
            return uiName;
        }
    }

    @Override
    public void onHello(String flag) {
        setInfoText("Login successful! Server supports the "+flag);
        
        client.requestNewGame(((GameType)gameTypeBox.getSelectedItem()).protocolName);
    }

    @Override
    public void gameReady() {
         frame.setContentPane(new RolitView(client.getGame(), client));
         frame.validate();
    }
    
    @Override
    public void loginError() {
      setInfoText("Login failed! Please try again...");
      client.shutdown();
    }
}
