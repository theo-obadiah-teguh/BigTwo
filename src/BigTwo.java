import javax.swing.*;
import java.util.ArrayList;

/**
 * The BigTwo class implements the CardGame interface and is used to model a Big Two card game.
 */
public class BigTwo {
	/**
	 * a constructor for creating a Big Two card game.
	 */
	public BigTwo() {
		// Create four players
		this.numOfPlayers = 4;

		// Add them to the playerList
		this.playerList = new ArrayList<>();
		for (int i = 0; i < numOfPlayers; i++) {
			this.playerList.add(new CardGamePlayer(null));
		}

		// Create BigTwoUI object with the current constructed game object
		this.ui = new BigTwoGUI(this);

		// NEW: Create a BigTwoClient
		this.client = new BigTwoClient(this, this.ui);

		// Prompt player name upon entering
		String playerName = JOptionPane.showInputDialog("Hello! What's your name?");

		// Catch the corner case where the user did not input a name
		// Catch the corner case where the user had a really long name
		while(playerName.isEmpty() || playerName.length() > 10) {
			if (playerName.isEmpty()) {
				playerName = JOptionPane.showInputDialog("Your name cannot be empty.\nPlease enter a new name to continue.");
			} else {
				playerName = JOptionPane.showInputDialog("The maximum length of your name is 10 characters.\nPlease enter a new name to continue.");
			}
		}

		// Welcome message
		JOptionPane.showMessageDialog(null, "Welcome to BigTwo " + playerName + "!",
				"BigTwoLobby", JOptionPane.QUESTION_MESSAGE);

		// Hard coded IP address and port number
		this.client.setPlayerName(playerName);
		this.client.setServerIP("127.0.0.1");
		this.client.setServerPort(Integer.parseInt("2396"));
		this.client.connect();

		// Create and shuffle a deck of cards
		this.deck = new BigTwoDeck();
		this.deck.shuffle();
	}

	/** a BigTwoClient object.*/
	private BigTwoClient client;

	/** a boolean to denote whether the game has started.*/
	private boolean hasStarted;
	
	/** an integer specifying the number of players.*/
	private int numOfPlayers;
	
	/** a deck of cards.*/
	private Deck deck;
	
	/** a list of players.*/
	private ArrayList<CardGamePlayer> playerList;
	
	/** a list of hands played on the table.*/
	private ArrayList<Hand> handsOnTable;
	
	/** an integer specifying the index of the current player.*/
	private int currentPlayerIdx;

	/** a custom private integer specifying the index of the last player.*/
	private int lastPlayerIdx;
	
	/** a BigTwoUI object for providing the user interface.*/
	private BigTwoGUI ui;

	/**
	 * a method to set the game status.
	 * @param status a boolean denoting the game status.
	 */
	public void updateGameStatus(boolean status) {
		this.hasStarted = status;
	}

	/**
	 * a method for checking if the game has started.
	 * @return true if the game has started and false otherwise.
	 */
	public boolean getGameStatus() {
		return hasStarted;
	}

	/**
	 * a method for getting the number of players.
	 * @return numOfPlayers field of BigTwo object.
	 */
	public int getNumOfPlayers() {
		return numOfPlayers;
	}
	
	/**
	 * a method for retrieving the deck of cards being used.
	 * @return deck field of BigTwo object.
	 */
	public Deck getDeck() {
		return deck;
	}

	/**
	 * a custom public method to set the local player list.
	 * @param newPlayerList new player list retrieved from the server
	 */
	public void setPlayerList(String[] newPlayerList) {
		for (int i = 0; i < 4; i++) {
			this.playerList.set(i, new CardGamePlayer(newPlayerList[i]));
		}
	}

	/**
	 * a custom public method to update a player entry in the local player list.
	 * @param idx the index to update
	 * @param newPlayerName the name of the new player
	 */
	public void updatePlayerList(int idx, String newPlayerName) {
		this.playerList.set(idx, new CardGamePlayer(newPlayerName));
	}

