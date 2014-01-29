package team144.rolit.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;

public class Connection extends Thread {
    private static final String SEPERATOR = " ";
    
    private Socket socket;
    private NetworkListener listener;
    private BufferedWriter out;
    private BufferedReader in;
    
    private boolean running;
    
    public Connection(Socket socket, NetworkListener listener) {
        this.listener = listener;
        this.socket = socket;
        setName(listener.getName());
        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        try {
            running = true;
            while (running) {
                synchronized (in) {
                    String message;
                    while ((message = in.readLine()) != null) {
                        String[] contents = decryptMessage(message);
                        running = listener.executeCommand(contents[0], Arrays.copyOfRange(contents, 1, contents.length), this);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            running = false;
            
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                //could happen, but is not important because the stream would close anyway
                e.printStackTrace();
            }
        }
    }
    
    private String[] decryptMessage(String message) {
        String[] contents = message.split(SEPERATOR);
        String[] parameters = new String[contents.length - 1];
        for (int i = 1; i < contents.length; i++) {
            parameters[i - 1] = contents[i];
        }
        return contents;
    }
    
    private String concatMessage(String cmd, String[] message) {
        if(message.length == 0) return cmd;
        
        String result = cmd + " " + message[0];
        for (int i = 1; i < message.length; i++) {
            result += " " + message[i];
        }
        return result;
        
    }
    
    public void write(String cmd, String...parameters) {
        String output = concatMessage(cmd, parameters);
        write(output);
    }
    
    public void write(String combined) {
        try {
            out.write(combined + System.lineSeparator());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    public void setRunning(boolean b) {
        running = b;
    }
    
    public boolean isRunning() {
        return running;
    }
}
