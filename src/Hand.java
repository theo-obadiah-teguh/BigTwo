import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The Hand class is a subclass of the CardList class and is used to model a hand of cards.
 */
public abstract class Hand extends CardList {
	/**
	 * Custom constant ArrayList of Strings to check the ordering of 5-Card Hand types
	 */
	private static final String[] TYPES = { "Straight", "Flush", "FullHouse", "Quad", "StraightFlush"};
	
	/**
	 * a constructor for building a hand with the specified player and list of cards.
	 * @param player player owning the hand.
	 * @param cards cards contained by the hand.
	 */
	public Hand(CardGamePlayer player, CardList cards) {
		// Superclass just has the no-argument default constructor, so we just directly implement the constructor code
		// First, we set the private instance variable for player
		this.player = player;
		
		// Then, add the cards from the cards parameter to the Hand we want to construct
		for (int i = 0; i < cards.size(); i++) {
			addCard(cards.getCard(i));
		}
	}
	
	/** the player who plays this hand.*/
	private CardGamePlayer player;
	
	/**
	 * a method for retrieving the player of this hand.
	 * @return player field of the Hand object.
	 */
	public CardGamePlayer getPlayer() {
		return player;
	}
	
	/**
	 * a method for retrieving the top card of this hand.
	 * @return top card of the Hand object.
	 */
	public Card getTopCard() {
		// Handle the case of an empty hand
		if (this.isEmpty()) {
			return null;
		}
		
		// Get the type of the hand
		String handType = this.getType();
		
		// Naive implementation for Single/Pair/Triplet/Straight/Flush/StraightFlush
		if (Objects.equals(handType, "Single") ||
				Objects.equals(handType, "Pair") ||
				Objects.equals(handType, "Triple") ||
				Objects.equals(handType, "Straight") ||
				Objects.equals(handType, "Flush") ||
				Objects.equals(handType, "StraightFlush")) {
			this.sort();
			return this.getCard(this.size() - 1);
		} 
		else if (Objects.equals(handType, "FullHouse")) {
			// Make a dictionary of rank and counts
			HashMap<Integer, Integer> dict = new HashMap<>();
			
			for (int i = 0; i < this.size(); i++) {
				if (dict.get(this.getCard(i).getRank()) == null) {
					dict.put(this.getCard(i).getRank(), 1);
				} else {
					dict.put(this.getCard(i).getRank(), dict.get(this.getCard(i).getRank()) + 1);
				}
			}

			// Find the key (rank) with the value (count 3)
			int rank = -1;
			
			for (Map.Entry<Integer, Integer> set : dict.entrySet()) {
				if (set.getValue() == 3) {
					rank = set.getKey();
				}
			}
			
			// Make new card list of the triplets
			CardList triplet = new CardList();
			
			for (int i = 0; i < this.size(); i++) {
				if (this.getCard(i).getRank() == rank) {
					triplet.addCard(this.getCard(i));
				}
			}
			
			// Sort the triplet
			triplet.sort();
			
			// Get the biggest card within the triplet
			return triplet.getCard(triplet.size() - 1);
		}	
		else { // Quad Case
			/* For this case, the top card is from the group of four
			* Therefore, the suit will always be spades (3)
			* We just need to find the rank
			*/
			
			// Make a dictionary of rank and counts
			HashMap<Integer, Integer> dict = new HashMap<>();
			
			for (int i = 0; i < this.size(); i++) {
				dict.put(this.getCard(i).getRank(), dict.get(this.getCard(i).getRank()) + 1);
			}
			
			// Initialize return value
			int rank = -1;
			
			// Find the key with the value (count 4)
			for (Map.Entry<Integer, Integer> set : dict.entrySet()) {
				if (set.getValue() == 4) {
					rank = set.getKey();
				}
			}
			return new BigTwoCard(rank, 3);
		}
	}
	
	/**
	 * a method for checking if this hand beats a specified hand.
	 * @param hand a Hand object.
	 * @return true if the current Hand object beats the input argument hand, and false otherwise.
	 */
	public boolean beats(Hand hand) {
		// Corner case where the hands cannot be compared, just return false
		// Or if this hand is empty, it definitely loses
		if (this.size() != hand.size()) {
			return false;
		}
		// Get the handType index of both hands
		int thisHandTypeIndex = getTypeIndex(this.getType());
		int thatHandTypeIndex = getTypeIndex(hand.getType());
		
		// SPECIAL CASE: Comparing straight, flush, full house, quad, and straight flush
		// Type comparison that doesn't require getTopCard()
		if (hand.size() == 5 && this.size() == 5 && thisHandTypeIndex > thatHandTypeIndex) {
			return true;
		} else if (hand.size() == 5 && this.size() == 5 && thisHandTypeIndex < thatHandTypeIndex) {
			return false;
		}
		
		// Only if this is the same hand type, we will continue with getTopCard()
		// Note: same type hand = same index (for size five hands)
		
		// Get the suit of both hands
		int thisCardSuit = this.getTopCard().getSuit();
		int thatCardSuit = hand.getTopCard().getSuit();
		
		// SPECIAL CASE: Comparing Flushes
		// If the flush suits are different, we can directly know which hand wins
		if (this.size() == 5 && hand.size() == 5 &&
				Objects.equals(this.getType(), "Flush") &&
				Objects.equals(hand.getType(), "Flush") &&
				thisCardSuit > thatCardSuit) {
			return true;
		} else if (this.size() == 5 && hand.size() == 5 &&
				Objects.equals(this.getType(), "Flush") &&
				Objects.equals(hand.getType(), "Flush") &&
				thisCardSuit < thatCardSuit) {
			return false;
		}
		
		// Define default the winning condition for cards
		if (this.getTopCard().compareTo(hand.getTopCard()) == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Custom private method to generate a number mapping for the 5-card types.
	 * @param type string of the hand type.
	 * @return index to order the type strength.
	 */
	private static int getTypeIndex(String type) {
		// Default value
		int k = -1;
		
		// Lookup the constant String array
		for (int i = 0; i < TYPES.length; i++) {
			if (TYPES[i].equals(type)) {
				k = i;
				break;
			}
		}
		return k;
	}
	
	/**
	 * a method for checking if this is a valid hand.
	 * @return true if the hand is valid and false otherwise.
	 */
	public abstract boolean isValid();
	
	/**
	 * a method for returning a string specifying the type of this hand.
	 * @return type of hand.
	 */
	public abstract String getType();
	
}
