// import for the image icons
import javax.swing.ImageIcon;

// class for a card in the blackjack game
public class Card {
    // private variables for the card
    private final String suit;
    private final String rank;
    private final int value;
    private final int imageNumber;
    private final boolean isJoker;
    
    // constructor for the card
    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
        this.isJoker = rank.equals("Joker");
        this.value = calculateValue(rank);
        this.imageNumber = calculateImageNumber();
    }
    
    // private method to calculate the value of the card
    private int calculateValue(String rank) {
        if (rank.equals("Joker")) return 11;
        if (rank.equals("Ace")) return 11;
        if (rank.equals("King") || rank.equals("Queen") || rank.equals("Jack")) return 10;
        return Integer.parseInt(rank);
    }
    
    // private method to calculate the image number of the card
    private int calculateImageNumber() {
        // if the card is a joker, return 54
        if (isJoker) return 54;
        
        int suitOffset;
        switch (suit) {
            case "Spades": suitOffset = 0; break;
            case "Hearts": suitOffset = 13; break;
            case "Diamonds": suitOffset = 26; break;
            case "Clubs": suitOffset = 39; break;
            default: return 1;
        }
        
        int rankNumber;
        if (rank.equals("Ace")) rankNumber = 1;
        else if (rank.equals("Jack")) rankNumber = 11;
        else if (rank.equals("Queen")) rankNumber = 12;
        else if (rank.equals("King")) rankNumber = 13;
        else rankNumber = Integer.parseInt(rank);
        
        return suitOffset + rankNumber;
    }
    
    // getter for the value of the card
    public int getValue() {
        return value;
    }
    
    // getter for the suit of the card
    public String getSuit() {
        return suit;
    }
    
    // getter for the rank of the card
    public String getRank() {
        return rank;
    }
    
    // getter for the image of the card
    public ImageIcon getCardImage() {
        return new ImageIcon("cards/"+ imageNumber + ".png");
    }
    
    // getter for if the card is a joker
    public boolean isJoker() {
        return isJoker;
    }
    
    // overriding toString string representation of the card
    @Override
    public String toString() {
        return rank + " of " + suit;
    }
} 