import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

/**
 * The BigTwoGUI class is used to build a GUI for the BigTwo card game and handle all user actions.
 */
public class BigTwoGUI implements CardGameUI {
    /**
     * a constructor for creating a BigTwoGUI.
     * @param game a reference to a Big Two card game associated with this GUI.
     */
    public BigTwoGUI(BigTwo game) {
        // Initialize the instance variables
        this.game = game;
        this.activePlayer = game.getCurrentPlayerIdx();
        this.playerPanelList = new BigTwoPanel[5];
        this.selected = new boolean[13];

        // Initialize the GUI window
        setupWindow();
    }

    /**
     * a Big Two card game associated with this GUI.
     */
    private BigTwo game;

    /**
     * a boolean array indicating which cards are being selected
     */
    private boolean[] selected;

    /**
     * an integer specifying the index of the active player.
     */
    private int activePlayer;

    /**
     * the main window of the application.
     */
    private JFrame frame;

    /**
     * a panel for showing the cards of each player and the cards played on the table.
     */
    private JPanel gamePanel;

    /**
     * an array containing the player card panels.
     */
    private BigTwoPanel[] playerPanelList;

    /**
     * a “Play” button for the active player to play the selected cards.
     */
    private JButton playButton;

    /**
     * a “Pass” button for the active player to pass his/her turn to the next player.
     */
    private JButton passButton;

    /**
     * a text area for showing the current game status as well as end of game messages.
     */
    private JTextArea msgArea;

    /**
     * a text area for showing chat messages sent by the players.
     */
    private JTextArea chatArea;

    /**
     * a text field for players to input chat messages.
     */
    private JTextField chatInput;

    /**
     * a connect button for players to manually connect to the server.
     */
    private JMenuItem connectButton;

    /**
     * a custom private method to initialize the GUI window.
     */
    private void setupWindow() {
        // Create a window or frame
        this.frame = new JFrame("Big Two");

        // Quit program when window is closed
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Make the layout of the frame
        this.frame.setLayout(new BorderLayout());

        // Add all the panels to the frame
        setupTopPanel();
        setupBottomPanel();
        setupGamePanel();
        setupTextPanel();

        // Display the window
        this.frame.pack();
        this.frame.setVisible(true);
    }

    /**
     * a custom private method to initialize the top panel of the game window.
     */
    private void setupTopPanel() {
        // Make the top panel for the restart button
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(250, 250, 250));
        topPanel.setPreferredSize(new Dimension(0, 30));
        topPanel.setLayout(new BorderLayout());

        // Make a menu bar in the top panel
        JMenuBar gameMenuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Menu");

        // Add a restart button to the menu bar, and register it to a listener
        this.connectButton = new JMenuItem("Connect");
        this.connectButton.addActionListener(new ConnectMenuItemListener());
        gameMenu.add(this.connectButton);

        // Add a quit button to the menu bar, and register it to a listener
        JMenuItem quitButton = new JMenuItem("Quit");
        quitButton.addActionListener(new QuitMenuItemListener());
        gameMenu.add(quitButton);

        // Add the menu bar to the JMenu object
        gameMenuBar.add(gameMenu);

        // Add the JMenu object to the top panel
        topPanel.add(gameMenuBar, BorderLayout.WEST);

