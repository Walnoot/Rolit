package team144.rolit.network;

import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

import team144.util.Util;

public class Authenticator implements NetworkListener {
	
	//pws
	private static final String name1 = "player_willemsiers";
	private static final String pw1 = "Ouleid9E";
	private static final String name2 = "player_michiel";
	private static final String pw2 = "paars";
	
	private Socket socket;
	private Connection peer;
	private String name;
	
	private PrivateKey pk;
	private String pubkey;
	
	private Object lock = new Object();
	
	public Authenticator() {
		try {
			InetAddress address = InetAddress.getByName("ss-security.student.utwente.nl");
			socket = new Socket(address, 2013);
			name = "authenticattooor";
			peer = new Connection(socket, this);
			peer.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * login to the authentication server
	 * 
	 * @param username
	 *            registered username on pki-server
	 * @param password
	 *            matching password
	 * @return PrivateKey object
	 * 
	 *         all params are in default encoding
	 * @throws Exception
	 */
	public PrivateKey login(String username, String password) throws Exception {
		synchronized (lock) {
			try {
				this.name = username;
				sendCommand("IDPLAYER", new String[] { username, password });
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (pk == null) throw new Exception("Authentication Error");
		return pk;
	}
	
	private void printMessage(String m) {
		System.out.println(name + ":\t" + m);
	}
	
	private void sendCommand(String cmd, String... parameters) {
		peer.write(cmd, parameters);
	}
	
	@Override
	public boolean executeCommand(String cmd, String[] parameters, Connection peer) {
		// System.out.println(cmd + " " + Util.concat(parameters));
		switch (cmd) {
			case ("ERROR"):
				printMessage(Util.concat(parameters)); //wrong user/pw, try again
				synchronized (lock) {
					lock.notifyAll();
				}
				break;
			case ("PRIVKEY"):
				setPrivateKey(parameters[0]);
				synchronized (lock) {
					lock.notifyAll();
				}
				break;
			case ("PUBKEY"):
				pubkey = parameters[0];
				synchronized (lock) {
					lock.notifyAll();
				}
				break;
			default:
				printMessage("Unknown Command()\t" + cmd + " " + Util.concat(parameters));
				break;
		}
		return false;
	}
	
	/**
	 * @param String
	 *            message (DEFAULT ENCODEING!!)
	 * @return base64 encoded String signature
	 */
	public String signMessage(String msg) {
		while (pk == null) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		byte[] signature = null;
		try {
			Signature sig = Signature.getInstance("SHA1withRSA");
			sig.initSign(pk);
			sig.update(msg.getBytes());
			signature = sig.sign();
		} catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return Base64.encodeBase64String(signature);
	}
	
	/**
	 * sets PrivateKey pk to represent the private key string
	 * 
	 * @param b64key
	 *            in base64 encoded privatekey
	 */
	private void setPrivateKey(String b64key) {
		try {
			byte[] bytes = Base64.decodeBase64(b64key);
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
	
	/**
	 * 
	 * @param name
	 *            - pki username
	 * @param signature
	 *            - base64 encoded String
	 * @param message
	 *            - message that was to be signed (DEFAULT JAAV ENCODEIGN)
	 * @return - true if message is signed by user
	 */
	public boolean verifySignature(String name, String message, String signature) {
		boolean check = false;
		try {
			sendCommand("PUBLICKEY", name);
			synchronized (lock) {
				lock.wait();
				KeyFactory fact = KeyFactory.getInstance("RSA");
				X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(pubkey.getBytes()));
				PublicKey publicKey = fact.generatePublic(keySpec);
				Signature sig = Signature.getInstance("SHA1withRSA");
				sig.initVerify(publicKey);
				sig.update(message.getBytes());
				check = sig.verify(Base64.decodeBase64(signature));
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return check;
	}
	
	/**
	 * Generate random default encoded string
	 * 
	 * @param n
	 *            length of the generated string in bytes
	 * @returns default encoded string
	 */
	public synchronized static final String generateRandomString(int n) {
		char[] chars = "qwertyuiopasdfghjklzxcvbnm".toCharArray();
		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < n; i++) {
			sb.append(chars[random.nextInt(chars.length)]);
		}
		return sb.toString();
	}
	
	@Override
	public void endConnection(Connection c) {
		
	}
	
}
