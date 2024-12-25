import java.io.*;
import java.net.Socket;

/**
 * used to model a Big Two game client that is responsible for establishing a connection and communicating with the Big Two game server.
 */
public class BigTwoClient implements NetworkGame {
    /**
     * a constructor for creating a Big Two client.
     * @param game a reference to a BigTwo object associated with this client
     * @param gui a reference to a BigTwoGUI object associated the BigTwo object
     */
    public BigTwoClient(BigTwo game, BigTwoGUI gui) {
        this.game = game;
        this.gui = gui;
    }

    /** a BigTwo object for the Big Two card game.*/
    private BigTwo game;

    /** a BigTwoGUI object for the Big Two card game.*/
    private BigTwoGUI gui;

    /** a socket connection to the game server.*/
    private Socket sock;

    /** an ObjectOutputStream for sending messages to the server.*/
    private ObjectOutputStream oos;

    /** an integer specifying the playerID (i.e., index) of the local player.*/
    private int playerID;

    /** a string specifying the name of the local player.*/
    private String playerName;

    /** a string specifying the IP address of the game server.*/
    private String serverIP;

    /** an integer specifying the TCP port of the game server.*/
    private int serverPort;

    /**
     * a method for getting the playerID (i.e., index) of the local player.
     * @return index of the local player.
     */
    @Override
    public int getPlayerID() {
        return this.playerID;
    }

    /**
     * a method for setting the playerID (i.e., index) of the local player.
     * @param playerID the playerID (index) of the local player.
     */
    @Override
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    /**
     * a method for getting the name of the local player.
     * @return a String representing the player's name
     */
    @Override
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * a method for setting the name of the local player.
     * @param playerName the name of the local player
     */
    @Override
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * a method for getting the IP address of the game server.
     * @return the server's IP address in a String format
     */
    @Override
    public String getServerIP() {
        return this.serverIP;
    }

    /**
     * a method for setting the IP address of the game server.
     * @param serverIP the IP address of the server
     */
    @Override
    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    /**
     * a method for getting the TCP port of the game server.
     * @return an integer denoting the server port number
     */
    @Override
    public int getServerPort() {
        return this.serverPort;
    }

    /**
     * a method for setting the TCP port of the game server.
     * @param serverPort the TCP port of the server
     */
    @Override
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * a custom public method to check if the client is connected to a server.
     * @return true or false depending on the connection status of the client to the server
     */
    public boolean isConnected() {
        return (this.sock != null && this.sock.isConnected() && !this.sock.isClosed());
    }

    /**
     * a method for making a socket connection with the game server.
     */
    @Override
    public synchronized void connect() {
        try {
            // Create a socket connection
            // An IP is like an address, and a server port is like a door number
            this.sock = new Socket(getServerIP(), getServerPort());

            // Wrap the socket's output stream as an object output stream
            this.oos = new ObjectOutputStream(this.sock.getOutputStream());

            // Run a thread to listen to messages from the server, and start the thread
            Thread renderThread = new Thread(new ServerHandler());
            renderThread.start();

            // Prepare for a new game
            this.gui.reset();

            // Print a message saying that the connection is successful
            this.gui.printMsg("Connected to server at /" + getServerIP() + ":" + getServerPort() + "\n");

            this.gui.updateConnectButtonStatus(false);
            this.gui.disableAll();
            this.gui.repaint();

        } catch(IOException ex) {
            gui.printMsg("Server is currently unavailable.\n");
            ex.printStackTrace();
        }
    }