	/**
	 * a method for retrieving the list of players.
	 * @return playerList field of BigTwo object.
	 */
	public ArrayList<CardGamePlayer> getPlayerList() {
		return playerList;
	}
	
	/**
	 * a method for retrieving the list of hands played on the table.
	 * @return handsOnTable field of BigTwo object.
	 */
	public ArrayList<Hand> getHandsOnTable() {
		return handsOnTable;
	}
	
	/**
	 * a method for retrieving the index of the current player.
	 * @return currentPlayerIdx field of BigTwo object.
	 */
	public int getCurrentPlayerIdx() {
		return currentPlayerIdx;
	}
	
	/**
	 * a method for starting/restarting the game with a given shuffled deck of cards.
	 * @param deck a deck of 52 BigTwoCard objects to play the game.
	 */
	public void start(Deck deck) {
		// Set default value of handsOnTable to empty ArrayList ??
		this.handsOnTable = new ArrayList<>();

		// Remove all the cards from the players
		for (CardGamePlayer player : playerList) {
			player.removeAllCards();
		}
		
		// Distribute the cards to the players
		int cardIdx = 0;
		
		for (CardGamePlayer player : playerList) {
			for (int i = 0; i < 13; i++) {
				player.addCard(deck.getCard(cardIdx));
				cardIdx++;
			}
			// After adding all the cards, sort them
			player.sortCardsInHand();
		}
		
		// Identify the player who holds the Three of Diamonds
		int startingPlayerIdx = 0;

		for (CardGamePlayer player : playerList) {
			// Card creation uses original (non BigTwo) indexing
			if (player.getCardsInHand().contains(new Card(0, 2))) {
				break;
			}
			startingPlayerIdx += 1;
		}
		
		/* Set both the currentPlayerIdx of the BigTwo object and the activePlayer of
		 * the BigTwoUI object to the index of the player who holds the Three of Diamonds
		 */
		this.currentPlayerIdx = startingPlayerIdx;
		updateUI();
	}

	/**
	 * a public helper and wrapper method to tell the client to send messages.
	 * @param message a CardGameMessage object containing the data.
	 */
	public void clientSendMessage(CardGameMessage message) {
		this.client.sendMessage(message);
	}

	/**
	 * a public helper and wrapper method to return the player name of the current client.
	 * @return a String containing the player's name
	 */
	public String clientGetPlayerName() {
		return this.client.getPlayerName();
	}

	/**
	 * a public helper and wrapper method to return the player ID of the current client.
	 * @return an integer which is the player's index in the local player list
	 */
	public int clientGetPlayerID() {
		return this.client.getPlayerID();
	}

	/**
	 * a public helper method to connect the client to the server.
	 */
	public void clientConnect() {
		this.client.connect();
	}

	/**
	 * a public helper and wrapper method to check if the client is connected to a server.
	 * @return true if the client is connected and false otherwise.
	 */
	public boolean clientIsConnected() {
		return this.client.isConnected();
	}
	
