import java.util.HashMap;

/**
 * This Quad class is a subclass of the Hand class and consists of five cards, with four having the same rank.
 */
public class Quad extends Hand {
	/**
	 * a constructor for building a "Quad" hand with the specified player and list of cards.
	 * @param player player owning the hand.
	 * @param cards cards contained by the hand.
	 */
	public Quad (CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * a method for checking if this is a valid hand.
	 * @return true if the hand is valid and false otherwise.
	 */
	@Override
	public boolean isValid() {
		if (this.size() != 5) {
			return false;
		}

		// Make a dictionary of rank and counts
		HashMap<Integer, Integer> dict = new HashMap<>();
		
		// Initialize all counts to be zero
		for (int i = 0; i < this.size(); i++) {
			dict.put(this.getCard(i).getRank(), 0);
		}
		
		for (int i = 0; i < this.size(); i++) {
			dict.put(this.getCard(i).getRank(), dict.get(this.getCard(i).getRank()) + 1);
		}
		
		// Get the number of ranks, there must be two to be valid
		if (dict.size() != 2) {
			return false;
		}
		
		// The hand can't be a FullHouse if there is no unique rank
		// Equivalently, this checks if there is a rank count of 4
		// This is possible because we made sure there are two keys
		return dict.containsValue(1);
	}
	
	/**
	 * a method for returning a string specifying the type of this hand.
	 * @return type of hand.
	 */
	@Override
	public String getType() {
		return "Quad";
	}
}
