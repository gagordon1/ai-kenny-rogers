package javabot;

import javabot.skeleton.Action;
import javabot.skeleton.ActionType;
import javabot.skeleton.GameState;
import javabot.skeleton.State;
import javabot.skeleton.TerminalState;
import javabot.skeleton.BoardState;
import javabot.skeleton.RoundState;
import javabot.skeleton.Bot;
import javabot.skeleton.Runner;

import java.util.*;
import java.lang.Integer;
import java.lang.String;
import java.util.stream.*;




/**
 * A pokerbot.
 */
public class Player implements Bot {
    // Your instance variables go here.
    String[][] allocation;
    double[] winProb;

    private static double PREFLOP_BET_THRESHOLD1 = .57;
    private static double PREFLOP_BET_THRESHOLD2 = .65;
    private static double POSTFLOP_BET_THRESHOLD = .7;

    private static double MADE_FLUSH_WIN_PROBABILITY = .96; //calculated in google colab
    private static double FLOP_ALMOST_FLUSH_WIN_PROBABILITY = .62;
    private static double TURN_ALMOST_FLUSH_WIN_PROBABILITY = .55;


    private static double CONFIDENT_WIN_THRESHOLD = .9;
    private static double CONFIDENT_WIN_BET_SIZE = .75;

    private static double THRESHOLD_2 = .6;
    private static double THRESHOLD_2_BET_SIZE = .5;

    private static double THRESHOLD_3 = .5;
    private static double THRESHOLD_3_BET_SIZE = .25;

    private static int BIG_BLIND = 2;
    private static int SMALL_BLIND = 1;

    private List<String> suitedPairs = List.of(
            "AK",
            "AQ",
            "AJ",
            "KQ",
            "AT",
            "KJ",
            "QJ",
            "KT",
            "A9",
            "QT",
            "JT",
            "A8",
            "K9",
            "A5",
            "A7",
            "T9",
            "A4",
            "Q9",
            "J9",
            "A6",
            "A3",
            "K8",
            "98",
            "T8",
            "K7",
            "A2",
            "87",
            "Q8",
            "J8",
            "76",
            "97",
            "K6",
            "K5",
            "K4",
            "T7",
            "Q7",
            "65",
            "86",
            "J7",
            "54",
            "Q6",
            "K3",
            "75",
            "64",
            "Q5",
            "K2",
            "96",
            "Q3",
            "Q4",
            "J5",
            "J4",
            "74",
            "53",
            "63",
            "J6",
            "T6",
            "95",
            "T5",
            "Q2",
            "85",
            "43",
            "T4",
            "J3",
            "T3",
            "84",
            "82",
            "42",
            "93",
            "73",
            "J2",
            "92",
            "52",
            "T2",
            "62",
            "83",
            "94",
            "72",
            "32"
            );
    private List<Double> suitedPairWinProbList = List.of(
            0.664,
            0.662,
            0.6358,
            0.6267,
            0.6317,
            0.6134,
            0.5906,
            0.6048,
            0.6109,
            0.5828,
            0.5617,
            0.6098,
            0.5853,
            0.5709,
            0.5909,
            0.5267,
            0.5673,
            0.5603,
            0.5419,
            0.579,
            0.5518,
            0.569,
            0.4854,
            0.5095,
            0.5534,
            0.5435,
            0.4577,
            0.5416,
            0.5222,
            0.4222,
            0.4698,
            0.5491,
            0.5414,
            0.5214,
            0.4877,
            0.5279,
            0.4001,
            0.4302,
            0.5051,
            0.3811,
            0.5138,
            0.5224,
            0.4051,
            0.3891,
            0.5059,
            0.51,
            0.4607,
            0.4942,
            0.4898,
            0.4818,
            0.4567,
            0.3854,
            0.3759,
            0.3787,
            0.4836,
            0.4646,
            0.4465,
            0.4463,
            0.4784,
            0.4248,
            0.3529,
            0.4534,
            0.4608,
            0.4368,
            0.3906,
            0.3737,
            0.3516,
            0.4082,
            0.3678,
            0.4539,
            0.4019,
            0.3547,
            0.4305,
            0.3542,
            0.3814,
            0.413,
            0.3556,
            0.3278
            );
    private List<String> nonSuitedPairs = List.of(
            "AA",
            "KK",
            "QQ",
            "JJ",
            "TT",
            "AK",
            "99",
            "AQ",
            "88",
            "AJ",
            "KQ",
            "77",
            "AT",
            "KJ",
            "66",
            "QJ",
            "55",
            "KT",
            "QT",
            "44",
            "A9",
            "JT",
            "K9",
            "T9",
            "A8",
            "33",
            "Q9",
            "22",
            "J9",
            "J8",
            "98",
            "T8",
            "97",
            "A7",
            "T7",
            "Q8",
            "T6",
            "75",
            "K8",
            "86",
            "K7",
            "85",
            "76",
            "A6",
            "T2",
            "84",
            "62",
            "95",
            "A5",
            "Q7",
            "T5",
            "87",
            "83",
            "65",
            "94",
            "74",
            "54",
            "A4",
            "T4",
            "82",
            "64",
            "42",
            "J7",
            "93",
            "73",
            "53",
            "T3",
            "63",
            "K6",
            "J6",
            "96",
            "92",
            "72",
            "52",
            "Q4",
            "K5",
            "J5",
            "Q3",
            "43",
            "K4",
            "J4",
            "Q6",
            "Q2",
            "J3",
            "A3",
            "Q5",
            "J2",
            "K3",
            "K2",
            "32",
            "A2"
            );
    private List<Double> nonSuitedPairWinProbList = List.of(
            0.8479,
            0.8246,
            0.7968,
            0.7736,
            0.7483,
            0.6505,
            0.7161,
            0.6405,
            0.6889,
            0.6268,
            0.6028,
            0.663,
            0.6188,
            0.5958,
            0.6334,
            0.5708,
            0.5956,
            0.5955,
            0.5574,
            0.5601,
            0.5936,
            0.5379,
            0.571,
            0.4942,
            0.5734,
            0.5301,
            0.5318,
            0.5034,
            0.523,
            0.503,
            0.4567,
            0.4727,
            0.4475,
            0.5849,
            0.4596,
            0.5185,
            0.4366,
            0.3767,
            0.5348,
            0.4178,
            0.5357,
            0.3917,
            0.396,
            0.5705,
            0.396,
            0.3655,
            0.3108,
            0.404,
            0.557,
            0.5006,
            0.4218,
            0.4191,
            0.3457,
            0.3659,
            0.3834,
            0.3588,
            0.3549,
            0.5505,
            0.4121,
            0.3384,
            0.3424,
            0.2902,
            0.4807,
            0.378,
            0.3348,
            0.3354,
            0.3981,
            0.3325,
            0.5318,
            0.4604,
            0.414,
            0.3697,
            0.3163,
            0.3025,
            0.4761,
            0.5152,
            0.449,
            0.461,
            0.3134,
            0.5095,
            0.434,
            0.4962,
            0.444,
            0.4252,
            0.5306,
            0.4796,
            0.4191,
            0.5004,
            0.4904,
            0.2908,
            0.5315
            );


//    1             Royal Flush
//    2             Straight Flush
//    3             Four of a Kind
//    4             Full House
//    5             Flush
//    6             Straight
//    7             Three of a Kind
//    8             Two Pair
//    9             One Pair
//    10            High Card
    private List<Double> handWinProbabilities = List.of(0.0,1.0,.9,.9,.9,.9,.85,.7,.6,.55,.45);
    private List<String> allCards = List.of(
      "As",
      "Ks",
      "Qs",
      "Js",
      "Ts",
      "9s",
      "8s",
      "7s",
      "6s",
      "5s",
      "4s",
      "3s",
      "2s",
      "Ac",
      "Kc",
      "Qc",
      "Jc",
      "Tc",
      "9c",
      "8c",
      "7c",
      "6c",
      "5c",
      "4c",
      "3c",
      "2c",
      "Ah",
      "Kh",
      "Qh",
      "Jh",
      "Th",
      "9h",
      "8h",
      "7h",
      "6h",
      "5h",
      "4h",
      "3h",
      "2h",
      "Ad",
      "Kd",
      "Qd",
      "Jd",
      "Td",
      "9d",
      "8d",
      "7d",
      "6d",
      "5d",
      "4d",
      "3d",
      "2d"
    );