        // Add the top panel to the frame
        this.frame.add(topPanel, BorderLayout.NORTH);
    }

    /**
     * a custom private method to initialize the BigTwo panel of the game window.
     */
    private void setupGamePanel() {
        // Make the panel for the BigTwo Cards
        this.gamePanel = new JPanel();
        this.gamePanel.setBackground(new Color(0, 163, 94));
        this.gamePanel.setPreferredSize(new Dimension(800, 800));
        this.gamePanel.setLayout(new BoxLayout(this.gamePanel, BoxLayout.Y_AXIS));

        // Set up the panels for the players
        for (int i = 0; i < 4; i++) {
            setupPlayerPanel(i);
        }

        // Make the panel for the table
        setupTablePanel();

        // Add the player and the table panel to the whole bigTwoPanel
        this.frame.add(this.gamePanel, BorderLayout.CENTER);
    }

    /**
     * a custom private method to initialize the individual players' panels.
     * @param playerNumber an integer representing a player's index.
     */
    private void setupPlayerPanel(int playerNumber) {
        // Get the height of the gamePanel
        int height = gamePanel.getHeight();

        // Create the player panel
        BigTwoPanel playerPanel = new BigTwoPanel(playerNumber);
        playerPanel.setBackground(new Color(0, 163, 94));
        playerPanel.setPreferredSize(new Dimension(800, height / 5));
        playerPanel.setBorder(new LineBorder(Color.black));
        playerPanel.setLayout(new BorderLayout());

        // Add the current panel to our list of panels
        this.playerPanelList[playerNumber] = playerPanel;

        // Add the player panel to the BigTwo panel
        this.gamePanel.add(playerPanel);
    }

    /**
     * a custom private method to initialize the table panel.
     */
    private void setupTablePanel() {
        // Get the height of the gamePanel
        int height = gamePanel.getHeight();

        // Set up the table panel
        BigTwoPanel tablePanel = new BigTwoPanel(4);
        tablePanel.setBackground(new Color(0, 163, 94));
        tablePanel.setPreferredSize(new Dimension(800, height / 5));
        tablePanel.setBorder(new LineBorder(Color.black));
        tablePanel.setLayout(new BorderLayout());

        // Add the current panel to our list of panels
        this.playerPanelList[4] = tablePanel;
        this.gamePanel.add(tablePanel);
    }

    /**
     * custom private method to initialize the text panels of the game window.
     */
    private void setupTextPanel() {
        // Add text panel
        JPanel textPanel = new JPanel();
        textPanel.setBackground(Color.darkGray);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        // Add message area to text panel
        this.msgArea = new JTextArea(23,30);

        // Wrap text and don't allow edits
        this.msgArea.setLineWrap(true);
        this.msgArea.setEditable(false);

        // Add a vertical scroll bar to the message area
        JScrollPane scrollMsgArea = new JScrollPane(this.msgArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textPanel.add(scrollMsgArea);

        // Add message area to text panel
        this.chatArea = new JTextArea(23,30);

        // Wrap text and don't allow edits
        this.chatArea.setLineWrap(true);
        this.chatArea.setEditable(false);

        // Change text color
        this.chatArea.setForeground(Color.BLUE);

        // Add a vertical scroll bar to the chat area
        JScrollPane scrollChatArea = new JScrollPane(this.chatArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textPanel.add(scrollChatArea);

        // Add the panel to the frame
        this.frame.add(textPanel, BorderLayout.EAST);
    }

    /**
     * custom private method to initialize the bottom panel of the game window.
     */
    private void setupBottomPanel() {
        // Make the bottom panel for the pass and play buttons and the message box
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(238, 238, 238));
        bottomPanel.setPreferredSize(new Dimension(0, 50));
        bottomPanel.setLayout(new BorderLayout());

        // Separate the bottom panel into the bottom left and right
        // Make the bottom left panel
        JPanel bottomLeftPanel = new JPanel();
        bottomLeftPanel.setBackground(new Color(238, 238, 238));
        bottomLeftPanel.setLayout(new GridBagLayout());

        // Make the bottom right panel
        JPanel bottomRightPanel = new JPanel();
        bottomRightPanel.setBackground(new Color(238, 238, 238));
        bottomRightPanel.setLayout(new GridBagLayout());

        // Make a play button and add it to the bottom left panel
        this.playButton = new JButton("Play");
        bottomLeftPanel.add(playButton);

        // Make a pass button and add it to the bottom left panel
        this.passButton = new JButton("Pass");
        bottomLeftPanel.add(passButton);

        // Make a message typing box on the bottom right
        JLabel chatInputLabel = new JLabel("Message: ");
        bottomRightPanel.add(chatInputLabel);

        // Create the chat input section
        this.chatInput = new JTextField(22);
        this.chatInput.setBackground(Color.WHITE);
        bottomRightPanel.add(this.chatInput);

        // Text area will not be resized, so we can add an extra border without resizing and scaling problems
        bottomRightPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,14));

        // Combine the bottom left and right panel and add them to the frame
        bottomPanel.add(bottomLeftPanel, BorderLayout.CENTER);
        bottomPanel.add(bottomRightPanel, BorderLayout.EAST);
        this.frame.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * a custom method to re-register a key listener to the chat input area. Used when restarting the game.
     */
    private void reinitializeChatInputListener() {
        // Remove key listener in the chat input box
        for (KeyListener kl : this.chatInput.getKeyListeners()) {
            this.chatInput.removeKeyListener(kl);
            this.chatInput.setEditable(false);
        }

        // Make sure that the chat input area is editable
        this.chatInput.setEditable(true);

        // Add a new key listener to the chat input section
        this.chatInput.addKeyListener(new chatInputListener());
    }

    /**
     * a method to return an array of indices of the cards selected through the UI.
     * @return an array of indices of the cards selected, or null if no valid cards have been selected.
     */
    private int[] getSelected() {
        // Make the return array
        int[] cardIdx = null;
        int count = 0;

        // Calculate the length of the array we need
        for (boolean b : selected) {
            if (b) {
                count++;
            }
        }

        // Add the selected card indexes to the array
        if (count != 0) {
            cardIdx = new int[count];
            count = 0;
            for (int j = 0; j < selected.length; j++) {
                if (selected[j]) {
                    cardIdx[count] = j;
                    count++;
                }
            }
        }

        return cardIdx;
    }

    /**
     * a method to reset the list of selected cards to an empty list.
     */
    private void resetSelected() {
        Arrays.fill(selected, false);
    }

    /**
     * a custom public method to activate or deactivate the connect button.
     * @param status true to activate the connect button and false otherwise
     */
    public void updateConnectButtonStatus(boolean status) {
        this.connectButton.setEnabled(status);
    }

    /**
     * a method for setting the index of the active player.
     *
     * @param activePlayer an int value representing the index of the active player
     */
    public void setActivePlayer(int activePlayer) {
        if (activePlayer < 0) {
            this.activePlayer = -1;
        } else if (activePlayer == 4) {
            this.activePlayer = 0;
        } else {
            this.activePlayer = activePlayer;
        }
    }

    /**
     * a method for repainting the GUI.
     */
    public void repaint() {
        // Reset selected array
        resetSelected();

        // Add all the panels to the frame
        this.gamePanel.repaint();

        // Disable all functionalities for non-active players
        disable();

        if (!game.endOfGame()) {
            // Re-enable functionalities for active players
            enable();
        } else {
            for (KeyListener kl : this.chatInput.getKeyListeners()) {
                // Turn off the chat input box if the game is finished
                this.chatInput.removeKeyListener(kl);
                this.chatInput.setEditable(false);
            }
        }
    }

    /**
     * a method for printing the specified string to the message area of the GUI.
     * @param msg the string to be printed to the message area of the card game user interface.
     */
    public void printMsg(String msg) {
        this.msgArea.append(msg);

        // Reset caret position to bottom
        this.msgArea.setCaretPosition(this.msgArea.getDocument().getLength());
    }

    /**
     * a method for clearing the message area of the GUI.
     */
    public void clearMsgArea() {
        this.msgArea.setText("");
    }

    /**
     * a method for printing the specified string to the chat area of the GUI.
     * @param chatMsg the string to be printed to the chat area of the card game user interface.
     */
    public void printChat(String chatMsg) {
        this.chatArea.append(chatMsg + "\n");

        // Reset caret position to bottom
        this.msgArea.setCaretPosition(this.msgArea.getDocument().getLength());
    }

    /**
     * a method for clearing the chat area of the GUI.
     */
    public void clearChatArea() {
        this.chatArea.setText("");
    }

    /**
     * a method for resetting the GUI.
     */
    public void reset() {
        // Reset the list of selected cards
        resetSelected();

        // Clear the message area
        clearMsgArea();

        // Enable user interactions
        enable();
    }

    /**
     * a method for enabling user interactions with the GUI.
     */
    public void enable() {
        // Enable the “Play” button and “Pass” button (i.e., making them clickable), only if the local client is the active player
        if (this.game.clientGetPlayerID() == activePlayer) {
            this.playButton.addActionListener(new PlayButtonListener());
            this.passButton.addActionListener(new PassButtonListener());
        }

        // Enable the BigTwoPanel for selection of cards through mouse click, if the local client is the active player
        for (BigTwoPanel panel : this.playerPanelList) {
            if (this.game.clientGetPlayerID() == activePlayer && panel.playerIndex == activePlayer) {
                panel.registerMouseListener();
            }
        }

        // Reinitialize chat input box
        reinitializeChatInputListener();
    }

    /**
     * a method for disabling non-active player interactions with the GUI.
     */
    public void disable() {
        // Disable the “Play” button and “Pass” button (i.e. making them not clickable)
        for (ActionListener al : this.playButton.getActionListeners()) {
            this.playButton.removeActionListener(al);
        }
        for (ActionListener al : this.passButton.getActionListeners()) {
            this.passButton.removeActionListener(al);
        }

        // Disable the BigTwoPanel for selection of cards through mouse clicks
        for (BigTwoPanel panel : this.playerPanelList) {
            panel.deregisterMouseListener();
        }
    }

    /**
     * a custom public method to disable ALL user interactions with the GUI
     */
    public void disableAll() {
        // Disable the “Play” button and “Pass” button (i.e. making them not clickable)
        for (ActionListener al : this.playButton.getActionListeners()) {
            this.playButton.removeActionListener(al);
        }
        for (ActionListener al : this.passButton.getActionListeners()) {
            this.passButton.removeActionListener(al);
        }

        // Disable the BigTwoPanel for selection of cards through mouse clicks
        for (BigTwoPanel panel : this.playerPanelList) {
                panel.deregisterMouseListener();
        }

        // Turn off the chat input box
        for (KeyListener kl : this.chatInput.getKeyListeners()) {
            this.chatInput.removeKeyListener(kl);
            this.chatInput.setEditable(false);
        }
    }

    /**
     * a method for prompting the active player to select cards and make his/her move.
     */
    public void promptActivePlayer() {
        if (game.getGameStatus()) {
            printMsg(game.getPlayerList().get(this.activePlayer).getName() + "'s turn:\n");
        }
    }

    /**
     * an inner class that extends the JPanel class and implements the MouseListener interface.
     */
    class BigTwoPanel extends JPanel {
        // Array for path creation purposes
        private static final char[] SUITS = { 'd', 'c', 'h', 's' }; // {Diamond, Club, Heart, Spade}
        private static final char[] RANKS = { 'a', '2', '3', '4', '5', '6', '7', '8', '9', 't', 'j', 'q', 'k' };
        private int playerIndex;

        /**
         * a default constructor for the BigTwoPanel class.
         */
        public BigTwoPanel() {
            super();
        }

        /**
         * A constructor for a player's BigTwo panel.
         * @param playerIndex player index owning the panel.
         */
        public BigTwoPanel(int playerIndex) {
            this.playerIndex = playerIndex;
        }

        /**
         * a public BigTwoPanel method to register a mouse listener.
         */
        public void registerMouseListener() {
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    // Retain base class behavior
                    super.mousePressed(e);

                    // Calculate card coordinates
                    int cardX = playerPanelList[playerIndex].getWidth() * 2 / 7;
                    int cardY = playerPanelList[playerIndex].getHeight() / 6;

                    // Calculate card dimensions
                    int cardHeight = playerPanelList[playerIndex].getHeight() * 14 / 17;
                    int cardWidth = playerPanelList[playerIndex].getWidth() * 12 / 100;

                    // Return if this is the table panel
                    if (playerIndex > 3 || playerIndex != activePlayer) {
                        return;
                    }

                    // Get the required variables
                    int x = e.getX();
                    int cardIdx;
                    int playerNumCards = game.getPlayerList().get(playerIndex).getCardsInHand().size();

                    // Some logic to handle the card bounds
                    if (x > cardX && x <= cardX + (playerNumCards - 1) * (cardWidth / 3)){
                        cardIdx = (x - cardX) / (cardWidth / 3);
                    } else if (x > cardX + (playerNumCards - 1) * (cardWidth / 3) && x <= cardX + (playerNumCards - 1) * (cardWidth / 3) + cardWidth){
                        cardIdx = playerNumCards - 1;
                    } else {
                        return;
                    }

                    // Update the selected cards list
                    selected[cardIdx] = !selected[cardIdx];
                    repaint();
                }
                }
            );
        }

        /**
         * a public BigTwoPanel method to deregister all mouse listeners.
         */
        public void deregisterMouseListener() {
            for (MouseListener ml : this.getMouseListeners()) {
                this.removeMouseListener(ml);
            }
        }

        /**
         * a method that overrides paintComponent.
         * @param g the <code>Graphics</code> object to protect
         */
        @Override
        public void paintComponent(Graphics g) {
            // Ensure the panel is properly rendered
            super.paintComponent(g);

            // Step 1: PAINTING THE PLAYER ICON AND TEXT
            if (playerIndex != 4) {
                // Construct the path
                String path = "players/player" + playerIndex + ".png";

                // Get the image for the player icon and scale it
                Image playerImage = new ImageIcon(path).getImage();

                // Calculate icon coordinates
                int iconX = playerPanelList[playerIndex].getWidth() / 10;
                int iconY = playerPanelList[playerIndex].getHeight() / 4;

                // Calculate icon dimensions
                int iconHeight = playerPanelList[playerIndex].getHeight() * 11 / 17;
                int iconWidth = playerPanelList[playerIndex].getWidth() * 14 / 99;

                // Get the name of the player of this panel
                String playerName = game.getPlayerList().get(playerIndex).getName();

                // After the client has entered their name, we display "You" for the current client player
                if (!(playerName == null) && playerIndex == game.clientGetPlayerID()) {
                    playerName = "You";
                }

                // The active player's name will be colored yellow if the game has started
                // The client name should be blue if it is not their current turn
                if (playerIndex == activePlayer && game.getGameStatus()) {
                    g.setColor(Color.YELLOW);
                } else if (playerIndex == game.clientGetPlayerID()) {
                    g.setColor(Color.BLUE);
                } else {
                    g.setColor(Color.BLACK);
                }

                // Set the font size
                int fontSize = 2 + iconHeight * iconWidth / 550;
                if (fontSize > 15) { fontSize = 15; }
                Font font = new Font(g.getFont().getFontName(), Font.PLAIN, fontSize);
                g.setFont(font);

                // Draw the player icon
                if (playerName != null) {
                    g.drawString(playerName, iconX, iconY * 5 / 6);
                    g.drawImage(playerImage, iconX, iconY, iconWidth, iconHeight, this);
                }
            }

            // STEP 2: PREPARE TO PRINT THE CARDS FOR EACH PLAYER
            // Calculate card coordinates
            int cardX = playerPanelList[playerIndex].getWidth() * 2 / 7;
            int cardY = playerPanelList[playerIndex].getHeight() * 155 / 1000;

            // Calculate card dimensions
            int cardHeight = playerPanelList[playerIndex].getHeight() * 14 / 17;
            int cardWidth = playerPanelList[playerIndex].getWidth() * 12 / 100;

            if (game.getGameStatus()) {
                // The condition was updated in the network implementation
                if (this.playerIndex == game.clientGetPlayerID()) {
                    // Get the cards of the player
                    CardList currentDeck = game.getPlayerList().get(this.playerIndex).getCardsInHand();

                    for (int i = 0; i < currentDeck.size(); i++) {
                        // Get the card rank and suit indexes, and create a card object
                        int cardRankIndex = currentDeck.getCard(i).getRank();
                        int cardSuitIndex = currentDeck.getCard(i).getSuit();

                        // Get the image of the back of a card
                        String cardFileName = "cards/" + RANKS[cardRankIndex] + SUITS[cardSuitIndex] + ".gif";
                        Image cardFace = new ImageIcon(cardFileName).getImage();

                        // Draw the images
                        if (selected[i]) {
                            g.drawImage(cardFace, cardX, cardY / 5, cardWidth, cardHeight, this);
                        } else {
                            g.drawImage(cardFace, cardX, cardY, cardWidth, cardHeight, this);
                        }
                        cardX += cardWidth / 3;
                    }
                } else if (this.playerIndex == 4) {
                    // EXCEPTIONAL CASE: TABLE IS A SPECIAL PLAYER
                    Hand lastHandOnTable = (game.getHandsOnTable() == null || game.getHandsOnTable().isEmpty()) ? null : game.getHandsOnTable().getLast();
                    if (lastHandOnTable != null) {
                        for (int i = 0; i < lastHandOnTable.size(); i++) {
                            // Part A: Initialize the text of the table section
                            String tableText1;
                            String tableText2;
                            if (game.getHandsOnTable().isEmpty()) {
                                tableText1 = "";
                                tableText2 = "";
                            } else {
                                String playerName = game.getHandsOnTable().getLast().getPlayer().getName();
                                tableText1 = "Played by";
                                tableText2 = playerName;
                            }

                            // Calculate string coordinates
                            int tableTextX = playerPanelList[playerIndex].getWidth() / 10;
                            int tableTextY = playerPanelList[playerIndex].getHeight() / 4;

                            // Calculate icon dimensions
                            int heightScale = playerPanelList[playerIndex].getHeight() * 11 / 17;
                            int widthScale = playerPanelList[playerIndex].getWidth() * 14 / 99;

                            // Set the font size
                            int fontSize = 2 + heightScale * widthScale / 550;
                            if (fontSize > 15) { fontSize = 15; }
                            Font font = new Font(g.getFont().getFontName(), Font.PLAIN, fontSize);
                            g.setFont(font);

                            // Draw the string
                            g.drawString(tableText1, tableTextX, tableTextY * 4 / 5);
                            g.drawString(tableText2, tableTextX, tableTextY * 7 / 5);

                            // Part B: Drawing the cards
                            // Get the card rank and suit indexes, and create a card object
                            int cardRankIndex = lastHandOnTable.getCard(i).getRank();
                            int cardSuitIndex = lastHandOnTable.getCard(i).getSuit();

                            // Get the image of the back of a card
                            String cardFileName = "cards/" + RANKS[cardRankIndex] + SUITS[cardSuitIndex] + ".gif";
                            Image cardFace = new ImageIcon(cardFileName).getImage();

                            // Draw the images
                            g.drawImage(cardFace, cardX, cardY,cardWidth, cardHeight, this);
                            cardX += cardWidth / 3;
                        }
                    }
                } else {
                    // Get the cards of the player
                    CardList currentDeck = game.getPlayerList().get(this.playerIndex).getCardsInHand();

                    for (int i = 0; i < currentDeck.size(); i++) {
                        // Get the image of the back of a card
                        String cardFileName = "cards/b.gif";
                        Image cardBack = new ImageIcon(cardFileName).getImage();

                        // Draw the images
                        g.drawImage(cardBack, cardX, cardY, cardWidth, cardHeight, this);
                        cardX += cardWidth / 3;
                    }
                }
            }
        }
    }

    /**
     * an inner class that implements the ActionListener interface.
     */
    class PlayButtonListener implements ActionListener {
        /**
         * an overridden method that handles the implementation of the play button.
         * @param event the event to be processed.
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            // When play button is clicked, make a move
            game.makeMove(game.getCurrentPlayerIdx(), getSelected());
        }
    }

    /**
     * an inner class that implements the ActionListener interface.
     */
    class PassButtonListener implements ActionListener {
        /**
         * an overridden method that handles the implementation of the pass button.
         * @param event the event to be processed.
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            game.makeMove(game.getCurrentPlayerIdx(), null);
        }
    }

    /**
     * an inner class that implements the ActionListener interface. Not used in the network-based game.
     */
    class RestartMenuItemListener implements ActionListener {
        /**
         * an overridden method that handles the implementation of the restart button.
         * @param event the event to be processed.
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            // Create a new BigTwoDeck object and shuffle it
            BigTwoDeck deck = new BigTwoDeck();
            deck.shuffle();

            // Turn off and turn on the chat input area
            reinitializeChatInputListener();

            // Delete all mouse listeners in card decks
            for (BigTwoPanel panel : playerPanelList) {
                panel.deregisterMouseListener();
            }

            // OPTIONAL: Clear the text panels
            clearMsgArea();
            clearChatArea();

            // Restart the game
            game.start(deck);
        }
    }

    /**
     * an inner class that implements the ActionListener interface. Prompts the client to make a connection to the server.
     */
    class ConnectMenuItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            // Only make a connection if we are currently not connected
            if (!game.clientIsConnected()) {
                game.clientConnect();
            }
        }
    }

    /**
     * an inner class that implements the ActionListener interface.
     */
    class QuitMenuItemListener implements ActionListener {
        /**
         * an overridden method that handles the implementation of the quit button.
         *
         * @param event the event to be processed.
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            // Exit the program
            System.exit(0);
        }
    }

    /**
     * an inner class that implements the KeyListener interface.
     */
    class chatInputListener implements KeyListener {
        /**
         * an overridden method that is intentionally left empty.
         * @param e the event to be processed.
         */
        @Override
        public void keyTyped(KeyEvent e) {
            // Intentionally left deactivated
        }

        /**
         * an overridden method that sends chat text from input area to display area.
         * @param e the event to be processed.
         */
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                String chatMessage = chatInput.getText();

                // Print the chat input to the chat area, only used in A4
                // printChat(chatMessage);

                // The client should send a message of the type MSG
                // With playerID being -1
                // Data being a reference to a string representing the text in the text input field
                game.clientSendMessage(new CardGameMessage(CardGameMessage.MSG, -1, chatMessage));
            }
        }

        /**
         * Resets the text pane content and the cursor after successfully clicking enter.
         * @param e the event to be processed.
         */
        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                // After we release the enter key, clear the input area and return the cursor
                chatInput.setText("");
                chatInput.setCaretPosition(0);
            }
        }
    }
}
