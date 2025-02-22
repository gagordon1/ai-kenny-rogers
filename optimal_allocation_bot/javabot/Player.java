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

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.lang.Integer;
import java.lang.String;



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
    private Map<String,Double> suitedPairWinProbMap;
   
    private Map<String,Double> nonSuitedPairWinProbMap;
    
    
   
    
    //Maps strategies to their associated starting cards
    //e.g AGGRESSIVE would be mapped to [Ac,As] 

    /**
     * Called when a new game starts. Called exactly once.
     */
    public Player() {
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
        int roundNum = gameState.roundNum;  // the round number from 1 to State.NUM_ROUNDS
        List<String> myCards = roundState.hands.get(active);
        List<String> optimalAllocation = this.getOptimalAllocation(myCards);
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
    
    /**
     * Recalculate win probabilities for each board based on the cards that are
     * showing and the cards we hold.
     * @param myCards cards held where adjacent indices are on one board (0,1),(2,3) etc.
     * @param boardCards cards showing up on the board.
     */
    private void recalculateWinProb(List<String> myCards, List<List<String>> boardCards) {
        
        for (int i = 0; i < 3; i++) {
            double winProbability = winProb[i];
            String card1 = myCards.get(2*i);
            String card2 = myCards.get(2*i + 1);
        }
        
        
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
         List<List<String>> boardCards = new ArrayList<List<String>>(); // the board cards
        // int[] myPips = new int[State.NUM_BOARDS];  // the number of chips you have contributed to the pot on each board this round of betting
        // int[] oppPips = new int[State.NUM_BOARDS];  // the number of chips your opponent has contributed to the pot on each board this round of betting
//         int[] continueCost = new int[State.NUM_BOARDS];  // the number of chips needed to stay in each pot
         
        
        int myStack = roundState.stacks.get(active);  // the number of chips you have remaining
        // int oppStack = roundState.stacks.get(1-active);  // the number of chips your opponent has remaining
        int netLowerRaiseBound = roundState.raiseBounds().get(0);
        int netUpperRaiseBound = roundState.raiseBounds().get(1);  // the maximum value you can raise across all 3 boards
        int netCost = 0;  // to keep track of the net additional amount you are spending across boards this round 
        
        List<Action> myActions = new ArrayList<Action>();
        if (street > 0){
            this.recalculateWinProb(myCards, boardCards);
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
                
                BoardState state = (BoardState) roundState.boardStates.get(i);
                int myPips = state.pips.get(active);
                int oppPips = state.pips.get(1-active);
                int continueCost = oppPips-myPips;
                int pot = state.pot;
                int pipsAvailable = myStack - netCost;
                
                //CHECK AVAILABLE
                if (legalBoardActions.contains(ActionType.CHECK_ACTION_TYPE)) {
                    double winProbability = winProb[i];
                    if(winProbability > POSTFLOP_BET_THRESHOLD && legalBoardActions.contains(ActionType.RAISE_ACTION_TYPE)){
                        int raiseAmount = 4 + (int) (.1*(pot));
                        
                        if(raiseAmount <= pipsAvailable) { //IF raise amount is valid and we have enough.
                            myActions.add(new Action(ActionType.RAISE_ACTION_TYPE, raiseAmount));
                            netCost += raiseAmount;
                            
                        }
                        else {
                            myActions.add(new Action(ActionType.CHECK_ACTION_TYPE));
                            netCost += continueCost;
                        }
                        
                    }
                    else {
                        myActions.add(new Action(ActionType.CHECK_ACTION_TYPE));
                        netCost += continueCost;
                    }
                    
                    
                    
                }
                
                //CALL REQUIRED
                else {//WE MUST ACT
                    double winProbability = winProb[i];
                    double potOdds = ((double) continueCost)/(continueCost + pot);
                    if(winProbability > PREFLOP_BET_THRESHOLD2 && legalBoardActions.contains(ActionType.RAISE_ACTION_TYPE)){
                        int raiseAmount = myPips + continueCost + (int) (.5*(pot + continueCost));
                        
                        if(raiseAmount > netLowerRaiseBound && raiseAmount <= myStack - netCost) { //IF raise amount is valid and we have enough.
                            myActions.add(new Action(ActionType.RAISE_ACTION_TYPE, raiseAmount));
                            netCost += raiseAmount;
                            
                        }
                        else {
                            myActions.add(new Action(ActionType.CALL_ACTION_TYPE));
                            netCost += continueCost;
                        }
                        
                    }
                    else if (winProb[i] > potOdds) {
                        myActions.add(new Action(ActionType.CALL_ACTION_TYPE));
                        netCost += continueCost;
                    }
                    else {
                        myActions.add(new Action(ActionType.FOLD_ACTION_TYPE));
                    }
                    
                   
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
        runner.parseArgs(args);
        runner.runBot(player);
    }
}