    private Map<Character, Integer> rankToValueConversion = new HashMap<>();

    private Map<Integer, Character> valueToRankConversion = new HashMap<>();

    private List<List<String>> remainingCards = new ArrayList<>();

    private List<List<Set<String>>> possibleOpponentHands = new ArrayList<>();

    private Map<String,Double> suitedPairWinProbMap;

    private Map<String,Double> nonSuitedPairWinProbMap;




    //Maps strategies to their associated starting cards
    //e.g AGGRESSIVE would be mapped to [Ac,As]

    /**
     * Called when a new game starts. Called exactly once.
     */


    public class Hand {

      List<String> contents;
      int strength;

      public Hand (List<String> contents, int strength) {
        this.contents = contents;
        this.strength = strength;
      }

      public Hand() {
        this.contents = new ArrayList<>();
        this.strength = 11;
      }
    }




    public Player() {

        rankToValueConversion.put('2', 1);
        rankToValueConversion.put('3', 2);
        rankToValueConversion.put('4', 3);
        rankToValueConversion.put('5', 4);
        rankToValueConversion.put('6', 5);
        rankToValueConversion.put('7', 6);
        rankToValueConversion.put('8', 7);
        rankToValueConversion.put('9', 8);
        rankToValueConversion.put('T', 9);
        rankToValueConversion.put('J', 10);
        rankToValueConversion.put('Q', 11);
        rankToValueConversion.put('K', 12);
        rankToValueConversion.put('A', 13);

        valueToRankConversion.put(1, '2');
        valueToRankConversion.put(2, '3');
        valueToRankConversion.put(3, '4');
        valueToRankConversion.put(4, '5');
        valueToRankConversion.put(5, '6');
        valueToRankConversion.put(6, '7');
        valueToRankConversion.put(7, '8');
        valueToRankConversion.put(8, '9');
        valueToRankConversion.put(9, 'T');
        valueToRankConversion.put(10, 'J');
        valueToRankConversion.put(11, 'Q');
        valueToRankConversion.put(12, 'K');
        valueToRankConversion.put(13, 'A');


        this.allocation = new String[3][2];
        this.winProb = new double[3];

        //initial strategy for game 1,2 and 3
        this.suitedPairWinProbMap = new HashMap<>();
        for (int i = 0; i < suitedPairs.size(); i++) {
            this.suitedPairWinProbMap.put(suitedPairs.get(i), suitedPairWinProbList.get(i));
        }
        this.nonSuitedPairWinProbMap = new HashMap<>();
        for (int i = 0; i < nonSuitedPairs.size(); i++) {
            this.nonSuitedPairWinProbMap.put(nonSuitedPairs.get(i), nonSuitedPairWinProbList.get(i));
        }
    }

