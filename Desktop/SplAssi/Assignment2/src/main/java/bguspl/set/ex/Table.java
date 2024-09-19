package bguspl.set.ex;

import bguspl.set.Env;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * This class contains the data that is visible to the player.
 *
 * @inv slotToCard[x] == y iff cardToSlot[y] == x
 */
public class Table {

    /**
     * The game environment object.
     */
    private final Env env;

    /**
     * Mapping between a slot and the card placed in it (null if none).
     */
    protected final Integer[] slotToCard; // card per slot (if any)

    /**
     * Mapping between a card and the slot it is in (null if none).
     */
    protected final Integer[] cardToSlot; // slot per card (if any)
    /**
    /* Mapping between a slot and the players' tokens that placed on it
    */
    protected int[][] slotToPlayer;
    /**
     /* holds a counter of tokens for every player
     */
    protected int[] tokenscounter;
    /**
     /* players that have a set
     */
    protected LinkedBlockingQueue<Integer> playersSet;
    public boolean isallowed;

    /**
     * Constructor for testing.
     *
     * @param env        - the game environment objects.
     * @param slotToCard - mapping between a slot and the card placed in it (null if none).
     * @param cardToSlot - mapping between a card and the slot it is in (null if none).
     */
    public Table(Env env, Integer[] slotToCard, Integer[] cardToSlot) {

        this.env = env;
        this.slotToCard = slotToCard;
        this.cardToSlot = cardToSlot;
        this.tokenscounter = new int[env.config.players];
        this.slotToPlayer = new int[slotToCard.length][env.config.players];
        this.isallowed=false;
        for (int i = 0; i < slotToPlayer.length; i++) {
            for (int j = 0; j < slotToPlayer[i].length; j++) {
                slotToPlayer[i][j] = 0;
            }
        }
        for (int j = 0; j < tokenscounter.length; j++) {
            tokenscounter[j] = 0;
        }
        this.playersSet = new LinkedBlockingQueue<>();

    }

    public LinkedBlockingQueue<Integer> getPlayersSet() {
        return playersSet;
    }

    /**
     * Constructor for actual usage.
     *
     * @param env - the game environment objects.
     */
    public Table(Env env) {
        this(env, new Integer[env.config.tableSize], new Integer[env.config.deckSize]);
    }

    /**
     * This method prints all possible legal sets of cards that are currently on the table.
     */
    public void hints() {
        List<Integer> deck = Arrays.stream(slotToCard).filter(Objects::nonNull).collect(Collectors.toList());
        env.util.findSets(deck, Integer.MAX_VALUE).forEach(set -> {
            StringBuilder sb = new StringBuilder().append("Hint: Set found: ");
            List<Integer> slots = Arrays.stream(set).mapToObj(card -> cardToSlot[card]).sorted().collect(Collectors.toList());
            int[][] features = env.util.cardsToFeatures(set);
            System.out.println(sb.append("slots: ").append(slots).append(" features: ").append(Arrays.deepToString(features)));
        });
    }

    /**
     * Count the number of cards currently on the table.
     *
     * @return - the number of cards on the table.
     */
    public int countCards() {
        int cards = 0;
        for (Integer card : slotToCard)
            if (card != null)
                ++cards;
        return cards;
    }

    /**
     * Places a card on the table in a grid slot.
     *
     * @param card - the card id to place in the slot.
     * @param slot - the slot in which the card should be placed.
     * @post - the card placed is on the table, in the assigned slot.
     */
    public void placeCard(int card, int slot) {
        try {
            Thread.sleep(env.config.tableDelayMillis);
        } catch (InterruptedException ignored) {
        }
        // TODO implement
        cardToSlot[card] = slot;
        slotToCard[slot] = card;
        env.ui.placeCard(card, slot);
    }

    /**
     * Removes a card from a grid slot on the table.
     *
     * @param slot - the slot from which to remove the card.
     * @pre - the assigned slot is not  null
     * @post - the assigned slot is null.
     */
     
    public void removeCard(int slot) {
        try {
            Thread.sleep(env.config.tableDelayMillis);
        } catch (InterruptedException ignored) {
        }
        // TODO implement
        if (slotToCard[slot]!=null) {
            int card = slotToCard[slot];
            slotToCard[slot] = null;
            cardToSlot[card] = null;
        }
        for (int i = 0; i < slotToPlayer[slot].length; i++) {
            if(slotToPlayer[slot][i]==1) {
                slotToPlayer[slot][i] = 0;
                tokenscounter[i]--;
            }
        }
        env.ui.removeTokens(slot);
        env.ui.removeCard(slot);


    }

    /**
     * Removes a card from a grid slot on the table and returns it .
     *
     * @param slot - the slot from which to remove the card.
     */
    public int removeCard1(int slot,Player[] players) {
        try {
            Thread.sleep(env.config.tableDelayMillis);
        } catch (InterruptedException ignored) {
        }
        // TODO implement
        int card = slotToCard[slot];
        cardToSlot[card] = null;
        slotToCard[slot] = null;
        for (int i = 0; i < slotToPlayer[slot].length; i++) {
            if(slotToPlayer[slot][i]==1) {
                slotToPlayer[slot][i] = 0;
                tokenscounter[i]--;
            }
        }
        for(int i=0;i<players.length;i++)
        {
            players[i].keys.remove(slot);
        }
        env.ui.removeCard(slot);
        env.ui.removeTokens();
        return card;

    }


    /**
     * Places a player token on a grid slot.
     *
     * @param player - the player the token belongs to.
     * @param slot   - the slot on which to place the token.
     * @pre -the assigned slot is not null and no token for this player in the assigned slot.
     * @post - the assigned slot has token for the player. 
    */
    public int placeToken(int player, int slot) {
        // TODO implement
        if (slotToCard[slot] == null)
            throw new RuntimeException("no card here");
        else {
            slotToPlayer[slot][player] = 1;
            tokenscounter[player]++;
//            if (tokenscounter[player] == 3) {
//                playersSet.add(player);
//                synchronized (playersSet) {
//                    playersSet.notifyAll();
//                }


        }
        env.ui.placeToken(player, slot);
        return tokenscounter[player];
    }



    /**
     * Removes a token of a player from a grid slot.
     *
     * @param player - the player the token belongs to.
     * @param slot   - the slot from which to remove the token.
     * @pre -the assigned slot is not null and that has token for this player in the assigned slot.
     * @post -the assigned slot dosn't have a token for this player. 
     * @return - true if a token was successfully removed.
     */
    public boolean removeToken(int player, int slot) {
        // TODO implement
        boolean isremoved = false;
        if (slotToPlayer[slot][player] == 1) {
            isremoved = true;
            slotToPlayer[slot][player] = 0;
            tokenscounter[player]--;
            env.ui.removeToken(player, slot);
        }
        return isremoved;
    }
}