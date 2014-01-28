package team144.rolit;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JSplitPane;

import team144.rolit.network.Client;
import team144.rolit.network.Client.ClientListener;
import team144.util.Util;

import com.esotericsoftware.tablelayout.swing.Table;

public class LobbyPanel extends Panel implements ActionListener, ClientListener {
    private static final long serialVersionUID = -5449982089768781903L;
    
    private static final int PAD_VALUE = 4;
    
    private final JFrame frame;
    private final Client client;
    
    private Button findGameButton;
    private JComboBox<GameType> gameTypeBox;
    private JList<String> playerList;
    private Vector<String> players = new Vector<String>();
    private Button inviteButton;
    private final TextArea chatArea;
    private TextField chatField;
    
    public LobbyPanel(JFrame frame, final Client client) {
        this.frame = frame;
        this.client = client;
        
        client.setClientListener(this);
        
        setLayout(new BorderLayout());
        
        Panel chat = new Panel();
        chat.setLayout(new BorderLayout());
        
        chatField = new TextField();
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setFocusable(false);
        chat.add(chatArea, BorderLayout.CENTER);
        chat.add(chatField, BorderLayout.SOUTH);
        
        chatField.addActionListener(this);
        
        chat.setMinimumSize(new Dimension(200, 100));
        
        Panel players = new Panel();
        players.setMinimumSize(new Dimension(200, 100));
        players.setLayout(new BorderLayout());
        
        Panel invitePanel = new Panel();
        invitePanel.setMinimumSize(new Dimension(200, 100));
        invitePanel.setLayout(new BorderLayout());
        
        ScrollPane scrollPane = new ScrollPane();
        invitePanel.add(scrollPane, BorderLayout.CENTER);
        
        playerList = new JList<String>();
        scrollPane.add(playerList);
        
        inviteButton = new Button("Invite Players");
        inviteButton.addActionListener(this);
        invitePanel.add(inviteButton, BorderLayout.SOUTH);
        
        Panel gameStartPanel = new Panel();
        gameStartPanel.setLayout(new BoxLayout(gameStartPanel, BoxLayout.Y_AXIS));
        
        Table table = new Table();
        table.top().pad(PAD_VALUE);
        gameStartPanel.add(table);
        
        table.addCell(new Label("Create/Find room")).expandX().fillX().row();
        
        gameTypeBox = new JComboBox<GameType>(GameType.values());
        table.addCell(gameTypeBox).fillX().pad(PAD_VALUE).row();
        
        findGameButton = new Button("Find Game");
        findGameButton.addActionListener(this);
        table.addCell(findGameButton).pad(PAD_VALUE).fillX();
        
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, invitePanel, gameStartPanel);
        players.add(rightSplitPane);
        
        JSplitPane topSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chat, players);
//        topSplitPane.setDividerLocation(150);
        add(topSplitPane);
        
        client.setClientListener(this);
        //query player list
        client.sendCommand("PLIST");
        
    }
    
    private static enum GameType {
        TWO_PLAYERS("Two Players", "H"), THREE_PLAYERS("Three Players", "I"), FOUR_PLAYERS("Four Players", "J");
        
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
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == findGameButton) {
            client.requestNewGame(((GameType) gameTypeBox.getSelectedItem()).protocolName);
        } else if (e.getSource() == inviteButton) {
            List<String> players = playerList.getSelectedValuesList();
            
            if (players.size() > 0) {
                ArrayList<String> parameters = new ArrayList<String>();
                
                parameters.add("R");
                
                parameters.addAll(players);
                
                client.sendCommand("INVIT", parameters.toArray(new String[0]));
            }
        } else if (e.getSource() == chatField) {
            String text = chatField.getText();
            if (text.startsWith("/")) {
                String[] parsed = text.split(" ");
                client.sendCommand(parsed[0], Arrays.copyOfRange(parsed, 1, parsed.length));
            } else {
                client.sendCommand("CHATM", text);
            }
            chatArea.append(text + "\n");
            chatField.setText(null);
        }
    }
    
    @Override
    public void onHello(String flag) {
    }
    
    @Override
    public void lobbyJoin(String player) {
        if (!players.contains(player) && !client.getName().equals(player)) {
            players.add(player);
            playerList.setListData(players);
        }
    }
    
    @Override
    public void leave(String player) {
        players.remove(player);
        playerList.setListData(players);
    }
    
    @Override
    public void playerList(String[] playerList) {
        for (String player : playerList) {
            lobbyJoin(player);
        }
    }
    
    @Override
    public void gameReady() {
        frame.setContentPane(new RolitView(client.getGame(), client));
        frame.validate();
    }
    
    @Override
    public void loginError() {
    }
    
    @Override
    public void chatMessage(String[] message) {
        chatArea.append(message[0] + " says:\t" + Util.concat(Arrays.copyOfRange(message, 1, message.length)) + "\n");
    }
}
