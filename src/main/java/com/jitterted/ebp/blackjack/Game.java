package com.jitterted.ebp.blackjack;

import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.ansi;

public class Game {

    public static final int STAY_LIMIT = 16;
    private final Deck deck;

    private final List<Card> dealerHand = new ArrayList<>();
    private final List<Card> playerHand = new ArrayList<>();

    public static void main(String[] args) {
        Game game = new Game();
        dislayWelcomeMessage();
        game.initialDeal();
        game.play();
        System.out.println(ansi().reset());
    }

    private static void dislayWelcomeMessage() {
        System.out.println(ansi()
                                   .bgBright(Ansi.Color.WHITE)
                                   .eraseScreen()
                                   .cursor(1, 1)
                                   .fgGreen().a("Welcome to")
                                   .fgRed().a(" Jitterted's")
                                   .fgBlack().a(" BlackJack"));
    }

    public Game() {
        deck = new Deck();
    }

    public void initialDeal() {

        // deal first round of cards, players first
        playerHand.add(deck.draw());
        dealerHand.add(deck.draw());

        // deal next round of cards
        playerHand.add(deck.draw());
        dealerHand.add(deck.draw());
    }

    public void play() {
        // get Player's decision: hit until they stand, then they're done (or they go bust)
        boolean playerBusted = playerMove(false);
        // Dealer makes its choice automatically based on a simple heuristic (<=16, hit, 17>=stand)
        dealerMove(playerBusted);
        displayFinalGameState();
        displayGameResult(playerBusted);
    }

    private void displayGameResult(boolean playerBusted) {
        if (playerBusted) {
            System.out.println("You Busted, so you lose.  💸");
        } else if (handValueOf(dealerHand) > 21) {
            System.out.println("Dealer went BUST, Player wins! Yay for you!! 💵");
        } else if (handValueOf(dealerHand) < handValueOf(playerHand)) {
            System.out.println("You beat the Dealer! 💵");
        } else if (handValueOf(dealerHand) == handValueOf(playerHand)) {
            System.out.println("Push: You tie with the Dealer. 💸");
        } else {
            System.out.println("You lost to the Dealer. 💸");
        }
    }

    private void dealerMove(boolean playerBusted) {
        if (!playerBusted) {
            while (handValueOf(dealerHand) <= STAY_LIMIT) {
                dealerHand.add(deck.draw());
            }
        }

    }

    private boolean playerMove(boolean playerBusted) {
        while (!playerBusted) {
            displayGameState();
            String playerChoice = inputFromPlayer().toLowerCase();
            if (playerChoice.startsWith("s")) {
                break;
            }
            if (playerChoice.startsWith("h")) {
                playerHand.add(deck.draw());
                if (handValueOf(playerHand) > 21) {
                    playerBusted = true;
                }
            } else {
                System.out.println("You need to [H]it or [S]tand");
            }
        }
        return playerBusted;
    }

    public int handValueOf(List<Card> hand) {
        int handValue = hand
                .stream()
                .mapToInt(Card::rankValue)
                .sum();

        // does the hand contain at least 1 Ace?
        boolean hasAce = hand
                .stream()
                .anyMatch(card -> card.rankValue() == 1);

        // if the total hand value <= 11, then count the Ace as 11 by adding 10
        if (hasAce && handValue < 11) {
            handValue += 10;
        }

        return handValue;
    }

    private String inputFromPlayer() {
        System.out.println("[H]it or [S]tand?");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private void displayGameState() {
        displayDealerState();
        // second card is the hole card, which is hidden
        displayBackOfCard();
        displayPlayerHand();
    }

    private void displayDealerState() {
        dealerhandtoDisplay();
        System.out.println(dealerHand.get(0).display()); // first card is Face Up
    }

    private void displayBackOfCard() {
        System.out.print(
                ansi()
                        .cursorUp(7)
                        .cursorRight(12)
                        .a("┌─────────┐").cursorDown(1).cursorLeft(11)
                        .a("│░░░░░░░░░│").cursorDown(1).cursorLeft(11)
                        .a("│░ J I T ░│").cursorDown(1).cursorLeft(11)
                        .a("│░ T E R ░│").cursorDown(1).cursorLeft(11)
                        .a("│░ T E D ░│").cursorDown(1).cursorLeft(11)
                        .a("│░░░░░░░░░│").cursorDown(1).cursorLeft(11)
                        .a("└─────────┘"));
    }

    private void displayHand(List<Card> hand) {
        System.out.println(hand.stream()
                               .map(Card::display)
                               .collect(Collectors.joining(
                                       ansi().cursorUp(6).cursorRight(1).toString())));
    }

    private void displayFinalGameState() {
        displayDealerHand();
        displayPlayerHand();
    }

    private void displayDealerHand() {
        dealerhandtoDisplay();
        displayHand(dealerHand);
        System.out.println(" (" + handValueOf(dealerHand) + ")");
    }

    private void dealerhandtoDisplay() {
        System.out.print(ansi().eraseScreen().cursor(1, 1));
        System.out.println("Dealer has: ");
    }

    private void displayPlayerHand() {
        System.out.println();
        System.out.println("Player has: ");
        displayHand(playerHand);
        System.out.println(" (" + handValueOf(playerHand) + ")");
    }
}
