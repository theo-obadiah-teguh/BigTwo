/**
 * This Triple class is a subclass of the Hand class and consists of three cards of the same rank.
 */
public class Triple extends Hand {
	/**
	 * a constructor for building a "Triple" hand with the specified player and list of cards.
	 * @param player player owning the hand.
	 * @param cards cards contained by the hand.
	 */
	public Triple (CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * a method for checking if this is a valid hand.
	 * @return true if the hand is valid and false otherwise.
	 */
	@Override
	public boolean isValid() {
		if (this.size() != 3) {
			return false;
		}
		
		// Check all three cards
		boolean sameRank = true;
		int comparisonRank = getCard(0).getRank();
		
		for (int i = 1; i < this.size(); i++) {
			if (getCard(i).getRank() != comparisonRank) {
				sameRank = false;
				break;
			}
		}
		
		// Return true if all cards are the same rank
		return sameRank;
	}
	
	/**
	 * a method for returning a string specifying the type of this hand.
	 * @return type of hand.
	 */
	@Override
	public String getType() {
		return "Triple";
	}
}
