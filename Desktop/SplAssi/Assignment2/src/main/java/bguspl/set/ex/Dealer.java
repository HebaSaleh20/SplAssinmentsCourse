package bguspl.set.ex;

import bguspl.set.Env;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class manages the dealer's threads and data
 */
public class Dealer implements Runnable {

    /**
     * The game environment object.
     */
    public final Env env;

    /**
     * Game entities.
     */
    private final Table table;
    private final Player[] players;
    private Thread[] playersThreads;
    /**
     * The list of card ids that are left in the dealer's deck.
     */
    private final List<Integer> deck;

    /**
     * True iff game should be terminated due to an external event.
     */
    public volatile boolean terminate;

    /**
     * The time when the dealer needs to reshuffle the deck due to turn timeout.
     */
    private long reshuffleTime = Long.MAX_VALUE;
    public Timer timer;

    public Dealer(Env env, Table table, Player[] players) {
        this.env = env;
        this.table = table;
        this.players = players;
        deck = IntStream.range(0, env.config.deckSize).boxed().collect(Collectors.toList());
        this.playersThreads = new Thread[players.length];
        timer = new Timer(env);


    }

    /**
     * The dealer thread starts here (main loop for the dealer thread).
     */
    @Override
    public void run() {
        //start players threads
        for (int i = 0; i < players.length; i++) {
            playersThreads[i] = new Thread(players[i], "Player " + i);
            playersThreads[i].start();
        }

        env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " starting.");
        while (!shouldFinish()) {
            Collections.shuffle(deck);
            placeCardsOnTable();
            updateTimerDisplay(true);
            timerLoop();
            removeAllCardsFromTable();
        }
        announceWinners();
        env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " terminated.");

        for (int i = 0; i < players.length; i++)
            try {
                playersThreads[i].join();
            } catch (InterruptedException ignored) {
            }
    }

    /**
     * The inner loop of the dealer thread that runs as long as the countdown did not time out.
     */
    private void timerLoop() {


        Thread timerHandler = new Thread(timer);
        timer.timerThread = timerHandler;
        timerHandler.start();

        table.isallowed=true;
        while (!terminate && timer.currTime > 0) {
            sleepUntilWokenOrTimeout(timer.currTime);
            //check if timeout, do nothing.
            if(timer.currTime>0) {
                removeCardsFromTable();
                placeCardsOnTable();
            }
        }

        try {
            timerHandler.join();
        } catch (InterruptedException ignored) {
        }

    }

    /**
     * Called when the game should be terminated due to an external event.
     * @pre - All the terminates of the playres are false
     * @post - All the terminates of the playres are true
     */
    public void terminate() {
        // TODO implement
        List<Integer> l = new LinkedList<>();
        for (int i = 0; i < table.slotToCard.length; i++){
            if (table.slotToCard[i] != null)
                l.add(table.slotToCard[i]);
        }
        if (env.util.findSets(l, 1).size() == 0 ) {
            terminate = true;
            for(int i=0;i<players.length;i++)
                players[i].terminate();
        }
    }

    /**
     * Check if the game should be terminated or the game end conditions are met.
     *
     * @return true iff the game should be finished.
     */
    public boolean shouldFinish() {
        return terminate || env.util.findSets(deck, 1).size() == 0;
    }


    private boolean IsSet(Player p) {
        int[] a = p.mycards();
        a[0] = table.slotToCard[a[0]];
        a[1] = table.slotToCard[a[1]];
        a[2] = table.slotToCard[a[2]];
        return env.util.testSet(a);

    }
    private void removeSet(Player p)
    {
        int[] a = p.mycards();
        for(int i=0;i<a.length;i++)
            table.removeCard(a[i]);
    }

    /**
     * Checks cards should be removed from the table and removes them.
     */
    private void removeCardsFromTable() {
        // TODO implement
        boolean isfound = false;
        int playerid = -1;
            if (!table.getPlayersSet().isEmpty()) {
                table.isallowed = false;
                playerid = table.getPlayersSet().remove();
                Player p = players[playerid];
                isfound = IsSet(p);
                if (isfound) {
                    //tell player
                    removeSet(p);
                    p.dealersResponse = true;
                    timer.currTime = env.config.turnTimeoutMillis;
                }

                synchronized (p.lock) {
                    //check if legal set, and edit p.dealersResponse.
                    p.lock.notifyAll();
                    p.dealerHasResponded = true;
                    table.isallowed=true;
                }

            }
        }



    /**
     * Check if any cards can be removed from the deck and placed on the table.
     */

    private void placeCardsOnTable() {
        // TODO implement
        LinkedList<Integer> cards = new LinkedList<>();
        for (int i = 0; i < table.slotToCard.length; i++) {
            if (table.slotToCard[i] == null) {
                cards.add(i);
            }
        }
        Collections.shuffle(cards);
        int cardsSize = cards.size();
        for (int i = 0; i < cardsSize; i++) {
            if (!deck.isEmpty())
                table.placeCard(deck.remove(0), cards.get(i));
            else {
                terminate();
            }

        }

    }

    /**
     * Sleep for a fixed amount of time or until the thread is awakened for some purpose.
     */
    private void sleepUntilWokenOrTimeout(long remainingTime) {
        // TODO implement
//        try {
            /*synchronized (table.playersSet) {
                table.isallowed=true;
//                table.playersSet.wait(remainingTime);
            }*/
//        } catch (InterruptedException ignored) {
//        }
    }

    /**
     * Reset and/or update the countdown and the countdown display.
     */
    private void updateTimerDisplay(boolean reset) {
        // TODO implement
        if (reset)
            env.ui.setCountdown(env.config.turnTimeoutMillis, true);
    }

    /**
     * Returns all the cards from the table to the deck.
     */
    private void removeAllCardsFromTable() {

        // TODO implement
        for (int i = 0; i < table.cardToSlot.length; i++)
        {
            if (table.cardToSlot[i] != null) {
                int slot = table.cardToSlot[i];
                int card = table.removeCard1(slot,players);
                deck.add(card);
            }
        }

    }

    /**
     * Check who is/are the winner/s and displays them.
     */
    private void announceWinners() {
        // TODO implement
        int[] listwinner;
        int max = 0;
        for (int i = 0; i < players.length; i++) {
            if (max < players[i].score()) {
                max = players[i].score();
            }
        }
        int j = 0;//instant of check all player
        for (int i = 0; i < players.length; i++) {
            if (max == players[i].score()) {
                j++;
            }
        }
        listwinner=new int[j];
        j=0;
        for (int i = 0; i < players.length; i++) {
            if (max == players[i].score()) {
                listwinner[j]=players[i].id;
                j++;
            }
        }
        env.ui.announceWinner(listwinner);
    }
}