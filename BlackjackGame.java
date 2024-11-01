import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class BlackjackGame extends JFrame {
    
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    
    private ArrayList<Card> deck;
    private ArrayList<Card> playerHand;
    private ArrayList<Card> dealerHand;
    
    private JButton dealButton;
    private JButton hitButton;
    private JButton standButton;
    
    private JPanel dealerPanel;
    private JPanel playerPanel;
    private JLabel dealerScoreLabel;
    private JLabel playerScoreLabel;
    
    public BlackjackGame() {
        // Set up the main window
        setTitle("Blackjack");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center the window
        setResizable(false);
        
        // Initialize hands
        deck = new ArrayList<>();
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();
        
        // Set up the main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBackground(new Color(0, 100, 0));
        
        // Create dealer and player areas
        dealerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        playerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dealerPanel.setBackground(new Color(0, 100, 0));
        playerPanel.setBackground(new Color(0, 100, 0));
        
        // Create score labels
        dealerScoreLabel = new JLabel("Dealer: 0");
        playerScoreLabel = new JLabel("Player: 0");
        dealerScoreLabel.setForeground(Color.WHITE);
        playerScoreLabel.setForeground(Color.WHITE);
        
        // Add score labels to panels
        dealerPanel.add(dealerScoreLabel);
        playerPanel.add(playerScoreLabel);
        
        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0, 100, 0));
        
        // Initialize buttons
        dealButton = new JButton("Deal");
        hitButton = new JButton("Hit");
        standButton = new JButton("Stand");
        
        // Initially disable hit and stand buttons
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        
        // Add action listeners
        dealButton.addActionListener(e -> dealButtonClicked());
        hitButton.addActionListener(e -> hitButtonClicked());
        standButton.addActionListener(e -> standButtonClicked());
        
        // Add buttons to panel
        buttonPanel.add(dealButton);
        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        
        // Add all panels to main panel
        mainPanel.add(dealerPanel, BorderLayout.NORTH);
        mainPanel.add(playerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        initializeDeck();
    }
    
    public static void main(String[] args) {
        // Run the GUI in the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            BlackjackGame game = new BlackjackGame();
            game.setVisible(true);
        });
    }
    
    private void initializeDeck() {
        deck.clear();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
        
        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(new Card(suit, rank));
            }
        }
        Collections.shuffle(deck);
    }
    
    private void dealButtonClicked() {
        // Clear hands and reset UI
        playerHand.clear();
        dealerHand.clear();
        dealerPanel.removeAll();
        playerPanel.removeAll();
        dealerPanel.add(dealerScoreLabel);
        playerPanel.add(playerScoreLabel);
        
        // Initialize new deck if needed
        if (deck.size() < 10) initializeDeck();
        
        // Deal initial cards
        dealCard(playerHand, true);
        dealCard(dealerHand, true);
        dealCard(playerHand, true);
        dealCard(dealerHand, false);  // Dealer's hole card
        
        // Update UI
        updateScores();
        enableGameButtons(true);
        dealButton.setEnabled(false);
        
        revalidate();
        repaint();
    }
    
    private void hitButtonClicked() {
        dealCard(playerHand, true);
        updateScores();
        
        if (calculateScore(playerHand, false) > 21) {
            endGame("Bust! Dealer wins!");
        }
    }
    
    private void standButtonClicked() {
        // Reveal dealer's hole card
        revealDealerCard();
        updateScores();  // Now shows full dealer score
        
        // Dealer must hit on 16 and below, stand on 17 and above
        while (calculateScore(dealerHand, false) < 17) {
            dealCard(dealerHand, true);
            updateScores();
        }
        
        determineWinner();
    }
    
    private void dealCard(ArrayList<Card> hand, boolean faceUp) {
        Card card = deck.remove(deck.size() - 1);
        hand.add(card);
        
        JLabel cardLabel = new JLabel(faceUp ? card.getCardImage() : 
            new ImageIcon("cards/53.png"));
        if (hand == dealerHand) {
            dealerPanel.add(cardLabel);
        } else {
            playerPanel.add(cardLabel);
        }
    }
    
    private void revealDealerCard() {
        dealerPanel.removeAll();
        dealerPanel.add(dealerScoreLabel);
        for (Card card : dealerHand) {
            dealerPanel.add(new JLabel(card.getCardImage()));
        }
        revalidate();
        repaint();
    }
    
    private int calculateScore(ArrayList<Card> hand, boolean dealerHidden) {
        int score = 0;
        int aces = 0;
        
        // For dealer's hand, skip the last card (hole card) if it's still hidden
        int cardCount = dealerHidden && hand == dealerHand ? hand.size() - 1 : hand.size();
        
        for (int i = 0; i < cardCount; i++) {
            Card card = hand.get(i);
            if (card.getRank().equals("Ace")) {
                aces++;
            }
            score += card.getValue();
        }
        
        // Adjust for aces
        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }
        
        return score;
    }
    
    private void updateScores() {
        // For dealer, only show score of face-up card until stand is clicked
        int dealerScore = calculateScore(dealerHand, true);
        int playerScore = calculateScore(playerHand, false);
        
        dealerScoreLabel.setText("Dealer: " + dealerScore);
        playerScoreLabel.setText("Player: " + playerScore);
    }
    
    private void determineWinner() {
        int dealerScore = calculateScore(dealerHand, false);
        int playerScore = calculateScore(playerHand, false);
        
        String message;
        if (dealerScore > 21) {
            message = "Dealer busts! You win!";
        } else if (dealerScore > playerScore) {
            message = "Dealer wins!";
        } else if (dealerScore < playerScore) {
            message = "You win!";
        } else {
            message = "Push!";
        }
        
        endGame(message);
    }
    
    private void endGame(String message) {
        JOptionPane.showMessageDialog(this, message);
        enableGameButtons(false);
        dealButton.setEnabled(true);
    }
    
    private void enableGameButtons(boolean enabled) {
        hitButton.setEnabled(enabled);
        standButton.setEnabled(enabled);
    }
}