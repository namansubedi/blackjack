// import swing for the GUI
// import awt for the layout, and event for the event listeners
import javax.swing.*;
import java.awt.*;
//import java.awt.event.*;
import java.util.*;

// main class for the blackjack game
public class BlackjackGame extends JFrame {

    // height and width of the window
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    // array lists for the deck, player hand, and dealer hand
    private ArrayList<Card> deck;
    private ArrayList<Card> playerHand;
    private ArrayList<Card> dealerHand;

    // buttons for the game
    private JButton dealButton;
    private JButton hitButton;
    private JButton standButton;
    private JPanel mainPanel;

    // panels for the dealer and player
    private JPanel dealerPanel;
    private JPanel playerPanel;
    private JLabel dealerScoreLabel;
    private JLabel playerScoreLabel;

    // money and bet variables
    private int playerMoney = 1000;
    private int currentBet = 0;
    private JLabel moneyLabel;
    private JLabel betLabel;
    private JPanel betButtonPanel;
    private boolean betPlaced = false;

    // panel for the buttons
    private JPanel buttonPanel;

    // avatar selector and label
    private JComboBox<String> avatarSelector;
    private JLabel avatarLabel;
    private static final int AVATAR_SIZE = 80;  // pixel size of the avatar

    // deck selector and label
    private JComboBox<String> deckSelector;
    private int numberOfDecks = 1;
    private JLabel deckCountLabel;

    public BlackjackGame() {
        // Set up the main window
        setTitle("Blackjack");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setResizable(false); // prevent the window from being resized

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

        // setup the betting UI
        setupBettingUI();

        dealButton.setEnabled(false); // Only enable after bet is placed
        hitButton.setEnabled(false); // disable hit button
        standButton.setEnabled(false); // disable stand button

        add(mainPanel); // add the main panel to the frame
        initializeDeck();

        // Add avatar UI before other components
        setupAvatarUI();
        setupDeckSelectorUI(); // setup the deck selector UI
    }

    // private method to setup the betting UI, before the game starts
    private void setupBettingUI() {
        // Create betting panel
        JPanel bettingPanel = new JPanel(new BorderLayout());
        bettingPanel.setBackground(new Color(0, 100, 0));
        
        // Create labels panel
        JPanel labelsPanel = new JPanel(new FlowLayout());
        labelsPanel.setBackground(new Color(0, 100, 0));
        
        // create the money and bet labels
        moneyLabel = new JLabel("Money: $" + playerMoney);
        betLabel = new JLabel("Current Bet: $" + currentBet);
        moneyLabel.setForeground(Color.WHITE);
        betLabel.setForeground(Color.WHITE);
        
        // add the money and bet labels to the labels panel
        labelsPanel.add(moneyLabel);
        labelsPanel.add(betLabel);
        
        // create the bet buttons
        betButtonPanel = new JPanel();
        betButtonPanel.setBackground(new Color(0, 100, 0));
        
        // create the bet amounts
        int[] betAmounts = {1, 5, 20, 50, 100};

        //create five buttons for the bet amounts
        for (int amount : betAmounts) {
            JButton betButton = new JButton("$" + amount);
            betButton.addActionListener(e -> placeBet(amount));
            betButtonPanel.add(betButton);
        }
        
        // create a combined bottom panel
        JPanel bottomPanel = new JPanel(new GridLayout(3, 1));
        bottomPanel.setBackground(new Color(0, 100, 0));
        
        // add the components to the bottom panel
        bottomPanel.add(labelsPanel);
        bottomPanel.add(betButtonPanel);
        bottomPanel.add(buttonPanel);  // This is the panel with deal/hit/stand buttons
        
        // update the main panel layout
        mainPanel.add(dealerPanel, BorderLayout.NORTH);
        mainPanel.add(playerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    // private method to place a bet
    private void placeBet(int amount) {
        // check if the bet is valid
        if (amount <= playerMoney && !betPlaced) {
            currentBet += amount; // add the bet to the current bet
            playerMoney -= amount; // subtract the bet from the player's money
            updateMoneyLabels(); // update the money labels

            // enable the deal button if there's a bet
            dealButton.setEnabled(currentBet > 0);
        }
    }

    // private method to update the money labels
    private void updateMoneyLabels() {
        moneyLabel.setText("Money: $" + playerMoney); // update the money label
        betLabel.setText("Current Bet: $" + currentBet);
    }

    // main method to run the game
    public static void main(String[] args) {
        // Run the GUI in the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // create a new blackjack game
            BlackjackGame game = new BlackjackGame();
            game.setVisible(true); // make the game visible
        });
    }

