/**
 * This Flush class is a subclass of the Hand class and consists of five cards, with the same suit.
 */
public class Flush extends Hand {
	/**
	 * a constructor for building a "Flush" hand with the specified player and list of cards.
	 * @param player player owning the hand.
	 * @param cards cards contained by the hand.
	 */
	public Flush (CardGamePlayer player, CardList cards) {
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
		
		// Check cards
		boolean sameSuit = true;
		int comparisonSuit = getCard(0).getSuit();
				
		for (int i = 1; i < this.size(); i++) {
			if (getCard(i).getSuit() != comparisonSuit) {
				sameSuit = false;
				break;
			}
		}
				
		// Return true if all cards are the same suit
		return sameSuit;
	}
	
	/**
	 * a method for returning a string specifying the type of this hand.
	 * @return type of hand.
	 */
	@Override
	public String getType() {
		return "Flush";
	}
}