    /**
     * a method for parsing the messages received from the game server.
     * @param message the specified message received from the server
     */
    @Override
    public synchronized void parseMessage(GameMessage message) {
        switch (message.getType()) {
            case CardGameMessage.PLAYER_LIST:
                // The playerID in this message specifies the index of the local player
                // The client sets the player ID of the local player
                this.setPlayerID(message.getPlayerID());

                // The data is a reference to a regular array of strings specifying the names of players
                // A null value in the array means that particular player does not exist
                String[] playerList = (String[]) message.getData();

                // Update the names in the playerList
                for (int i = 0; i < 4; i++) {
                    if (i == this.getPlayerID()) {
                        playerList[i] = this.getPlayerName();
                    }
                }

                // Update the local player list
                this.game.setPlayerList(playerList);

                // Send a message of type JOIN, with playerID being -1 and a string of the local player's name
                sendMessage(new CardGameMessage(CardGameMessage.JOIN, -1, this.getPlayerName()));
                break;

            case CardGameMessage.JOIN:
                // The playerID in this message specifies the playerID (i.e., index) of the new player
                int newPlayerIdx = message.getPlayerID();

                // Data is a reference to a string specifying the name of this new player.
                String newPlayerName = (String) message.getData();

                // The client should add a new player to the player list by updating his/her name.
                this.game.updatePlayerList(newPlayerIdx, newPlayerName);

                // Print a message saying that the player has joined
                this.gui.printMsg(this.game.getPlayerList().get(newPlayerIdx).getName() + " has joined the game.\n");

                // Repaint the ui to update the player names
                gui.repaint();

                // If the playerID is identical to the local player, the client should send a message of type READY,
                // with playerID and data being -1 and null, respectively, to the server.
                if (newPlayerIdx == this.getPlayerID()) {
                    sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
                }
                break;

            case CardGameMessage.FULL:
                // The playerID and data in this message are -1 and null, respectively, which can simply be ignored.
                // The client displays a message in the game message area of the BigTwoGUI that the server is full
                // and cannot join the game.
                this.gui.printMsg("This game is full! You can't join the game :(\n");
                break;

            case CardGameMessage.QUIT:
                // The playerID in this message specifies the playerID (i.e., index) of the player who leaves the game,
                // and data is a reference to a string representing the IP address and TCP port of this player.
                int quitPlayerIdx = message.getPlayerID();

                // Print a message saying the player has left
                this.gui.printMsg(this.game.getPlayerList().get(quitPlayerIdx).getName() + " has left the game.\n");

                // The client should remove a player from the game by setting his/her name to an empty string.
                this.game.updatePlayerList(quitPlayerIdx, null);

                // Repaint the gui
                gui.repaint();

                // If a game is in progress, the client should stop the game
                if (game.getGameStatus()) {
                    // Turn off game
                    game.updateGameStatus(false);
                    gui.disableAll();
                }

                // Then send a message of type READY, with playerID and data being -1 and null, respectively, to the server.
                sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
                break;

            case CardGameMessage.READY:
                // The playerID in this message specifies the playerID (i.e., index) of the player who is ready,
                // and data is null which can simply be ignored.
                int readyPlayerIdx = message.getPlayerID();

                // The client should display a message in the game message area of the BigTwoGUI that the specified player is ready.
                String readyPlayerName = this.game.getPlayerList().get(readyPlayerIdx).getName();
                this.gui.printMsg(readyPlayerName + " is ready.\n");
                break;

            case CardGameMessage.START:
                // The playerID in this message is -1, which can simply be ignored,
                // and data is a reference to a (shuffled) BigTwoDeck object.
                BigTwoDeck deck = (BigTwoDeck) message.getData();

                // Print a message saying that the game is starting
                this.gui.clearMsgArea();
                this.gui.printMsg("All players are ready. Game starts.\n");

                // The client should start a new game with the given deck of cards (already shuffled).
                this.game.updateGameStatus(true);
                this.game.start(deck);
                break;

            case CardGameMessage.MOVE:
                // The playerID in this message specifies the playerID (i.e., index) of the player who makes the move,
                int movingPlayerIdx = message.getPlayerID();

                // and data is a reference to a regular array of integers specifying the indices of the cards selected by the player.
                int[] movingPlayerCards = (int[]) message.getData();

                // The client should check the move played by the specified player.
                this.game.checkMove(movingPlayerIdx, movingPlayerCards);
                break;

            case CardGameMessage.MSG:
                // The playerID in this message specifies the playerID (i.e., index) of the player who sends out the chat message
                // The data is a reference to a formatted string including the player’s name, his/her IP address and TCP port, and the original chat message
                String chatMsg = (String) message.getData();

                // The client should display the chat message in the chat message area.
                this.gui.printChat(chatMsg);
                break;
        }
    }

    /**
     * a method for sending the specified message to the game server.
     * @param message the specified message to be sent the server
     */
    public synchronized void sendMessage(GameMessage message) {
        try {
            this.oos.writeObject(message);
            this.oos.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * an inner class that handles the server and implements the runnable interface.
     */
    class ServerHandler implements Runnable {
        /** a buffered reader object to wrap the input stream reader of a server handler.*/
        private ObjectInputStream ois = null;

        /**
         * a constructor that initializes the reader private instance variable based on the client socket's input stream.
         */
        public ServerHandler() {
            try {
                // Initialize the object input stream
                this.ois = new ObjectInputStream(sock.getInputStream());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        /**
         * an overridden run() method.
         */
        @Override
        public synchronized void run() {
            CardGameMessage message;
            try {
                // Keep listening to the object input stream for any messages
                while (true) {
                    message = (CardGameMessage) ois.readObject();
                    parseMessage(message);
                }
            } catch (EOFException ex) {
                gui.printMsg("Lost connection with the server.\n");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}