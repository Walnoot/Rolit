package team144.rolit.network;

import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

import team144.util.Util;

public class Authenticator implements NetworkListener {
    
    private Socket socket;
    private Peer peer;
    private String name;
    
    private PrivateKey pk;
    
    private static final String USERNAME = "player_willemsiers";
    private static final String PASSWORD = "Ouleid9E";
    
    public static void main(String[] args) {
        Authenticator auth = new Authenticator();
        
        System.out.println(new String(auth.signMessage("Hallo Ik Ben Willem!!!")));
    }
    
    public Authenticator() {
        try {
            this.name = "Authenticatoor";
            InetAddress address = InetAddress.getByName("ss-security.student.utwente.nl");
            socket = new Socket(address, 2013);
            System.out.println(socket.isConnected());
            peer = new Peer(socket, this);
            peer.start();
            
            idPlayer(USERNAME, PASSWORD);//generate private key
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void printMessage(String m) {
        System.out.println(name + ":\t" + m);
    }
    
    private void idPlayer(String name, String password) {
        sendCommand("IDPLAYER", new String[] { name, password });
    }
    
    private void sendCommand(String cmd, String[] parameters) {
        peer.write(cmd, parameters);
    }
    
    @Override
    public boolean executeCommand(String cmd, String[] parameters) {
        switch (cmd) {
            case ("ERROR"):
                printMessage(parameters[0]); //wrong user/pw, try again
                break;
            case ("PRIVKEY"):
                setPrivateKey(parameters[0]);
                return true;
            default:
                printMessage("Unknown Command()\t" + cmd + " " + Util.concat(parameters));
                break;
        }
        return false;
    }
    
    public byte[] signMessage(String message) {
        while (pk == null) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        byte[] signature = null;
        try {
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initSign(pk);
            sig.update(message.getBytes());
            signature = sig.sign();
            return signature;
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return signature;
    }
    
    public boolean checkMessage(String message, byte[] signature) {
        Boolean check = false;
        try {
            byte[] bytes = Base64.decodeBase64(message.getBytes());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            KeyFactory fact;
            fact = KeyFactory.getInstance("RSA");
            
            PublicKey publicKey = fact.generatePublic(keySpec);
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(publicKey);
            sig.update(message.getBytes());
            check = sig.verify(signature);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return check;
    }
    
    private void setPrivateKey(String b64key) {
        try {
            byte[] bytes = Base64.decodeBase64(b64key.getBytes());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            pk = fact.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public String getName() {
        return name;
    }
    
}