	/**
	 * a method for making a move by a player with the specified index using the cards specified by the list of indices.
	 * Broadcasts the current player's move to others over the network.
	 * @param playerIdx index of the current player.
	 * @param cardIdx indexes of the cards to play.
	 */
	public void makeMove (int playerIdx, int[] cardIdx) {
		// The client should send a message of the type MOVE, with playerID being -1
		// The data is a reference to a regular array of integers specifying the indices of the cards
		// selected by the local player.
		this.clientSendMessage(new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx));
	}
	
	/**
	 * a method used by all users to validate the move made by the current player, upon receiving information over the network.
	 * @param playerIdx playerIdx index of the current player.
	 * @param cardIdx cardIdx indexes of the cards to play.
	 */
	public void checkMove (int playerIdx, int[] cardIdx) {
		// Get current player, and get a card list they want to play
		CardGamePlayer currentPlayer = playerList.get(playerIdx);
		CardList cards = currentPlayer.play(cardIdx);
		
		// Check direct violations of rules
		// NOTE: Whether or not the hand beats another is not checked in this method.
		if (isNotLegal(cards, playerIdx, cardIdx)) {
			notLegalUpdateUI();
			return;
		}

		// Handle pass condition
		if (cardIdx == null) {
			ui.printMsg("{Pass}\n");
			updatePlayerIdx();
			updateUI();
			return;
		}

		// Make a hand out of the card list played
		Hand hand = composeHand(currentPlayer, cards);
		
		// Valid hand is composed, or move is legal
		// Note we win, if the table is empty (first round)
		// If everybody has passed, and we return to the original player, the hand does not need to be a winning hand
		if (hand != null && (playerIdx == lastPlayerIdx || handsOnTable.isEmpty() || hand.beats(handsOnTable.getLast()))) {
			
			// Print type of hand and cards
			ui.printMsg(String.format("{%s} ", hand.getType()));
			ui.printMsg(hand + "\n");
			
			// Player puts hand on table
			handsOnTable.add(hand);

			// Cards are played, and remember that this is the last player to play
			this.lastPlayerIdx = getCurrentPlayerIdx();
			
			// Cards are removed from player's arsenal
			currentPlayer.removeCards(cards);
			
			// Terminal state check
			if (this.endOfGame()) {
				// There is no more current player
				this.currentPlayerIdx = -1;
				endOfGameDialog();
			} else {
				// Update game state if the game is not finished
				updatePlayerIdx();
				updateUI();
			}
		} else {
			notLegalUpdateUI();
		}
	}
	
	/**
	 * a method for checking if the game ends.
	 * @return a boolean denoting the end of the game.
	 */
	public boolean endOfGame() {
		// Initialize the initial game status
		boolean finished = false;
		
		// The game ends when any of the player has no more cards in their hand
		for (CardGamePlayer player : playerList) {
			if (player.getNumOfCards() == 0) {
				finished = true;
			}
		}
		
		// Finally return result
		return finished;
	}
	
	/**
	 * a method for starting a Big Two card game.
	 * @param args list of command line input arguments.
	 */
	public static void main(String[] args) {
		// Create BigTwo card game
		BigTwo game = new BigTwo();
				
		// Start the game with the deck of cards
		game.start(game.getDeck());
	}
	
	/**
	 * a method for returning a valid hand from the specified list of cards of the player.
	 * @param player player that owns the hand.
	 * @param cards cards contained by the hand.
	 * @return valid hand or null if there are no valid hands.
	 */
	public static Hand composeHand(CardGamePlayer player, CardList cards) {
		Hand hand = null;

		// Corner case, empty list
		if (cards == null) {
			return null;
		}
		
		switch (cards.size()) {
			case 1:
				hand = new Single(player, cards);
				break;
			case 2:
				hand = new Pair(player, cards);
				break;
			case 3:
				hand = new Triple(player, cards);
				break;
			case 5:
				// Try out best hand to the weakest hand
				if (new StraightFlush(player, cards).isValid()) {
				hand = new StraightFlush(player, cards);
				} else if (new Quad(player, cards).isValid()) {
				hand = new Quad(player, cards);
				} else if (new FullHouse(player, cards).isValid()) {
				hand = new FullHouse(player, cards);
				} else if (new Flush(player, cards).isValid()) {
				hand = new Flush(player, cards);
				} else if (new Straight(player, cards).isValid()) {
				hand = new Straight(player, cards);
				} break;
		}
		
		// Final validation step
		// This is somewhat redundant, but this guarantees output for the method
		if (hand == null || !hand.isValid()) {
			return null;
		} else {
			return hand;
		}
	}

	/**
	 * a custom private method to increment the player index.
	 */
	private void updatePlayerIdx() {
		// Reset currentPlayerIndex
		this.currentPlayerIdx += 1;

		// After player three plays, we go back to player one
		if (this.currentPlayerIdx == getNumOfPlayers()) {
			this.currentPlayerIdx= 0;
		}
	}

	/**
	 * a custom private method to reset the active player index, and update the UI state.
	 */
	private void updateUI() {
		// Setup next player
		ui.setActivePlayer(getCurrentPlayerIdx());

		// Call the repaint() method of the BigTwoUI object to show the cards on the table
		ui.repaint();

		// Call the promptActivePlayer() method of the BigTwoUI object to prompt user to select cards and make their move
		ui.promptActivePlayer();
	}

	/**
	 * a custom private method to check if a move is not legal.
	 * @return true if no first round or pass rules are violated, false otherwise. The hand winning condition is NOT checked here.
	 */
	private boolean isNotLegal(CardList cards, int playerIdx, int[] cardIdx) {
		// Check to see if this is the first round
		boolean firstRound = handsOnTable.isEmpty();

		/*
		First round cannot be a pass
		First round must have three diamonds
		A player cannot pass if they are the one who played the last hand of cards on the table
		*/
		return (firstRound && cardIdx == null)
				|| (firstRound && !cards.contains(new Card(0, 2)))
				|| (playerIdx == lastPlayerIdx && cardIdx == null);
	}

	/**
	 * a custom private method for handling animations of non-legal moves.
	 */
	private void notLegalUpdateUI() {
		ui.printMsg("Not a legal move!!!\n");
		ui.promptActivePlayer();
	}

	/**
	 *  custom private method to calculate the number of remaining cards of each player and send a game ending dialog.
	 */
	private void endOfGameDialog() {
		// There are no more active players
		ui.setActivePlayer(getCurrentPlayerIdx());
		ui.repaint();

		// Clear the message area
		ui.clearMsgArea();

		// Format the output string
		StringBuilder outputString = new StringBuilder();

		int winnerIdx = -1;
		int currIdx = 0;

		// Print remaining cards for each player
		for (CardGamePlayer player : playerList) {
			String playerName = player.getName();
			int cardsLeft = player.getCardsInHand().size();

			// Construct the output string
			if (cardsLeft == 0) {
				outputString.append(String.format("\n%s wins the game.", playerName));
				winnerIdx = currIdx;
			} else {
				outputString.append(String.format("\n%s has %d cards in hand.", playerName, cardsLeft));
			}

			currIdx += 1;
		}

		// Determine dialog title based on winning condition
		String dialogTitle;
		if (winnerIdx == this.clientGetPlayerID()) {
			dialogTitle = "You won!";
		} else {
			dialogTitle = "You lost!";
		}

		// Show dialog
		JOptionPane.showMessageDialog(null, outputString, dialogTitle,
				JOptionPane.INFORMATION_MESSAGE);

		// The client should send a message of the type READY, with playerID and data being -1 and null, respectively, to the server.
		this.clientSendMessage(new CardGameMessage(CardGameMessage.READY, -1,null));
	}

	/**
	 * a custom private method to calculate the number of remaining cards of each player and animate the game end. Only used in the offline version of the game.
	 */
	private void endOfGameAnimation() {
		// There are no more active players
		ui.setActivePlayer(getCurrentPlayerIdx());
		ui.repaint();

		// Print final output
		ui.printMsg("\nGame ends");

		// Print remaining cards for each player
		for (CardGamePlayer player : playerList) {
			String playerName = player.getName();
			int cardsLeft = player.getCardsInHand().size();

			// Format the output string
			String outputString;

			if (cardsLeft == 0) {
				outputString = String.format("\n%s wins the game.", playerName);
			} else {
				outputString = String.format("\n%s has %d cards in hand.", playerName, cardsLeft);
			}
			ui.printMsg(outputString);
		}
	}
}