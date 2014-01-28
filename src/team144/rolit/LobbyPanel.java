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
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JSplitPane;

import team144.rolit.network.Client;
import team144.rolit.network.Client.ClientListener;

import com.esotericsoftware.tablelayout.swing.Table;

public class LobbyPanel extends Panel implements ActionListener, ClientListener {
    private static final long serialVersionUID = -5449982089768781903L;
    
    private static final int PAD_VALUE = 4;
    
    private final JFrame frame;
    private final Client client;

    private Button findGameButton;
    private JComboBox<GameType> gameTypeBox;
    
    public LobbyPanel(JFrame frame,final Client client) {
        this.frame = frame;
        this.client = client;
        
        client.setClientListener(this);
        
        setLayout(new BorderLayout());
        
        Panel chat = new Panel();
        chat.setLayout(new BorderLayout());
        
        final TextField textField = new TextField();
        final TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setFocusable(false);
        chat.add(chatArea, BorderLayout.CENTER);
        chat.add(textField, BorderLayout.SOUTH);
        
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String text = textField.getText();
                if(text.startsWith("/")){
                    String[] parsed = text.split(" ");
                    client.sendCommand(parsed[0], Arrays.copyOfRange(parsed, 1, parsed.length));
                }else{
                client.sendCommand("CHATM", text);
                }
                chatArea.append(text);
                textField.setText(null);
            }
        });        
        
        chat.setMinimumSize(new Dimension(200, 100));
        
        Panel players = new Panel();
        players.setMinimumSize(new Dimension(200, 100));
        players.setLayout(new BorderLayout());
        
        Panel invitePanel = new Panel();
        invitePanel.setMinimumSize(new Dimension(200, 100));
        invitePanel.setLayout(new BorderLayout());
        
        ScrollPane scrollPane = new ScrollPane();
        invitePanel.add(scrollPane, BorderLayout.CENTER);
        
        String[] names = { "test2", "willem", "willem" };
        JList<String> playerList = new JList<String>(names);
        scrollPane.add(playerList);
        
        Button button = new Button("Invite Players");
        invitePanel.add(button, BorderLayout.SOUTH);
        
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
        if(e.getSource() == findGameButton){
            client.requestNewGame(((GameType) gameTypeBox.getSelectedItem()).protocolName);
        }
    }

    @Override
    public void onHello(String flag) {
    }

    @Override
    public void gameReady() {
      frame.setContentPane(new RolitView(client.getGame(), client));
      frame.validate();
    }

    @Override
    public void loginError() {
    }
}