    /**
     * Called when a new round starts. Called State.NUM_ROUNDS times.
     *
     * @param gameState The GameState object.
     * @param roundState The RoundState object.
     * @param active Your player's index.
     */
    public void handleNewRound(GameState gameState, RoundState roundState, int active) {
        //int myBankroll = gameState.bankroll;  // the total number of chips you've gained or lost from the beginning of the game to the start of this round
        //int oppBankroll = gameState.oppBankroll; // ^ but for your opponent
        float gameClock = gameState.gameClock;  // the total number of seconds your bot has left to play this game
        System.out.println(gameClock);
        int roundNum = gameState.roundNum;  // the round number from 1 to State.NUM_ROUNDS
        List<String> myCards = roundState.hands.get(active);
        List<String> optimalAllocation = this.getOptimalAllocation(myCards);

        //Creates the remaining cards field for each board, which start out as the same.
        this.remainingCards = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            this.remainingCards.add(getRemainingCardsAtStart(myCards));
        }


        // This gets all permuations of possible opponent deals.
        List<Set<String>> possibleOpponentHandsAfterDeal = getPossibleOpponentHandsAtStart();

        //Gives each part to each board.
        this.possibleOpponentHands = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
          this.possibleOpponentHands.add(new ArrayList<>(possibleOpponentHandsAfterDeal));
        }



        System.out.println("\n" + "ROUND " + roundNum);
        if (roundNum == State.NUM_ROUNDS) {
            System.out.println(gameClock);
        }
