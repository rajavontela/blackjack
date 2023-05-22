package com.rgrv.blackjack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.rgrv.blackjack.pojo.Card;

@SpringBootApplication
public class BlackjackApplication implements CommandLineRunner {


    private static final String BUSTED = "BUSTED";
	private static final String H = "H";
	private static final String HIT = "HIT";
	private static final String HITS = "hits";
	private static final String STANDS = "stands";
	private static final String DEALER = "Dealer";
	Scanner scanner = new Scanner(System.in);
    private static final List<String> CARDS = Arrays.asList("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K");
    private static final List<String> SHAPES = Arrays.asList("Spades", "Diamonds", "Clubs", "Hearts");
    private static final Set<String> FACE_CARDS = new HashSet<>(Arrays.asList("J", "Q", "K"));
    List<Card> dealerCards = new ArrayList<>();
    Integer numberOfPlayers = 0;
    
	public static void main(String[] args) {
		SpringApplication.run(BlackjackApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Map<String, List<Card>> mapOfPlayerCards = initializePlayTable(args);
		System.out.println("Starting game with "+(mapOfPlayerCards.size()-1)+ ((mapOfPlayerCards.size()-1)>1 ? " players." : " player."));
		System.out.println("Shuffling.");
		play(prepareDeck(), mapOfPlayerCards, false);
		getResults(mapOfPlayerCards).stream().forEach(System.out::println);
	}

	public Map<String, List<Card>> initializePlayTable(String... args) {
		this.numberOfPlayers = findNumberOfPlayersArg(args);
		Map<String, List<Card>> mapOfPlayerCards = new HashMap<>();
		mapOfPlayerCards.put(DEALER, new ArrayList<>());
		for(int i = 1; i <= this.numberOfPlayers; i++) {
			mapOfPlayerCards.put("Player "+i, new ArrayList<>());
		}
		return mapOfPlayerCards;
	}

	public void play(LinkedList<Card> deck, Map<String, List<Card>> mapOfPlayerCards, boolean autoPlay) {
		int roundCounter = 1;
		while(roundCounter <= 2) {//loop of actual play rounds
			for(int playerSrNo = 1; playerSrNo <= mapOfPlayerCards.size(); playerSrNo++) {//loop of given player's in a round
				if(playerSrNo!=mapOfPlayerCards.size()) {//players block
					String player = "Player " + playerSrNo;
					String stance;
					do {
						mapOfPlayerCards.get(player).add(deck.pop());
						if(roundCounter == 1) {
							System.out.println("Dealing to "+player+", "+" card: "+mapOfPlayerCards.get(player));
							break;							
						}else {
							if(amIBusted(mapOfPlayerCards.get(player))) {
								System.out.println("Dealing to "+player+", "+" cards: "+mapOfPlayerCards.get(player) + " BUSTED!!");
								break;
							}else {
								System.out.print("Dealing to "+player+", "+" cards: "+mapOfPlayerCards.get(player) + " Hit or Stand? >");
								stance = autoPlay ? (canPlayerHit(getMyScore(mapOfPlayerCards.get(player))) ? "hit" : "stand") : scanner.next();
							}
						}
					}while(HIT.equalsIgnoreCase(stance) || H.equalsIgnoreCase(stance));
				}else {//dealer block
					String player = DEALER;
					String stance;
					do {//dealer's decision loop
						mapOfPlayerCards.get(player).add(deck.pop());
						if(roundCounter == 1) {
							System.out.println("Dealing to "+player+", card: face down");
							break;
						} else {
							stance = dealerStopHitting(mapOfPlayerCards, roundCounter) ? (getMyScore(mapOfPlayerCards.get(player)) > 21 ? BUSTED : STANDS) : HITS;
							System.out.println("Dealing to "+player+", "+"cards: "+mapOfPlayerCards.get(player) + " "+stance);
							if(STANDS.equalsIgnoreCase(stance) || BUSTED.equalsIgnoreCase(stance)) {
								break;
							}
						}
					}while(HITS.equalsIgnoreCase(stance));
				}
			}
			roundCounter++;
		}
	}
	

	public List<String> getResults(Map<String, List<Card>>  mapOfPlayerCards) {
		List<String> results = new ArrayList<>();
		Map<String, Integer> playersScores = mapOfPlayerCards.entrySet().stream().filter(entry->getMyScore(entry.getValue())<=21).collect(Collectors.toMap(Map.Entry::getKey, e -> getMyScore(e.getValue())));
		Map<String, Integer> playersBusted = mapOfPlayerCards.entrySet().stream().filter(entry->getMyScore(entry.getValue())>21).collect(Collectors.toMap(Map.Entry::getKey, e -> getMyScore(e.getValue())));
		for(int i = 1; i <= (mapOfPlayerCards.size()-1); i++) {
			String playerName = "Player "+i;
			if(playersBusted.containsKey(playerName)) {
				results.add(playerName+" score is:"+playersBusted.get(playerName)+". BUSTED!! Dealer wins.");
			}else {
				Integer playerScore =  playersScores.get(playerName);
				Integer dealerScore = !playersBusted.containsKey(DEALER) ? playersScores.get(DEALER) : playersBusted.get(DEALER);
				String dealerScoreString = "Dealer has:" + dealerScore;
				String winnerString = dealerScore > 21 ? " Dealer BUSTED! "+ playerName + " WINS!": (playerScore > 21 ? " BUSTED!!" :  playerScore == 21 && dealerScore  == 21 && mapOfPlayerCards.get(DEALER).size() == 2 && mapOfPlayerCards.get(playerName).size() > 2 ? " Dealer has REAL BLACKJACK! Dealer wins." : playerScore == 21 && dealerScore == 21 && mapOfPlayerCards.get(DEALER).size() == 2 && mapOfPlayerCards.get(playerName).size() == 2  ? " Both have REAL BLACKJACK! TIE#@$!" : (playerScore < dealerScore && dealerScore <= 21) ? " "+playerName+" lost, Dealer wins." : (playerScore == dealerScore) ? " Both TIE!" : " "+playerName+" WON!");
				results.add("Scoring "+playerName+" has: "+playerScore+ ", "+ dealerScoreString + "." + winnerString);
			}
		}
		return results;
	}
	
	public boolean canPlayerHit(Integer playerScore) {
		return 16 > playerScore;
	}

	public boolean dealerStopHitting(Map<String, List<Card>>  mapOfPlayerCards, Integer roundOfGame) {
		Integer dealerScore = getMyScore(mapOfPlayerCards.get(DEALER));
		boolean busted = dealerScore > 21;
		boolean blackjack = dealerScore == 21;
		boolean minimalLoss = isMinimalLoss(mapOfPlayerCards);
		boolean returnValue = busted ? busted : (blackjack ? true : ((roundOfGame==1) ? false : ((!minimalLoss && dealerScore <= 16) ? false : (!minimalLoss ? true : (minimalLoss ? true : false)))));
		return returnValue;
	}

	private boolean amIBusted(List<Card> myCards) {
		return 21 < getMyScore(myCards);
	}

	private boolean isMinimalLoss(Map<String, List<Card>>  mapOfPlayerCards) {
		Map<String, Integer> playersScores = mapOfPlayerCards.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> getMyScore(e.getValue())));
		int countPlayersBehind = 0;
		Integer dealerScore = playersScores.get(DEALER);
		for(Entry<String, Integer> playerScoreEntry : playersScores.entrySet()) {
			if(!playerScoreEntry.getKey().equalsIgnoreCase(DEALER) && dealerScore > playerScoreEntry.getValue()) {
				countPlayersBehind += 1;
			}
		}
		return countPlayersBehind>((mapOfPlayerCards.keySet().size()-1)/2);
	}

	public LinkedList<Card> prepareDeck() {
		LinkedList<Card> deck = new LinkedList<>();
		int i = 0;
		while(i < 6) {
			for(String card: CARDS) {
				for(String shape : SHAPES) {
					deck.add(new Card(card, shape));
				}
			}
			i++;
		}
		Collections.shuffle(deck);
		return deck;
	}

	private Integer findNumberOfPlayersArg(String[] args) {
		Integer numberOfPlayers = 1;
		for(String arg : args) {
			if(arg!=null && !arg.startsWith("--") && Integer.valueOf(arg) > 0) {
				numberOfPlayers = Integer.valueOf(arg);
			}
		}
		return numberOfPlayers;
	}
	
	public Integer getMyScore(List<Card> cards) {
		Integer score = 0;
		boolean hasAce = false;
		for(Card card : cards) {
			if(hasAce && card.getValue().equalsIgnoreCase("A")) {
				score += 1;
			}
			if(card.getValue().equalsIgnoreCase("A")) {
				hasAce = true;
				continue;
			}
			if(FACE_CARDS.contains(card.getValue())) {
				score += 10;
			}else {
				score += Integer.valueOf(card.getValue());
			}
		}
		if(hasAce) {
			score = score + 11 <= 21 ? score + 11 : score + 1;
		}
		return score;
	}

}
