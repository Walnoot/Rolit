package team144.rolit;

import java.awt.Button;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import team144.rolit.network.Client;
import team144.rolit.network.Server;

import com.esotericsoftware.tablelayout.swing.Table;

public class LoginPanel extends Panel implements ActionListener {
    private static final float TEXTFIELD_WIDTH = 200f;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField ipField;
    
    private Label infoLabel;
    
    private Client client;

    private final JFrame frame;
    
    public LoginPanel(JFrame frame) {
        this.frame = frame;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        Table table = new Table();
        add(table);
        
        table.addCell(new Label("Username:"));
        usernameField = new JTextField();
        table.addCell(usernameField).width(TEXTFIELD_WIDTH);
        table.row();
        
        table.addCell(new Label("Password:"));
        passwordField = new JPasswordField();
        table.addCell(passwordField).width(TEXTFIELD_WIDTH);
        table.row();
        
        table.addCell(new Label("Server IP:"));
        ipField = new JTextField();
        table.addCell(ipField).width(TEXTFIELD_WIDTH);
        table.row();
        
        Button startButton = new Button("Start Game");
        startButton.addActionListener(this);
        table.addCell(startButton).colspan(2);
        table.row();
        
        infoLabel = new Label();
        table.addCell(infoLabel).colspan(2);
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
            
            client = new Client(ip, port, usernameField.getText());
            
            while (client.getGame() == null) {
                Thread.sleep(100);
            }
            
//            frame.removeAll();
            frame.setContentPane(new RolitView(client.getGame(), client));
            frame.validate();
            
            //TODO: goede error messages
        } catch (UnknownHostException e1) {
            infoLabel.setText("Unknown host");
            validate();
        } catch (IOException e1) {
            infoLabel.setText("Something went wrong");
            validate();
        } catch (Exception e) {
            infoLabel.setText("Something else went wrong");
            validate();
        }
    }
}