    // private method to handle the deal button click
    private void dealButtonClicked() {
        betPlaced = true; // set the bet placed to true
        betButtonPanel.setEnabled(false); // disable the bet button panel
        for (Component c : betButtonPanel.getComponents()) {
            c.setEnabled(false); // disable the bet buttons
        }
        deckSelector.setEnabled(false);  // disable the deck selector
        playerHand.clear(); // clear the player's hand
        dealerHand.clear(); // clear the dealer's hand
        dealerPanel.removeAll(); // remove all components from the dealer panel
        playerPanel.removeAll(); // remove all components from the player panel
        dealerPanel.add(dealerScoreLabel); // add the dealer score label to the dealer panel
        playerPanel.add(playerScoreLabel); // add the player score label to the player panel

        // initialize a new deck if needed
        if (deck.size() < 10)
            initializeDeck();

        // deal the initial cards
        dealCard(playerHand, true);
        dealCard(dealerHand, true);
        dealCard(playerHand, true);
        dealCard(dealerHand, false); // Dealer's hole card

        // update the UI
        updateScores();
        
        enableGameButtons(true); // enable the game buttons
        dealButton.setEnabled(false); // disable the deal button

        revalidate(); // revalidate the components
        repaint(); // repaint the components
    }

    // private method to handle the hit button click
    private void hitButtonClicked() {
        dealCard(playerHand, true); // deal a card to the player
        updateScores(); // update the scores

        if (calculateScore(playerHand, false) > 21) {
            endGame("Bust! Dealer wins!"); // end the game if the player busts
        }
    }

    // private method to handle the stand button click
    private void standButtonClicked() {
        // reveal the dealer's hole card
        revealDealerCard();
        updateScores(); // Now shows full dealer score

        // dealer must hit on 16 and below, stand on 17 and above
        while (calculateScore(dealerHand, false) < 17) {
            dealCard(dealerHand, true); // deal a card to the dealer
            updateScores();
        }

        determineWinner();
    }

    // private method to deal a card to a hand
    private void dealCard(ArrayList<Card> hand, boolean faceUp) {
        Card card = deck.remove(deck.size() - 1); // remove the last card from the deck
        hand.add(card);

        JLabel cardLabel = new JLabel(faceUp ? card.getCardImage() : new ImageIcon("cards/53.png")); // create a card label
        if (hand == dealerHand) {
            dealerPanel.add(cardLabel); // add the card label to the dealer panel
        } else {
            playerPanel.add(cardLabel); // add the card label to the player panel
        }
    }

    // private method to reveal the dealer's card
    private void revealDealerCard() {
        dealerPanel.removeAll(); // remove all components from the dealer panel
        dealerPanel.add(dealerScoreLabel); // add the dealer score label to the dealer panel
        for (Card card : dealerHand) {
            dealerPanel.add(new JLabel(card.getCardImage())); // add the card label to the dealer panel
        }
        revalidate(); // revalidate the components
        repaint(); // repaint the components
    }

    // private method to calculate the score of a hand
    private int calculateScore(ArrayList<Card> hand, boolean dealerHidden) {
        int score = 0;
        int aces = 0;
        int jokers = 0;
        
        // for the dealer's hand, skip the last card (hole card) if it's still hidden
        int cardCount = dealerHidden && hand == dealerHand ? hand.size() - 1 : hand.size();
        
        // first pass: count non-Ace, non-Joker cards
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
        
        // second pass: optimize Joker values (1, 10, or 11)
        for (int i = 0; i < jokers; i++) {
            if (score <= 10) {
                score += 11;  // use 11 if it won't bust
            } else if (score <= 19) {
                score += 1;   // use 1 if 11 would bust but still room for more
            } else {
                score += 1;   // use 1 if close to 21
            }
        }
        
        // third pass: handle Aces after Jokers
        for (int i = 0; i < aces; i++) {
            if (score <= 10) {
                score += 11;
            } else {
                score += 1;
            }
        }
        
        return score;
    }

    // private method to update the scores
    private void updateScores() {
        // for the dealer, only show the score of the face-up card until stand is clicked
        int dealerScore = calculateScore(dealerHand, true);
        int playerScore = calculateScore(playerHand, false);

        dealerScoreLabel.setText("Dealer: " + dealerScore);
        playerScoreLabel.setText("Player: " + playerScore);
    }

    // private method to determine the winner
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

    // private method to end the game
    private void endGame(String message) {
        if (message.contains("You win")) {
            playerMoney += currentBet * 2; // Win pays 1:1
        } else if (message.contains("Push")) {
            playerMoney += currentBet; // Return the bet
        }
        // if the dealer wins, don't return any money (bet was already deducted when placed)

        // check if the player needs a refill
        if (playerMoney <= 0) {
            JOptionPane.showMessageDialog(this, "You're out of money! Here's a fresh $1000!");
            playerMoney = 1000;
        }

        currentBet = 0; // reset the current bet
        betPlaced = false; // reset the bet placed
        updateMoneyLabels(); // update the money labels

        // re-enable the betting buttons
        for (Component c : betButtonPanel.getComponents()) {
            c.setEnabled(true);
        }

        deckSelector.setEnabled(true);  // re-enable the deck selector

        JOptionPane.showMessageDialog(this, message); // show the message
        enableGameButtons(false); // enable the game buttons
        dealButton.setEnabled(false); // don't enable the deal button until a new bet is placed
    }

