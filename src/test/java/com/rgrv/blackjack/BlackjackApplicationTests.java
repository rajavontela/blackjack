package com.rgrv.blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rgrv.blackjack.pojo.Card;

class BlackjackApplicationTests {

	BlackjackApplication blackjackApplication;
	
	@BeforeEach
	void setUp() throws Exception {
		blackjackApplication = new BlackjackApplication();
	}

	@Test
	void testInitializePlayTable() {
		Map<String, List<Card>> initialized = blackjackApplication.initializePlayTable();
		assertEquals(initialized.size(), 2);
		initialized = blackjackApplication.initializePlayTable("1");
		assertEquals(initialized.size(), 2);
		initialized = blackjackApplication.initializePlayTable("3");
		assertEquals(initialized.size(), 4);
	}

	@Test
	void testPlay() {
		Map<String, List<Card>> initialized = blackjackApplication.initializePlayTable();
		blackjackApplication.play(blackjackApplication.prepareDeck(), initialized, true);
		initialized.forEach((k,v)->{
			if(k.startsWith("Player ")) assertTrue(Integer.valueOf(v.size()).compareTo(2)>=0);
		});
		initialized = blackjackApplication.initializePlayTable("3");
		blackjackApplication.play(blackjackApplication.prepareDeck(), initialized, true);
		initialized.forEach((k,v)->{
			if(k.startsWith("Player ")) assertTrue(Integer.valueOf(v.size()).compareTo(2)>=0);
		});
		initialized = blackjackApplication.initializePlayTable("6");
		blackjackApplication.play(blackjackApplication.prepareDeck(), initialized, true);
		initialized.forEach((k,v)->{
			if(k.startsWith("Player ")) assertTrue(Integer.valueOf(v.size()).compareTo(2)>=0);
		});
	}

	@Test
	void testGetResults() {
		Map<String, List<Card>> mapOfPlayerCards = blackjackApplication.initializePlayTable();
		mapOfPlayerCards.put("Player 1", getCardsUtil("5", "Diamonds", "2", "Diamonds", "8", "Diamonds", "4", "Hearts"));
		mapOfPlayerCards.put("Player 2", getCardsUtil("3", "Diamonds", "10", "Diamonds", "5", "Diamonds"));
		mapOfPlayerCards.put("Player 3", getCardsUtil("3", "Spades", "2", "Hearts", "9", "Hearts", "7", "Spades"));
		mapOfPlayerCards.put("Dealer", getCardsUtil("Q", "Spades", "A", "Spades"));
		List<String> results = blackjackApplication.getResults(mapOfPlayerCards);
		assertTrue(results.contains("Scoring Player 1 has: 19, Dealer has:21. Player 1 lost, Dealer wins."));
		assertTrue(results.contains("Scoring Player 2 has: 18, Dealer has:21. Player 2 lost, Dealer wins."));
		assertTrue(results.contains("Scoring Player 3 has: 21, Dealer has:21. Dealer has REAL BLACKJACK! Dealer wins."));
		mapOfPlayerCards.put("Player 1", getCardsUtil("8", "Diamonds", "K", "Clubs"));
		mapOfPlayerCards.put("Player 2", getCardsUtil("A", "Spades", "Q", "Diamonds"));
		mapOfPlayerCards.put("Player 3", getCardsUtil("9", "Hearts", "7", "Spades", "2", "Spades"));
		mapOfPlayerCards.put("Dealer", getCardsUtil("Q", "Diamonds", "A", "Diamonds"));
		results = blackjackApplication.getResults(mapOfPlayerCards);
		assertTrue(results.contains("Scoring Player 1 has: 18, Dealer has:21. Player 1 lost, Dealer wins."));
		assertTrue(results.contains("Scoring Player 2 has: 21, Dealer has:21. Both have REAL BLACKJACK! TIE#@$!"));
		assertTrue(results.contains("Scoring Player 3 has: 18, Dealer has:21. Player 3 lost, Dealer wins."));
		mapOfPlayerCards.put("Player 1", getCardsUtil("7", "Hearts", "3", "Diamonds", "7", "Clubs"));
		mapOfPlayerCards.put("Player 2", getCardsUtil("3", "Diamonds", "3", "Spades", "9", "Hearts", "3", "Clubs"));
		mapOfPlayerCards.put("Player 3", getCardsUtil("A", "Hearts", "6", "Spades", "10", "Diamonds"));
		mapOfPlayerCards.put("Dealer", getCardsUtil("2", "Hearts", "J", "Hearts", "4", "Clubs", "A", "Clubs"));
		results = blackjackApplication.getResults(mapOfPlayerCards);
		assertTrue(results.contains("Scoring Player 1 has: 17, Dealer has:17. Both TIE!"));
		assertTrue(results.contains("Scoring Player 2 has: 18, Dealer has:17. Player 2 WON!"));
		assertTrue(results.contains("Scoring Player 3 has: 17, Dealer has:17. Both TIE!"));
		mapOfPlayerCards.put("Player 1", getCardsUtil("J", "Diamonds", "3", "Diamonds", "A", "Hearts", "2", "Spades"));
		mapOfPlayerCards.put("Player 2", getCardsUtil("Q", "Diamonds", "10", "Clubs"));
		mapOfPlayerCards.put("Player 3", getCardsUtil("8", "Spades", "3", "Spades", "2", "Clubs", "4", "Diamonds"));
		mapOfPlayerCards.put("Dealer", getCardsUtil("5", "Hearts", "10", "Spades", "8", "Hearts"));
		results = blackjackApplication.getResults(mapOfPlayerCards);
		assertTrue(results.contains("Scoring Player 1 has: 16, Dealer has:23. Dealer BUSTED! Player 1 WINS!"));
		assertTrue(results.contains("Scoring Player 2 has: 20, Dealer has:23. Dealer BUSTED! Player 2 WINS!"));
		assertTrue(results.contains("Scoring Player 3 has: 17, Dealer has:23. Dealer BUSTED! Player 3 WINS!"));
	}

