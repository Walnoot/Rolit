package team144.rolit.network;

public interface NetworkListener {

	/**
	 * @returns false if shutting down
	 */
	public boolean executeCommand(String cmd, String[] parameters);
	
	/**
	 * @returns name of the client or server
	 */
	public String getName();
}
