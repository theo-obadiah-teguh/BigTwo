/**
 * This StraightFlush class is a subclass of the Hand class and consists of five cards, with consecutive ranks and the same suit.
 */
public class StraightFlush extends Hand {
	/**
	 * a constructor for building a "StraightFlush" hand with the specified player and list of cards.
	 * @param player player owning the hand.
	 * @param cards cards contained by the hand.
	 */
	public StraightFlush (CardGamePlayer player, CardList cards) {
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
		boolean consecutive = true;
		int comparisonSuit = getCard(0).getSuit();
				
		for (int i = 1; i < this.size(); i++) {
			if (getCard(i).getSuit() != comparisonSuit) {
				sameSuit = false;
				break;
			}
			if (BigTwoCard.bigTwoRank(getCard(i - 1).getRank()) != BigTwoCard.bigTwoRank(getCard(i).getRank()) - 1) {
				consecutive = false;
				break;
			}
		}
				
		// Return true if all cards are consecutive and the same suit
		return sameSuit && consecutive;
	}
	
	/**
	 * a method for returning a string specifying the type of this hand.
	 * @return type of hand.
	 */
	@Override
	public String getType() {
		return "StraightFlush";
	}
}