	@Test
	void testDealerStopHitting() {
		assertEquals(blackjackApplication.getMyScore(getCardsUtil("A", "Hearts", "A", "Diamonds", "A", "Clubs", "A", "Spades")), 14);
		Map<String, List<Card>> mapOfPlayerCards = new HashMap<>();
		mapOfPlayerCards.put("Player 1", getCardsUtil("Q", "Hearts", "7", "Clubs"));
		mapOfPlayerCards.put("Player 2", getCardsUtil("9", "Diamonds", "K", "Clubs"));
		mapOfPlayerCards.put("Player 3", getCardsUtil("Q", "Hearts", "7", "Spades"));
		mapOfPlayerCards.put("Dealer", getCardsUtil("5", "Clubs", "10", "Hearts"));
		assertEquals(blackjackApplication.dealerStopHitting(mapOfPlayerCards, 2), false);
		mapOfPlayerCards.put("Dealer", getCardsUtil("5", "Clubs", "10", "Hearts", "4", "Spades"));
		assertEquals(blackjackApplication.dealerStopHitting(mapOfPlayerCards, 2), true);
		mapOfPlayerCards.put("Player 1", getCardsUtil("Q", "Hearts", "A", "Clubs"));
		mapOfPlayerCards.put("Player 2", getCardsUtil("A", "Diamonds", "K", "Clubs"));
		mapOfPlayerCards.put("Player 3", getCardsUtil("Q", "Hearts", "A", "Spades"));
		mapOfPlayerCards.put("Dealer", getCardsUtil("A", "Clubs", "10", "Hearts"));
		assertEquals(blackjackApplication.dealerStopHitting(mapOfPlayerCards, 2), true);
		mapOfPlayerCards.put("Player 1", getCardsUtil("Q", "Hearts", "1", "Clubs"));
		mapOfPlayerCards.put("Player 2", getCardsUtil("9", "Diamonds", "K", "Clubs"));
		mapOfPlayerCards.put("Player 3", getCardsUtil("Q", "Hearts", "7", "Spades"));
		mapOfPlayerCards.put("Dealer", getCardsUtil("A", "Clubs", "10", "Hearts"));
		assertEquals(blackjackApplication.dealerStopHitting(mapOfPlayerCards, 2), true);
		mapOfPlayerCards.put("Player 1", getCardsUtil("Q", "Hearts", "1", "Clubs"));
		mapOfPlayerCards.put("Player 2", getCardsUtil("9", "Diamonds", "K", "Clubs"));
		mapOfPlayerCards.put("Player 3", getCardsUtil("Q", "Hearts", "7", "Spades"));
		mapOfPlayerCards.put("Dealer", getCardsUtil("A", "Clubs", "A", "Hearts"));
		assertEquals(blackjackApplication.dealerStopHitting(mapOfPlayerCards, 2), false);
		mapOfPlayerCards.put("Player 1", getCardsUtil("Q", "Hearts", "1", "Clubs"));
		mapOfPlayerCards.put("Player 2", getCardsUtil("9", "Diamonds", "K", "Clubs"));
		mapOfPlayerCards.put("Player 3", getCardsUtil("Q", "Hearts", "7", "Spades"));
		mapOfPlayerCards.put("Dealer", getCardsUtil("A", "Clubs", "A", "Hearts", "4", "Spades"));
		assertEquals(blackjackApplication.dealerStopHitting(mapOfPlayerCards, 2), false);
	}

	@Test
	void testPrepareDeck() {
		LinkedList<Card> deck = blackjackApplication.prepareDeck();
		assertEquals(52*6, deck.size());
		Map<String, Integer> identicalCardCountMap = deck.stream().map(c->c.getCardValueWithShape()).collect(Collectors.toMap(Function.identity(), card -> 1, Math::addExact));
		identicalCardCountMap.forEach((k,v)->{
			assertEquals(v, 6);
		});
	}

	@Test
	void testGetMyScore() {
		assertEquals(blackjackApplication.getMyScore(getCardsUtil("A", "Hearts", "A", "Diamonds", "A", "Clubs", "A", "Spades")), 14);
		assertEquals(blackjackApplication.getMyScore(getCardsUtil("A", "Hearts", "A", "Diamonds", "A", "Clubs", "4", "Spades")), 17);
		assertEquals(blackjackApplication.getMyScore(getCardsUtil("A", "Hearts", "10", "Diamonds", "A", "Clubs", "A", "Spades")), 13);
		assertEquals(blackjackApplication.getMyScore(getCardsUtil("A", "Hearts", "10", "Diamonds")), 21);
		assertEquals(blackjackApplication.getMyScore(getCardsUtil("A", "Hearts", "J", "Diamonds")), 21);
		assertEquals(blackjackApplication.getMyScore(getCardsUtil("A", "Hearts", "Q", "Diamonds")), 21);
		assertEquals(blackjackApplication.getMyScore(getCardsUtil("A", "Hearts", "K", "Diamonds")), 21);
		assertEquals(blackjackApplication.getMyScore(getCardsUtil("A", "Hearts", "5", "Diamonds", "5", "Clubs")), 21);
	}
	
	private List<Card> getCardsUtil(String... cardValues){
		List<Card> myCards = new ArrayList<>();
		for(int i = 0; i<cardValues.length; i+=2) {
			myCards.add(new Card(cardValues[i], cardValues[i+1]));
		}
		return myCards;
	}

}
