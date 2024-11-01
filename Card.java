import javax.swing.ImageIcon;

public class Card {
    private final String suit;
    private final String rank;
    private final int value;
    private final int imageNumber;
    
    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
        this.value = calculateValue(rank);
        this.imageNumber = calculateImageNumber();
    }
    
    private int calculateValue(String rank) {
        if (rank.equals("Ace")) return 11;
        if (rank.equals("King") || rank.equals("Queen") || rank.equals("Jack")) return 10;
        return Integer.parseInt(rank);
    }
    
    private int calculateImageNumber() {
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
    
    public int getValue() {
        return value;
    }
    
    public String getSuit() {
        return suit;
    }
    
    public String getRank() {
        return rank;
    }
    
    public ImageIcon getCardImage() {
        return new ImageIcon("cards/"+ imageNumber + ".png");
    }
    
    @Override
    public String toString() {
        return rank + " of " + suit;
    }
} 