//        List<String> myCards = roundState.hands.get(active);  // your six cards at the start of the round
        //boolean bigBlind = (active == 1);  // true if you are the big blind
    }

    /**
     * Called when a round ends. Called State.NUM_ROUNDS times.
     *
     * @param gameState The GameState object.
     * @param terminalState The TerminalState object.
     * @param active Your player's index.
     */
    public void handleRoundOver(GameState gameState, TerminalState terminalState, int active) {
        //int myDelta = terminalState.deltas.get(active);  // your bankroll change from this round
        //int oppDelta = terminalState.deltas.get(1-active);  // your opponent's bankroll change from this round
        //RoundState previousState = (RoundState)(terminalState.previousState);  // RoundState before payoffs
        //int street = previousState.street;  // 0, 3, 4, or 5 representing when this round ended
        //List<List<String>> myCards = new ArrayList<List<String>>();
        //List<List<String>> oppCards = new ArrayList<List<String>>();
        //for (State terminalBoardState : previousState.boardStates) {
        //    BoardState previousBoardState = (BoardState)(((TerminalState)terminalBoardState).previousState);
        //    myCards.add(previousBoardState.hands.get(active)); // your cards
        //    oppCards.add(previousBoardState.hands.get(1-active)); // opponent's cards or "" if not revealed
        //}

    }

    /*
      Gives all remaining cards for each board that are not within our own or displayed on the board. Should
      only be called at the beginning of a round though, such that this function only takes into account
      cards that we do not own. The cards in the flop, turn, and river will be added to this parameter separately.

      @param myCards the cards that we hold in our hands across all boards.
    */
    private List<String> getRemainingCardsAtStart(List<String> myCards) {
      List<String> cardsRemainingWithoutMine = new ArrayList<>();
      cardsRemainingWithoutMine.addAll(allCards);
      cardsRemainingWithoutMine.removeAll(myCards);
      return cardsRemainingWithoutMine;
    }

    /*
      Gives all permuatations of possible hands that the opponent has, excluding the six cards that we originally hold.
      Should be 1035 total combinations per board at the start.

      @param myCards the cards that we hold in our hands across all boards.
    */
    private List<Set<String>> getPossibleOpponentHandsAtStart() {

      //Use remaining cards to generate all permuations of remaining hands. Should not contain nor iterate over duplcates.
      List<Set<String>> currentHands = new ArrayList<>();
      for ( int i = 0; i < this.remainingCards.get(0).size(); i++) {
        for ( int j = i + 1; j < this.remainingCards.get(0).size(); j++) {

          currentHands.add(Set.of(this.remainingCards.get(0).get(i), this.remainingCards.get(0).get(j)));

        }
      }
      return currentHands;
    }

    /*
      Takes in a card, and for each board removes all possible opponent's hands that contain that card.

      @param currentPossibilites a List of 3 lists of possible opponent hands
      @param card the list of strings representing the card to remove from the opponent hands for that board
    */
    private void eliminateOpponentHandPossibilities(String card, int boardNum) {
      List<Set<String>> opponentHandsOnBoard = this.possibleOpponentHands.get(boardNum);
      for (int i = 0; i < opponentHandsOnBoard.size(); i++) {
        Set<String> currentHand = opponentHandsOnBoard.get(i);
        if (currentHand.contains(card)) {
          opponentHandsOnBoard.remove(i);
        }
      }
    }


    /*
      Checks to determine a  hand's best hand for a given board, via a dictionary that maps the hand type to an integer value.
      Integer Val:          Hand:
          1             Royal Flush
          2             Straight Flush
          3             Four of a Kind
          4             Full House
          5             Flush
          6             Straight
          7             Three of a Kind
          8             Two Pair
          9             One Pair
          10            High Card
    */

    private List<Hand> determineBestHandOnEachBoard(List<String> myCards, List<List<String>> boardCards) {

      List<Hand> result = new ArrayList<>();

      List<List<String>> allCardsForEachBoard = new ArrayList<>();
      for (int i = 0; i < 3; i++) {allCardsForEachBoard.add(new ArrayList<>());}

      for (int i = 0; i < 3; i++) {
        for (String card : boardCards.get(i)) {
          allCardsForEachBoard.get(i).add(card);
        }
        allCardsForEachBoard.get(i).add(myCards.get(i*2));
        allCardsForEachBoard.get(i).add(myCards.get((i*2)+1));
      }

      for (int i = 0; i < 3; i++) {
        result.add(determineBestHandOfGivenCards(allCardsForEachBoard.get(i)));
      }

      return result;
    }



    /*
     * Create SuitToRank and RankToSuit dictionaries.
     *
     * @param cards all cards in your hand and on the board
     * @param fromSuitToRank a dictionary mapping suits visible to their respective ranks,
     *                       initially is empty
     * @param fromRankToSuit a dictionary mapping ranks to their respective available suits,
     *                       initially is empty
     */

    private void createSuitAndRankDictionaries(List<String> cards, Map<Character, List<Character>> fromSuitToRank, Map<Character, List<Character>> fromRankToSuit) {

        for (String card : cards) {
          char rank = card.charAt(0);
          char suit = card.charAt(1);
          fromSuitToRank.putIfAbsent(suit, new ArrayList<>());
          fromSuitToRank.get(suit).add(rank);
          fromRankToSuit.putIfAbsent(rank, new ArrayList<>());
          fromRankToSuit.get(rank).add(suit);
        }
    }


    private Hand getBestFlushHand(List<Character> cardsOfSameSuit, Character suit) {
      // System.out.println("getting best flush...");
      List<Integer> ranks = new ArrayList<>();
      for (Character c : cardsOfSameSuit) {
        ranks.add(rankToValueConversion.get(c));
      }


      Collections.sort(ranks);
      List<String> content = new ArrayList<>();
      for (Integer r : ranks) {
        content.add(Character.toString(valueToRankConversion.get(r)) + Character.toString(suit));
      }
      Hand toReturn = new Hand(content.subList(content.size() - 5, content.size()), 5);
      // System.out.println(toReturn.contents.toString());
      return toReturn;
    }

    /*
     * Determines if a flush exists from a given pair of cards.
     *
     * @param cards a list of cards on the board and in your hand
     * @param fromSuitToRank map from suits to their resoective suits available.
     */
    private Hand determineIfFlushExists(List<String> cards, Map<Character, List<Character>> fromSuitToRank) {
        // System.out.println("determiningIfFlushExists...");
        Hand flushExists = new Hand();
        for (Character suit : fromSuitToRank.keySet()) {
            if (fromSuitToRank.get(suit).size() >= 5) {
              flushExists = getBestFlushHand(fromSuitToRank.get(suit), suit);
            }
        }
        // System.out.println(flushExists.contents.toString());
        return flushExists;
    }

    /**
     * determines all Pairs from a given set of cards
     * @param allRanks an empty array, which will be mutated with all ranks available
     * @param fromRankToSuit a dictionary mapping ranks to all suits available for that rank
     * @return  the set of all pairs for a given set of cards
     */
    private Map<Integer, List<Character>> findCardFrequencies(List<Integer> allRanks, Map<Character, List<Character>> fromRankToSuit) {

        Map<Integer, List<Character>> freqDict = new HashMap<>();
        for (Character rank : fromRankToSuit.keySet()) {
            freqDict.putIfAbsent(fromRankToSuit.get(rank).size(), new ArrayList<>());
            freqDict.get(fromRankToSuit.get(rank).size()).add(rank);
            if (!allRanks.contains(rankToValueConversion.get(rank))) {allRanks.add(rankToValueConversion.get(rank));}
        }
        return freqDict;
    }

    /**
     * Determines if a straight exists via a set of ranks.
     * @param allRanks a list of available ranks
     * @return true if a straight exists, otherwise false
     */
    private Hand determineIfStraightExists(List<Integer> allRanks, Map<Character, List<Character>> fromRankToSuit) {
        // System.out.println("Determining if straight exists...");
        boolean straightExists = false;
        Collections.sort(allRanks);

        int counter = 0;
        int end = 4;
        //IMPORTANT: CURRENTLY ONLY COUNTS THE STRAIGHTS THAT EXCLUDE AN ACE ACTING AS A ONE.
        while (end < allRanks.size()) {
          if (allRanks.get(end) - allRanks.get(counter) == 4) {
            straightExists = true;
            break;
          }
          counter++;
          end++;
        }

        Hand result = new Hand();
        List<String> content = new ArrayList<>();
        if (straightExists) {
          for (int i = counter; i <= end; i++) {
            content.add(Character.toString(valueToRankConversion.get(allRanks.get(i))) + Character.toString(fromRankToSuit.get(valueToRankConversion.get(allRanks.get(i))).get(0)));
          }
          result.contents = content;
          result.strength = 6;
        }
        // System.out.println(result.contents.toString());
        return result;
    }


    private boolean isRoyalFlush(Hand flush) {
      Integer starting = 9;
      for (String c : flush.contents) {
        if (rankToValueConversion.get(c.charAt(0)) == starting) {
          starting++;
        } else {
          return false;
        }
      }
      return true;
    }


    private Hand getBestHandForPairs(Map<Integer, List<Character>> freq, Map<Character, List<Character>> fromRankToSuit, List<Integer> allRanks) {

      //First we have to sort each one, then iterate downwards from 4 to see if frequency is available.
      // System.out.println(1);

      for (Integer i : freq.keySet()) {
        freq.put(i, freq.get(i).stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList()));
      }

      List<String> resultingHand = new ArrayList<>();

      //Four of a kind
      if (freq.containsKey(4)) {

        for (Character suit : fromRankToSuit.get(freq.get(4).get(0))) {
          resultingHand.add(Character.toString(freq.get(4).get(0)) + Character.toString(suit));
        }

        for (int i = allRanks.size() - 1; i >= 0; i--) {
          if (valueToRankConversion.get(allRanks.get(i)) != freq.get(4).get(0)) {
            resultingHand.add(Character.toString(valueToRankConversion.get(allRanks.get(i))) + Character.toString(fromRankToSuit.get(valueToRankConversion.get(allRanks.get(i))).get(0)));
            break;
          }
        }
        // System.out.println(resultingHand.toString());
        return new Hand(resultingHand, 3);

        //Three of a Kind
      } else if (freq.containsKey(3) && !freq.containsKey(2)) {
        for (Character suit : fromRankToSuit.get(freq.get(3).get(0))) {
          resultingHand.add(Character.toString(freq.get(3).get(0)) + Character.toString(suit));
        }

        for (int i = allRanks.size() - 1; i >= 0; i--) {
          if (valueToRankConversion.get(allRanks.get(i)) != freq.get(3).get(0)) {
            resultingHand.add(Character.toString(valueToRankConversion.get(allRanks.get(i))) + Character.toString(fromRankToSuit.get(valueToRankConversion.get(allRanks.get(i))).get(0)));
            if (resultingHand.size() == 5) break;
          }
        }
        // System.out.println(resultingHand.toString());
        return new Hand(resultingHand, 7);

      } else if (freq.containsKey(3) && freq.containsKey(2)) {
        for (Character suit : fromRankToSuit.get(freq.get(3).get(0))) {
          resultingHand.add(Character.toString(freq.get(3).get(0)) + Character.toString(suit));
        }

        for (Character suit : fromRankToSuit.get(freq.get(2).get(0))) {
          resultingHand.add(Character.toString(freq.get(2).get(0)) + Character.toString(suit));
        }
        // System.out.println(resultingHand.toString());
        return new Hand(resultingHand, 4);

      //Pair, two pair
    } else if (freq.containsKey(2)) {
        int currentHandStrength = 9;
        Set<Character> seenPairs = new HashSet<>();

        for (Character rank : freq.get(2)) {
          for (Character suit : fromRankToSuit.get(rank)) {
            resultingHand.add(Character.toString(rank) + Character.toString(suit));
            seenPairs.add(rank);
          }
          if (resultingHand.size() == 4) {
            currentHandStrength = 8;
            break;
          }
        }

        for (int i = allRanks.size() - 1; i >= 0; i--) {
          if (!seenPairs.contains(valueToRankConversion.get(allRanks.get(i)))) {
            resultingHand.add(Character.toString(valueToRankConversion.get(allRanks.get(i))) + Character.toString(fromRankToSuit.get(valueToRankConversion.get(allRanks.get(i))).get(0)));
            if (resultingHand.size() == 5) break;
          }
        }
        // System.out.println(resultingHand.toString());
        return new Hand(resultingHand, currentHandStrength);
      } else {
        for (int i = allRanks.size() - 1; i >= 0; i--) {
          resultingHand.add(Character.toString(valueToRankConversion.get(allRanks.get(i))) + Character.toString(fromRankToSuit.get(valueToRankConversion.get(allRanks.get(i))).get(0)));
          if (resultingHand.size() == 5) break;
        }
        // System.out.println(resultingHand.toString());
        return new Hand(resultingHand, 10);
      }
    }

    /*
      Given a list of cards,
    */
    private Hand determineBestHandOfGivenCards(List<String> cards) {

        if (cards.size() == 0) return new Hand();

        /*
        Relationships between pokerhands to leverage:
          1.) Royal Flush, Straight Flush are all Flushes (1,2,5)
          2.) Two of a Kind gives rise to potentially 3 of a kind, four of a kind (9 -> 7-> 3)
          3.) Two pair gives rise to a full house potentially (8 -> 4)
          4.) High Card is alone (10)
          5.) Straight is alone (6)
      */



      Map<Character, List<Character>> fromSuitToRank = new HashMap<>();
      Map<Character, List<Character>> fromRankToSuit = new HashMap<>();

      createSuitAndRankDictionaries(cards, fromSuitToRank, fromRankToSuit);


      //Determine if bases for each category are met.
      Hand flushExists = determineIfFlushExists(cards, fromSuitToRank);

      List<Integer> allRanks = new ArrayList<>();
      Map<Integer, List<Character>> allFrequencies = findCardFrequencies(allRanks, fromRankToSuit);

      //Will contain rank of any pairs possible, so could be up to a length of 4.

      Hand straightExists = determineIfStraightExists(allRanks, fromRankToSuit);


      Hand bestHand = new Hand();
      if (flushExists.contents.size() != 0) {
        bestHand = flushExists;
        if (straightExists.contents.size() != 0) {
          bestHand.strength = 2;
          if (isRoyalFlush(flushExists)) bestHand.strength = 1;
        }
      }

      if (allFrequencies.keySet().size() > 1) {

        Hand potentialBest = getBestHandForPairs(allFrequencies, fromRankToSuit, allRanks);
        if (potentialBest.strength < bestHand.strength) {
          bestHand = potentialBest;
        }

      }

      if (straightExists.strength < bestHand.strength) {
        bestHand = straightExists;
      }


      return bestHand;
    }

    // private int determineWinnerOfEqualHands(List<String> handOne, List<String> handTwo, List<List<String>> boardCards, int boardNum) {
    //
    // }

    /**
     * Given 5,6 or 7 cards, calculate the conditional probability of
     * 1. Getting a flush and 2. winning with that flush.
     * @param cards
     * @return probability of winning with a flush
     */
    private double getFlushWinProb(List<String> cards) {
        if (maxSameSuit(cards) >= 5) {
            //WE HAVE A FLUSH
            return MADE_FLUSH_WIN_PROBABILITY;
        }
        else if (maxSameSuit(cards) == 4) {
            if (cards.size() == 5) {
                return FLOP_ALMOST_FLUSH_WIN_PROBABILITY;
            }
            else if (cards.size() == 6) {
                return TURN_ALMOST_FLUSH_WIN_PROBABILITY;
            }
            else {
                return 0;
            }
        }
        else {
            //NEGLIGIBLE
            return 0;
        }

    }
    /**
     * Returns the max number of cards in the list with the same
     * suit
     * @param cards
     * @return max number of same suited cards
     */
    public int maxSameSuit(List<String> cards) {
        Map<Character,Integer> frequencyMap = new HashMap<>();
        for (String card: cards) {
            char suit = card.charAt(1);
            if (frequencyMap.keySet().contains(suit)) {
                frequencyMap.put(suit, frequencyMap.get(suit) + 1);
            }
            else {
                frequencyMap.put(suit, 1);
            }
        }
        return Collections.max(new ArrayList<>(frequencyMap.values()));
    }

    private boolean determineIfFirstHandBeatsSecondHand(Hand first, Hand second) {
      int sumOfRanksForFirst = 0;
      int sumOfRanksForSecond = 0;
      for (String card : first.contents) {
        sumOfRanksForFirst += rankToValueConversion.get(card.charAt(0));
      }
      for (String card : second.contents) {
        sumOfRanksForSecond += rankToValueConversion.get(card.charAt(0));
      }

      if (sumOfRanksForFirst >= sumOfRanksForSecond) return true;
      return false;

    }

    /**
     * Recalculate win probabilities for each board based on the cards that are
     * showing and the cards we hold.
     * @param myCards cards held where adjacent indices are on one board (0,1),(2,3) etc.
     * @param boardCards cards showing up on the board.
     */
    private List<Hand> recalculateWinProb(List<String> myCards, List<List<String>> boardCards, RoundState roundState, GameState gameState) {

      /*
        A basic idea for this is to calculate the number of current hands that could beat yours at the moment, and divide that by the total # of permuations.


        Following this, we must predict the future with calculating the probability of our outs to make each possible hand, and multiply the results of that
        by the probability of it happening.
      */

      /*
        Step 1: Calculate the current probability of winning, without the probabilities of future cards.
            1.) Determine our best hand.
            2.) Determine all permutations of other hands that the opponent could have (52 cards - 3 cards in flop - 6 cards you own)
            3.) Run through them, and determine how many of them would beat your hand vs how many would not, giving the win
      */


      int street = roundState.street;
      List<Integer> possibleStreets = List.of(0,3,4,5);


      List<Hand> bestHandForMe = determineBestHandOnEachBoard(myCards, boardCards);

      int previousStreet = (street != 0) ? possibleStreets.get(possibleStreets.indexOf(street) - 1) : 0;
      for (int j = 0; j < 3; j++) {
        for (int i = previousStreet; i < street; i++) {
          if (boardCards.get(j).size() != 0) {
            String currentCard = boardCards.get(j).get(i);
            this.remainingCards.get(j).remove(currentCard);
            eliminateOpponentHandPossibilities(currentCard, j);
          }
        }
      }


      for (int i = 0; i < 3; i++) {
        int handsWonOnThisBoard = 0;
        if (boardCards.get(i).size() != 0) {
          for (Set<String> hand : this.possibleOpponentHands.get(i)) {
            List<String> allCardsHere = new ArrayList<>();
            for (String card : boardCards.get(i)) {
              allCardsHere.add(card);
            }
            for (String card : hand) {
              allCardsHere.add(card);
            }

            Hand bestHandForThisDeal = determineBestHandOfGivenCards(allCardsHere);


            if (bestHandForThisDeal.strength > bestHandForMe.get(i).strength || bestHandForThisDeal.strength == 11) {
              handsWonOnThisBoard++;
            } else if (bestHandForThisDeal.strength == bestHandForMe.get(i).strength && bestHandForMe.get(i).strength != 11) {
              if (determineIfFirstHandBeatsSecondHand(bestHandForMe.get(i), bestHandForThisDeal)) handsWonOnThisBoard++;
            }
          }
        }
        double winProbability = (double) handsWonOnThisBoard / (double) this.possibleOpponentHands.get(i).size();
        this.winProb[i] = winProbability;
      }



      return bestHandForMe;


    }

    /**
     * Given the round state, return a list of list of cards where index 0
     * is the first board's up-facing cards and so on.
     * @param roundState the state of the round
     * @return each boards up-facing cards
     */
    private List<List<String>> getBoardCards(RoundState roundState){
        List<State> states = roundState.boardStates;
        List<List<String>> boardCards = new ArrayList<>();
        for(State state: states) {
            List<String> upFacingCards = new ArrayList<>();
            if (!(state instanceof TerminalState)) {
                BoardState boardState = (BoardState) state;
                for (String card: boardState.deck) {
                    if (card.length() == 2) {//valid card
                        upFacingCards.add(card);
                    }
                }
            }
            boardCards.add(upFacingCards);

        }
        return boardCards;

    }

    /**
     * Where the magic happens - your code should implement this function.
     * Called any time the engine needs a triplet of actions from your bot.
     *
     * @param gameState The GameState object.
     * @param roundState The RoundState object.
     * @param active Your player's index.
     * @return Your action.
     */
    public List<Action> getActions(GameState gameState, RoundState roundState, int active) {
        List<Set<ActionType>> legalActions = roundState.legalActions();  // the actions you are allowed to take
        int street = roundState.street;  // 0, 3, 4, or 5 representing pre-flop, flop, turn, or river respectively
        List<String> myCards = roundState.hands.get(active); // your cards across all boards

        List<String> properAllocation = new ArrayList<>();
        for (String[] board : this.allocation) {
          for (String card : board) {
            properAllocation.add(card);
          }
        }
         // the board cards

        // int[] myPips = new int[State.NUM_BOARDS];  // the number of chips you have contributed to the pot on each board this round of betting
        // int[] oppPips = new int[State.NUM_BOARDS];  // the number of chips your opponent has contributed to the pot on each board this round of betting
//         int[] continueCost = new int[State.NUM_BOARDS];  // the number of chips needed to stay in each pot


        int myStack = roundState.stacks.get(active);  // the number of chips you have remaining
        int oppStack = roundState.stacks.get(1-active);
        // int oppStack = roundState.stacks.get(1-active);  // the number of chips your opponent has remaining
        int netLowerRaiseBound = roundState.raiseBounds().get(0);
        int netUpperRaiseBound = roundState.raiseBounds().get(1);  // the maximum value you can raise across all 3 boards
        int netCost = 0;  // to keep track of the net additional amount you are spending across boards this round

        List<Action> myActions = new ArrayList<Action>();

        //GET A LIST OF UP FACING CARDS ON EACH BOARD
        List<List<String>> boardCards = this.getBoardCards(roundState);


        //RECALCULATE WIN PROBABILITIES FOR EACH BOARD AND STORE IN this.winProb
        if (street > 0 ) {
            List<Hand> winProbabilitiesPerBoard = this.recalculateWinProb(properAllocation, boardCards, roundState, gameState);
        }


        for (int i = 0; i < State.NUM_BOARDS; i++) {


            Set<ActionType> legalBoardActions = legalActions.get(i);
            if (legalBoardActions.contains(ActionType.ASSIGN_ACTION_TYPE)) {
                String card1;
                String card2;
                if (i == 0) {
                    card1 = allocation[0][0];
                    card2 = allocation[0][1];
                }
                else if (i == 1) {
                    card1 = allocation[1][0];
                    card2 = allocation[1][1];
                }
                else {
                    card1 = allocation[2][0];
                    card2 = allocation[2][1];
                }

                myActions.add(new Action(ActionType.ASSIGN_ACTION_TYPE, List.of(card1, card2)));

            }
            else if (roundState.boardStates.get(i) instanceof TerminalState) {
                myActions.add(new Action(ActionType.CHECK_ACTION_TYPE));
            }

            else {
                //DECIDE WHAT TO DO BASED ON WIN PROBABILITY

                BoardState state = (BoardState) roundState.boardStates.get(i);
                final int myPips = state.pips.get(active);
                final int oppPips = state.pips.get(1-active);
                final int continueCost = oppPips-myPips;
                final int pot = state.pot;
                final int pipsAvailable = myStack - netCost;
                final double winProbability = winProb[i];
                double potOdds = ((double) continueCost)/(continueCost + pot);
                final int maxContribution = myPips + Collections.min(List.of(pipsAvailable, oppStack + continueCost));
                final int minContribution = myPips + Collections.min(List.of(maxContribution, continueCost +
                        Collections.max(List.of(continueCost, BIG_BLIND))));


                System.out.println("\n");
                System.out.println("CHIPS IN POT: " + pot);
                //ONLY RAISE WHEN no continue cost (able to check)

                if(winProbability > CONFIDENT_WIN_THRESHOLD && legalBoardActions.contains(ActionType.RAISE_ACTION_TYPE)
                        && legalBoardActions.contains(ActionType.CHECK_ACTION_TYPE)) {

                    final int desiredRaise =  (int) (CONFIDENT_WIN_BET_SIZE*pot);
                    final int raiseAmount = Collections.min(
                            List.of(maxContribution, Collections.max(List.of(minContribution, desiredRaise))));

                    myActions.add(new Action(ActionType.RAISE_ACTION_TYPE, raiseAmount));
                    netCost += raiseAmount;
                }
                else if (winProbability > THRESHOLD_2 && legalBoardActions.contains(ActionType.RAISE_ACTION_TYPE)
                        && legalBoardActions.contains(ActionType.CHECK_ACTION_TYPE)){

                    final int desiredRaise = (int) (THRESHOLD_2_BET_SIZE*pot);
                    final int raiseAmount = Collections.min(
                            List.of(maxContribution, Collections.max(List.of(minContribution, desiredRaise))));

                    myActions.add(new Action(ActionType.RAISE_ACTION_TYPE, raiseAmount));
                    netCost += raiseAmount;

                }
                else if (winProbability > THRESHOLD_3 && legalBoardActions.contains(ActionType.RAISE_ACTION_TYPE)
                        && legalBoardActions.contains(ActionType.CHECK_ACTION_TYPE)) {

                    final int desiredRaise = (int) (THRESHOLD_3_BET_SIZE*pot);
                    final int raiseAmount = Collections.min(
                            List.of(maxContribution, Collections.max(List.of(minContribution, desiredRaise))));

                    myActions.add(new Action(ActionType.RAISE_ACTION_TYPE, raiseAmount));
                    netCost += raiseAmount;
                }
                //odds are at least greater than the pot odds, check or call if available.
                else if (winProbability > potOdds) {
                    if (legalBoardActions.contains(ActionType.CHECK_ACTION_TYPE)) {
                        myActions.add(new Action(ActionType.CHECK_ACTION_TYPE));
                    }
                    else {
                        myActions.add(new Action(ActionType.CALL_ACTION_TYPE));
                        netCost += continueCost;
                    }

                }
                //Odds are worse than what it takes to stay in
                else {
                    myActions.add(new Action(ActionType.FOLD_ACTION_TYPE));
                }

            }
        }
        return myActions;
    }

    /**
     * Given a list of cards, determine the optimal allocation and return it as an ordered list
     * where the first two cards represent the first game, second pair represents second game
     * and so on. Also updates allocation to be the 3x2 array of cards. The optimal allocation
     * is where the sum of the win probability of each board pair is maximized. and wp1 <= wp2 <= wp3
     * @param cards list of length 6 containing the string representation of each card e.g "As", "5c"
     * @return allocation of cards
     */
    private List<String> getOptimalAllocation(List<String> cards){
        List<List<String>> allocations = getAllAllocations(cards);
        double maxProb = 0;
        List<String> optimalAllocation = allocations.get(0);

        for (List<String> a : allocations) {
            double winProb1 = getWinProbability(a.get(0), a.get(1));
            double winProb2 = getWinProbability(a.get(2), a.get(3));
            double winProb3 = getWinProbability(a.get(4), a.get(5));
            double probSum = winProb1 + winProb2 + winProb3;

            if ((probSum >= maxProb) && (winProb1<= winProb2) && (winProb2 <= winProb3)){
                optimalAllocation = a;
                maxProb = probSum;
            }
        }
        winProb[0] = getWinProbability(optimalAllocation.get(0),optimalAllocation.get(1));
        winProb[1] = getWinProbability(optimalAllocation.get(2),optimalAllocation.get(3));
        winProb[2] = getWinProbability(optimalAllocation.get(4),optimalAllocation.get(5));
        allocation[0][0] = optimalAllocation.get(0);
        allocation[0][1] = optimalAllocation.get(1);
        allocation[1][0] = optimalAllocation.get(2);
        allocation[1][1] = optimalAllocation.get(3);
        allocation[2][0] = optimalAllocation.get(4);
        allocation[2][1] = optimalAllocation.get(5);
        return optimalAllocation;
    }

    /**
     * Gets all possible allocations of the cards
     * @param cards the 6 cards to shuffle
     * @return a list of all orderings of the 6 cards
     */
    private List<List<String>> getAllAllocations(List<String> cards){
        List<String> c = new ArrayList<>(cards);
        List<List<String>> allocations = new ArrayList<>();
        if (cards.size() == 1) {
            allocations.add(c);
            return allocations;
        }

        for (int i = 0; i < c.size(); i++) {
            String card = c.remove(i);
            List<List<String>> allocAfterRemoved = getAllAllocations(c);
            for (List<String> alloc :allocAfterRemoved) {
                alloc.add(0, card);
                allocations.add(alloc);
            }
            c.add(i, card);
        }
        return allocations;
    }

    /**
     * Given two cards, get the win probability according to pre-calculated values.
     * @param card1 valid representation of a card e.g "As", "5c"
     * @param card2 valid representation of a card
     * @return the win probability in the pre-flop
     */
    private double getWinProbability(String card1, String card2) {
        final String card1Rank = String.valueOf(card1.charAt(0));
        final String card1Suit = String.valueOf(card1.charAt(1));
        final String card2Rank = String.valueOf(card2.charAt(0));
        final String card2Suit = String.valueOf(card2.charAt(1));
        final String try1 = card1Rank + card2Rank;
        final String try2 = card2Rank + card1Rank;
        //IF SUITED
        if(card1Suit.equals(card2Suit)) {
            if (suitedPairWinProbMap.containsKey(try1)) {
                return suitedPairWinProbMap.get(try1);
            }
            else {
                return suitedPairWinProbMap.get(try2);
            }
        }
        //OTHERWISE IF NON SUITED
        else {
            if (nonSuitedPairWinProbMap.containsKey(try1)) {
                return nonSuitedPairWinProbMap.get(try1);
            }
            else {
                return nonSuitedPairWinProbMap.get(try2);
            }

        }
    }



    /**
     * Main program for running a Java pokerbot.
     */
    public static void main(String[] args) {
        Player player = new Player();
        Runner runner = new Runner();

        List<String> myCards = List.of("2d", "3d", "2h", "3h", "5s", "6c", "7c");
        Map<Character, List<Character>> fromSuitToRank = new HashMap<>();
        Map<Character, List<Character>> fromRankToSuit = new HashMap<>();
        player.createSuitAndRankDictionaries(myCards, fromSuitToRank, fromRankToSuit);

        System.out.println("TESTING OF DICTIONARIES: ");
        System.out.println(fromSuitToRank.toString());
        System.out.println(fromRankToSuit.toString());
        System.out.println("--------------------------");

        System.out.println("TESTING CARD FREQUENCIES DICTIONARY:");
        List<Integer> allRanks = new ArrayList<>();
        Map<Integer, List<Character>> allFrequencies = player.findCardFrequencies(allRanks, fromRankToSuit);
        System.out.println(allFrequencies.toString());
        System.out.println(allRanks.toString());
        System.out.println("------------------------------------");

        player.determineIfStraightExists(allRanks, fromRankToSuit);

        System.out.println("TESTING ISROYALFLUSH (WILL PRINT TRUE IF NOT A FLUSH):");
        System.out.println(player.isRoyalFlush(player.determineIfFlushExists(myCards, fromSuitToRank)));
        System.out.println("---------------------");

        System.out.println("TESTING BESTHANDFORPAIRS:");
        Hand best = player.getBestHandForPairs(allFrequencies, fromRankToSuit, allRanks);
        System.out.println(best.contents.toString());
        System.out.println(best.strength);
        System.out.println("-------------------------");

        System.out.println("TEST BEST HAND DETERMINATION:");
        Hand bestHand = player.determineBestHandOfGivenCards(myCards);
        System.out.println(bestHand.contents.toString());
        System.out.println(bestHand.strength);
        System.out.println("----------------------------");






        //
        runner.parseArgs(args);
        runner.runBot(player);

//        List<String> myCards = List.of("As", "Qc", "Th", "9d", "2s", "6h");
//        List<List<String>> boardCards = List.of(
//                List.of("Ah", "Qc", "3h"),
//                List.of("Tc", "Qh", "3s"),
//                List.of("Ac", "Ks", "3d")
//                );
//        player.recalculateWinProb(myCards, boardCards);
    }
}
