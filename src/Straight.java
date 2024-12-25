/**
 * This Straight class is a subclass of the Hand class and consists of five cards, with consecutive ranks.
 */
public class Straight extends Hand {
	/**
	 * a constructor for building a "Straight" hand with the specified player and list of cards.
	 * @param player player owning the hand.
	 * @param cards cards contained by the hand.
	 */
	public Straight (CardGamePlayer player, CardList cards) {
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
		boolean consecutive = true;
				
		for (int i = 1; i < this.size(); i++) {
			if (BigTwoCard.bigTwoRank(getCard(i - 1).getRank()) != BigTwoCard.bigTwoRank(getCard(i).getRank() - 1)) {
				consecutive = false;
				break;
			}
		}
				
		// Return true if all cards are consecutive ranks
		return consecutive;
	}
	
	/**
	 * a method for returning a string specifying the type of this hand.
	 * @return type of hand.
	 */
	@Override
	public String getType() {
		return "Straight";
	}
}