    // private method to enable the game buttons
    private void enableGameButtons(boolean enabled) {
        hitButton.setEnabled(enabled);
        standButton.setEnabled(enabled);
    }

    // private method to setup the avatar UI
    private void setupAvatarUI() {
        // create the avatar panel in the top right
        JPanel avatarPanel = new JPanel(new BorderLayout(5, 5));
        avatarPanel.setBackground(new Color(0, 100, 0));
        
        // create the avatar label
        avatarLabel = new JLabel();
        updateAvatarImage(1);  // start with the first avatar
        
        // create the avatar selector
        String[] avatarOptions = new String[12];
        for (int i = 0; i < 12; i++) {
            avatarOptions[i] = "Avatar " + (i + 1);
        }
        avatarSelector = new JComboBox<>(avatarOptions);
        avatarSelector.setPreferredSize(new Dimension(100, 25));
        
        // add a change listener
        avatarSelector.addActionListener(e -> {
            int selected = avatarSelector.getSelectedIndex() + 1;
            updateAvatarImage(selected);
        });
        
        // add the components to the avatar panel
        avatarPanel.add(avatarLabel, BorderLayout.CENTER);
        avatarPanel.add(avatarSelector, BorderLayout.SOUTH);
        
        // create a wrapper panel for the dealer area that includes the avatar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 100, 0));
        topPanel.add(dealerPanel, BorderLayout.CENTER);
        topPanel.add(avatarPanel, BorderLayout.EAST);
        
        // update the main panel to use the new top panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
    }
    
    // private method to update the avatar image
    private void updateAvatarImage(int avatarNumber) {
        // create the original icon
        ImageIcon originalIcon = new ImageIcon("avatar/p" + avatarNumber + ".png");
        Image image = originalIcon.getImage();
        
        // scale the image to the desired size while maintaining the aspect ratio
        Image scaledImage = image.getScaledInstance(AVATAR_SIZE, AVATAR_SIZE, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        
        avatarLabel.setIcon(scaledIcon); // set the avatar label to the scaled icon
    }

    // private method to setup the deck selector UI
    private void setupDeckSelectorUI() {
        // create the deck selector panel in the top left
        JPanel deckPanel = new JPanel(new BorderLayout(5, 5));
        deckPanel.setBackground(new Color(0, 100, 0));
        
        // create the deck count label
        deckCountLabel = new JLabel("Current Deck(s): 1");
        deckCountLabel.setForeground(Color.WHITE);
        deckCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // create the deck selector
        String[] deckOptions = {"1 Deck", "2 Decks", "4 Decks", "6 Decks"};
        deckSelector = new JComboBox<>(deckOptions);
        deckSelector.setPreferredSize(new Dimension(100, 25));
        
        // add a change listener
        deckSelector.addActionListener(e -> {
            // only allow changes if no cards are dealt
            if (!betPlaced) {
                numberOfDecks = Integer.parseInt(((String)deckSelector.getSelectedItem()).split(" ")[0]);
                deckCountLabel.setText("Current Deck(s): " + numberOfDecks);
                initializeDeck(); // Reinitialize with new deck count
            } else {
                // reset the selection if cards are dealt
                deckSelector.setSelectedIndex(getIndexForDeckCount(numberOfDecks));
            }
        });
        
        // add the components to the deck panel
        deckPanel.add(deckCountLabel, BorderLayout.CENTER);
        deckPanel.add(deckSelector, BorderLayout.SOUTH);
        
        // update the top panel to include the deck selector
        JPanel topPanel = (JPanel)mainPanel.getComponent(0); // get the existing top panel
        topPanel.add(deckPanel, BorderLayout.WEST);
    }
    
    // private helper method to get the combo box index from the deck count
    private int getIndexForDeckCount(int decks) {
        // switch statement to get the index for the deck count
        switch(decks) {
            // return the index for the deck count
            case 2: return 1;
            case 4: return 2;
            case 6: return 3;
            default: return 0;
        }
    }

    // private method to initialize the deck
    private void initializeDeck() {
        deck.clear(); // clear the deck
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"}; // suits of the cards
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"}; // ranks of the cards
        
        // add the regular cards for each deck
        for (int d = 0; d < numberOfDecks; d++) {
            for (String suit : suits) {
                for (String rank : ranks) {
                    deck.add(new Card(suit, rank));
                }
            }
            // add two jokers per deck
            deck.add(new Card("Special", "Joker"));
            deck.add(new Card("Special", "Joker"));
        }
        Collections.shuffle(deck); // shuffle the deck  
    }
}
