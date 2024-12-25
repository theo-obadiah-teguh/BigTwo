/**
 * This Single class is a subclass of the Hand class and consists of only one single card.
 */
public class Single extends Hand {
	/**
	 * a constructor for building a "Single" hand with the specified player and list of cards.
	 * @param player player owning the hand.
	 * @param cards card contained by the hand.
	 */
	public Single (CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * a method for checking if this is a valid hand.
	 * @return true if the hand is valid and false otherwise.
	 */
	@Override
	public boolean isValid() {
		return this.size() == 1;
	}
	
	/**
	 * a method for returning a string specifying the type of this hand.
	 * @return type of hand.
	 */
	@Override
	public String getType() {
		return "Single";
	}
}
