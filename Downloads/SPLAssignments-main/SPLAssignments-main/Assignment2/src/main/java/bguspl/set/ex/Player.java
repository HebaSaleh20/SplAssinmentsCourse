package bguspl.set.ex;

import bguspl.set.Env;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * This class manages the players' threads and data
 *
 * @inv id >= 0
 * @inv score >= 0
 */
public class Player implements Runnable {

    /**
     * The game environment object.
     */
    private final Env env;

    /**
     * Game entities.
     */
    private final Table table;

    /**
     * The id of the player (starting from 0).
     */
    public final int id;

    /**
     * The thread representing the current player.
     */
    private Thread playerThread;

    /**
     * The thread of the AI (computer) player (an additional thread used to generate key presses).
     */
    private Thread aiThread;

    /**
     * True iff the player is human (not a computer player).
     */
    private final boolean human;

    /**
     * True iff game should be terminated due to an external event.
     */
    public volatile boolean terminate;

    /**
 * true if the dealer gives a point for this player else if he gives a penalty
 */
    public volatile boolean dealersResponse;

    /**
     * true if the dealer has responded to the wehther he gives a penalty or a point
     */
    public volatile boolean dealerHasResponded;

    public Object lock;
    /**
     * The current score of the player.
     */
    private int score;
    //   private List<Integer> keys;
    public ConcurrentHashMap<Integer, Integer> keys;

    public volatile boolean asleep;

    /**
     * The class constructor.
     *
     * @param env    - the environment object.
     * @param dealer - the dealer object.
     * @param table  - the table object.
     * @param id     - the id of the player.
     * @param human  - true iff the player is a human player (i.e. input is provided manually, via the keyboard).
     */
    public Player(Env env, Dealer dealer, Table table, int id, boolean human) {
        this.env = env;
        this.table = table;
        this.id = id;
        this.human = human;
        terminate = false;
        keys = new ConcurrentHashMap<>();
        dealerHasResponded = false;
        dealersResponse = false;
        lock = new Object();
        asleep = false;
    }

    /**
     * The main player thread of each player starts here (main loop for the player thread).
     */
    @Override
    public void run() {
        playerThread = Thread.currentThread();
        env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + "starting.");
        if (!human) createArtificialIntelligence();

        while (!terminate) {
            // TODO implement main player loop
            //if dealer has responded
            if(dealerHasResponded){
                dealerHasResponded = false;
                if(dealersResponse)
                {
                    keys=new ConcurrentHashMap<>();
                    point();
                }
                else
                {
                    penalty();
                }

                dealersResponse=false;
            }
        }
        if (!human) try {
            aiThread.join();
        } catch (InterruptedException ignored) {
        }
        env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " terminated.");
    }

    /**
     * Creates an additional thread for an AI (computer) player. The main loop of this thread repeatedly generates
     * key presses. If the queue of key presses is full, the thread waits until it is not full.
     */
    private void createArtificialIntelligence() {
        // note: this is a very very smart AI (!)
        aiThread = new Thread(() -> {
            env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " starting.");
            while (!terminate) {
                // TODO implement player key press simulator
                Random r = new Random();
                int key1 = r.nextInt(env.config.tableSize);
                keyPressed(key1);
                int key2 = r.nextInt(env.config.tableSize);
                while(key1==key2)
                    key2=r.nextInt(env.config.tableSize);
                keyPressed(key2);
                int key3 = r.nextInt(env.config.tableSize);
                while(key1==key3 | key2==key3)
                    key3=r.nextInt(env.config.tableSize);
                keyPressed(key3);

                if(!env.util.testSet(this.mycards()))
                {
                    while(asleep){}

                    keyPressed(key1);

                    keyPressed(key2);

                    keyPressed(key3);
                }

                //try {
                  //  synchronized (this) {
                    //    wait();
                    //}
                //} catch (InterruptedException ignored) {
                //}
            }
            env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " terminated.");
        }, "computer-" + id);
        aiThread.start();
    }

    /**
     * Called when the game should be terminated due to an external event.
     * @pre - terminate = false
     * @post -terminate =true
     */
    public void terminate() {
        // TODO implement
        terminate = true;
    }

    /**
     * This method is called when a key is pressed.
     *
     * @param slot - the slot corresponding to the key pressed.
     */
    public void keyPressed(int slot) {
        // TODO implement

        if (asleep) return;
        synchronized (table) {
            if (!table.isallowed) return;

            if (keys.size() < 3 | (keys.size() == 3 & table.slotToPlayer[slot][this.id] == 1)) {
                if (table.slotToCard[slot] != null) {
                    if (table.slotToPlayer[slot][this.id] == 1) {
                        table.removeToken(this.id, slot);
                        keys.remove(slot);
                    } else {
                        if (keys.size() < 3) {
                            keys.put(slot, slot);
                            int tokens = table.placeToken(this.id, slot);

                            //return num of placed tokens from placeToken, and check if 3
                            if (tokens == 3) {
                                synchronized (lock) {
                                    synchronized (table.playersSet) {
                                        table.playersSet.add(this.id);
//                                        table.playersSet.notifyAll();
                                    }
                                    try {
                                       // asleep = true;
                                        lock.wait();
                                       // asleep = false;
                                    } catch (InterruptedException ignored) {
                                    }
                                    dealerHasResponded = true;
                                }
                            }
                        }
                    }

                }

            }

        }
    }

    public int[] mycards() {
        int[] a = new int[3];
        int i=0;
        for(Integer slot: keys.keySet())
            a[i++] = slot;
        return a;
    }

    /**
     * Award a point to a player and perform other related actions.
     *
     * @post - the player's score is increased by 1.
     * @post - the player's score is updated in the ui.
     */
    public void point() {
        // TODO implement
        asleep=true;
        long freezeTime = env.config.pointFreezeMillis;
        while(freezeTime > 0){
            env.ui.setFreeze(id, freezeTime);
            freezeTime -= 1000;
            try {
                Thread.sleep(1000);
            }catch(InterruptedException ignored){}
        }
        env.ui.setFreeze(id, 0);
        asleep=false;
        dealerHasResponded = false;

        int ignored = table.countCards(); // this part is just for demonstration in the unit tests
        env.ui.setScore(id, ++score);
        dealerHasResponded = false;

    }

    /**
     * Penalize a player and perform other related actions.
     * @pre none
     * @post none 
     */
    public void penalty() {
        // TODO implement
        asleep=true;
        long freezeTime = env.config.penaltyFreezeMillis;
        while(freezeTime > 0){
            env.ui.setFreeze(id, freezeTime);
            freezeTime -= 1000;
            try {
                Thread.sleep(1000);
            }catch(InterruptedException ignored){}
        }
        env.ui.setFreeze(id, 0);
        asleep=false;
        dealerHasResponded = false;
//        try {
//        Thread.sleep(5000);



//
//
//        }catch (InterruptedException ignored){
//        }

    }

    public int score() {
        return score;
    }
}