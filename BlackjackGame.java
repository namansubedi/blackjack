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
    private JPanel mainPanel;

    private JPanel dealerPanel;
    private JPanel playerPanel;
    private JLabel dealerScoreLabel;
    private JLabel playerScoreLabel;

    private int playerMoney = 1000;
    private int currentBet = 0;
    private JLabel moneyLabel;
    private JLabel betLabel;
    private JPanel betButtonPanel;
    private boolean betPlaced = false;

    private JPanel buttonPanel;

    private JComboBox<String> avatarSelector;
    private JLabel avatarLabel;
    private static final int AVATAR_SIZE = 80;  // pixels

    private JComboBox<String> deckSelector;
    private int numberOfDecks = 1;
    private JLabel deckCountLabel;

    public BlackjackGame() {
        // Set up the main window
        setTitle("Blackjack");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setResizable(false);

        // Initialize hands
        deck = new ArrayList<>();
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();

        // Set up the main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout(0, 10));
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
        buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0, 100, 0));

        // Initialize buttons
        dealButton = new JButton("Deal");
        hitButton = new JButton("Hit");
        standButton = new JButton("Stand");

        // Add action listeners
        dealButton.addActionListener(e -> dealButtonClicked());
        hitButton.addActionListener(e -> hitButtonClicked());
        standButton.addActionListener(e -> standButtonClicked());

        // Add buttons to panel
        buttonPanel.add(dealButton);
        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);

        setupBettingUI();

        dealButton.setEnabled(false); // Only enable after bet is placed
        hitButton.setEnabled(false);
        standButton.setEnabled(false);

        add(mainPanel);
        initializeDeck();

        // Add avatar UI before other components
        setupAvatarUI();
        setupDeckSelectorUI();
    }

    private void setupBettingUI() {
        // Create betting panel
        JPanel bettingPanel = new JPanel(new BorderLayout());
        bettingPanel.setBackground(new Color(0, 100, 0));
        
        // Create labels panel
        JPanel labelsPanel = new JPanel(new FlowLayout());
        labelsPanel.setBackground(new Color(0, 100, 0));
        
        moneyLabel = new JLabel("Money: $" + playerMoney);
        betLabel = new JLabel("Current Bet: $" + currentBet);
        moneyLabel.setForeground(Color.WHITE);
        betLabel.setForeground(Color.WHITE);
        
        labelsPanel.add(moneyLabel);
        labelsPanel.add(betLabel);
        
        // Create bet buttons
        betButtonPanel = new JPanel();
        betButtonPanel.setBackground(new Color(0, 100, 0));
        
        int[] betAmounts = {1, 5, 20, 50, 100};
        for (int amount : betAmounts) {
            JButton betButton = new JButton("$" + amount);
            betButton.addActionListener(e -> placeBet(amount));
            betButtonPanel.add(betButton);
        }
        
        // Create a combined bottom panel
        JPanel bottomPanel = new JPanel(new GridLayout(3, 1));
        bottomPanel.setBackground(new Color(0, 100, 0));
        
        // Add components to bottom panel
        bottomPanel.add(labelsPanel);
        bottomPanel.add(betButtonPanel);
        bottomPanel.add(buttonPanel);  // This is the panel with deal/hit/stand buttons
        
        // Update main panel layout
        mainPanel.add(dealerPanel, BorderLayout.NORTH);
        mainPanel.add(playerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void placeBet(int amount) {
        if (amount <= playerMoney && !betPlaced) {
            currentBet += amount;
            playerMoney -= amount;
            updateMoneyLabels();

            // Enable deal button if there's a bet
            dealButton.setEnabled(currentBet > 0);
        }
    }

    private void updateMoneyLabels() {
        moneyLabel.setText("Money: $" + playerMoney);
        betLabel.setText("Current Bet: $" + currentBet);
    }

    public static void main(String[] args) {
        // Run the GUI in the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            BlackjackGame game = new BlackjackGame();
            game.setVisible(true);
        });
    }

    private void dealButtonClicked() {
        // Clear hands and reset UI

        betPlaced = true;
        betButtonPanel.setEnabled(false);
        for (Component c : betButtonPanel.getComponents()) {
            c.setEnabled(false);
        }
        deckSelector.setEnabled(false);  // Disable deck selection while playing
        playerHand.clear();
        dealerHand.clear();
        dealerPanel.removeAll();
        playerPanel.removeAll();
        dealerPanel.add(dealerScoreLabel);
        playerPanel.add(playerScoreLabel);

        // Initialize new deck if needed
        if (deck.size() < 10)
            initializeDeck();

        // Deal initial cards
        dealCard(playerHand, true);
        dealCard(dealerHand, true);
        dealCard(playerHand, true);
        dealCard(dealerHand, false); // Dealer's hole card

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
        updateScores(); // Now shows full dealer score

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

        JLabel cardLabel = new JLabel(faceUp ? card.getCardImage() : new ImageIcon("cards/53.png"));
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
        int jokers = 0;
        
        // For dealer's hand, skip the last card (hole card) if it's still hidden
        int cardCount = dealerHidden && hand == dealerHand ? hand.size() - 1 : hand.size();
        
        // First pass: count non-Ace, non-Joker cards
        for (int i = 0; i < cardCount; i++) {
            Card card = hand.get(i);
            if (card.getRank().equals("Ace")) {
                aces++;
            } else if (card.isJoker()) {
                jokers++;
            } else {
                score += card.getValue();
            }
        }
        
        // Second pass: optimize Joker values (1, 10, or 11)
        for (int i = 0; i < jokers; i++) {
            if (score <= 10) {
                score += 11;  // Use 11 if it won't bust
            } else if (score <= 19) {
                score += 1;   // Use 1 if 11 would bust but still room for more
            } else {
                score += 1;   // Use 1 if close to 21
            }
        }
        
        // Third pass: handle Aces after Jokers
        for (int i = 0; i < aces; i++) {
            if (score <= 10) {
                score += 11;
            } else {
                score += 1;
            }
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

    // Modify endGame()
    private void endGame(String message) {
        if (message.contains("You win")) {
            playerMoney += currentBet * 2; // Win pays 1:1
        } else if (message.contains("Push")) {
            playerMoney += currentBet; // Return the bet
        }
        // If dealer wins, don't return any money (bet was already deducted when placed)

        // Check if player needs a refill
        if (playerMoney <= 0) {
            JOptionPane.showMessageDialog(this, "You're out of money! Here's a fresh $1000!");
            playerMoney = 1000;
        }

        currentBet = 0;
        betPlaced = false;
        updateMoneyLabels();

        // Re-enable betting buttons
        for (Component c : betButtonPanel.getComponents()) {
            c.setEnabled(true);
        }

        deckSelector.setEnabled(true);  // Re-enable deck selection

        JOptionPane.showMessageDialog(this, message);
        enableGameButtons(false);
        dealButton.setEnabled(false); // Don't enable deal until new bet is placed
    }

    private void enableGameButtons(boolean enabled) {
        hitButton.setEnabled(enabled);
        standButton.setEnabled(enabled);
    }

    private void setupAvatarUI() {
        // Create avatar panel in top right
        JPanel avatarPanel = new JPanel(new BorderLayout(5, 5));
        avatarPanel.setBackground(new Color(0, 100, 0));
        
        // Create avatar label
        avatarLabel = new JLabel();
        updateAvatarImage(1);  // Start with first avatar
        
        // Create avatar selector
        String[] avatarOptions = new String[12];
        for (int i = 0; i < 12; i++) {
            avatarOptions[i] = "Avatar " + (i + 1);
        }
        avatarSelector = new JComboBox<>(avatarOptions);
        avatarSelector.setPreferredSize(new Dimension(100, 25));
        
        // Add change listener
        avatarSelector.addActionListener(e -> {
            int selected = avatarSelector.getSelectedIndex() + 1;
            updateAvatarImage(selected);
        });
        
        // Add components to avatar panel
        avatarPanel.add(avatarLabel, BorderLayout.CENTER);
        avatarPanel.add(avatarSelector, BorderLayout.SOUTH);
        
        // Create a wrapper panel for dealer area that includes avatar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 100, 0));
        topPanel.add(dealerPanel, BorderLayout.CENTER);
        topPanel.add(avatarPanel, BorderLayout.EAST);
        
        // Update main panel to use new top panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
    }
    
    // Method to update avatar image
    private void updateAvatarImage(int avatarNumber) {
        ImageIcon originalIcon = new ImageIcon("avatar/p" + avatarNumber + ".png");
        Image image = originalIcon.getImage();
        
        // Scale image to desired size while maintaining aspect ratio
        Image scaledImage = image.getScaledInstance(AVATAR_SIZE, AVATAR_SIZE, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        
        avatarLabel.setIcon(scaledIcon);
    }

    private void setupDeckSelectorUI() {
        // Create deck selector panel in top left
        JPanel deckPanel = new JPanel(new BorderLayout(5, 5));
        deckPanel.setBackground(new Color(0, 100, 0));
        
        // Create deck count label
        deckCountLabel = new JLabel("Current Deck(s): 1");
        deckCountLabel.setForeground(Color.WHITE);
        deckCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Create deck selector
        String[] deckOptions = {"1 Deck", "2 Decks", "4 Decks", "6 Decks"};
        deckSelector = new JComboBox<>(deckOptions);
        deckSelector.setPreferredSize(new Dimension(100, 25));
        
        // Add change listener
        deckSelector.addActionListener(e -> {
            // Only allow changes if no cards are dealt
            if (!betPlaced) {
                numberOfDecks = Integer.parseInt(((String)deckSelector.getSelectedItem()).split(" ")[0]);
                deckCountLabel.setText("Current Deck(s): " + numberOfDecks);
                initializeDeck(); // Reinitialize with new deck count
            } else {
                // Reset selection if cards are dealt
                deckSelector.setSelectedIndex(getIndexForDeckCount(numberOfDecks));
            }
        });
        
        // Add components to deck panel
        deckPanel.add(deckCountLabel, BorderLayout.CENTER);
        deckPanel.add(deckSelector, BorderLayout.SOUTH);
        
        // Update the top panel to include deck selector
        JPanel topPanel = (JPanel)mainPanel.getComponent(0); // Get existing top panel
        topPanel.add(deckPanel, BorderLayout.WEST);
    }
    
    // Helper method to get combo box index from deck count
    private int getIndexForDeckCount(int decks) {
        switch(decks) {
            case 2: return 1;
            case 4: return 2;
            case 6: return 3;
            default: return 0;
        }
    }

    // Modify initializeDeck() to include Jokers
    private void initializeDeck() {
        deck.clear();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
        
        // Add regular cards for each deck
        for (int d = 0; d < numberOfDecks; d++) {
            for (String suit : suits) {
                for (String rank : ranks) {
                    deck.add(new Card(suit, rank));
                }
            }
            // Add two Jokers per deck
            deck.add(new Card("Special", "Joker"));
            deck.add(new Card("Special", "Joker"));
        }
        Collections.shuffle(deck);
    }
}
