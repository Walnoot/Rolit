package team144.rolit.network;

public interface NetworkListener {
    
    /**
     * @returns false if shutting down
     */
    public boolean executeCommand(String cmd, String[] parameters,
            Connection peer);
    
    /**
     * @returns name of the client or server
     */
    public String getName();
    
    /**
     * Called by the connection to indicate the server/client that the stream
     * has ended.
     * 
     * @param c
     *            - The conneciton that has ended.
     */
    public void endConnection(Connection c);
}
