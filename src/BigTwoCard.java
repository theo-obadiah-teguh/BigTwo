/**
 * The BigTwoCard class is a subclass of the Card class and is used to model a card used in a Big Two card game.
 */
public class BigTwoCard extends Card {
	/**
	 * a constructor for building a card with the specified suit and rank.
	 * @param suit an integer between 0 and 3
	 * @param rank an integer between 0 and 12
	 */
	public BigTwoCard(int suit, int rank) {
		// Default constructor of Card doesn't exist, so we use a specified constructor
		super(suit, rank);
	}
	
	/**
	 * a method for comparing the order of this card with the specified card.
	 * @return a negative integer, zero, or a positive integer when this card is less than, equal to, or greater than the specified card.
	 */
	@Override
	public int compareTo(Card card) {
		if (bigTwoRank(this.rank) > bigTwoRank(card.rank)) {
			return 1;
		} else if (bigTwoRank(this.rank) < bigTwoRank(card.rank)) {
			return -1;
		} else if (this.suit > card.suit) {
			return 1;
		} else if (this.suit < card.suit) {
			return -1;
		} else {
			return 0;
		}
	}
	
	/**
	 * a custom method static to convert regular card ranks into BigTwo ranks.
	 * @param rank a Card object's integer rank.
	 * @return updated rank representation in the BigTwo game.
	 */
	public static int bigTwoRank(int rank) {
		// Aces should be rank 11, and 2's should be rank 12
		// Shift the other cards downward by two
		 if (rank == 0 || rank == 1) {
			 rank += 11;
		 } else {
			 rank -= 2;
		 }
		 return rank;
	 }
}
