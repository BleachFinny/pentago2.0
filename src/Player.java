/**
 * 
 * @author ericzeng
 *
 */
public class Player implements Comparable {
    // data record of player information and statistics
    private String name;

    private int gamesWon;
    private int gamesLost;

    private int marblePlacements;
    private int blockTurns;

    public Player(String na, int gw, int gl, int mp, int bt) {
        name = na;
        gamesWon = gw;
        gamesLost = gl;
        marblePlacements = mp;
        blockTurns = bt;
    }

    // accessor methods

    public String getName() {
        return name;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public int getGamesLost() {
        return gamesLost;
    }

    public int getMarblePlacements() {
        return marblePlacements;
    }

    public int getBlockTurns() {
        return blockTurns;
    }

    // setter and incrementing methods

    public void setName(String n) {
        name = n;
    }

    public void incGamesWon() {
        gamesWon++;
    }

    public void incGamesLost() {
        gamesLost++;
    }

    public void incMarblePlacements() {
        marblePlacements++;
    }

    public void incBlockTurns() {
        blockTurns++;
    }

    /**
     * equals compares id of this player to player o. If o is not a player, this
     * will return false
     * 
     * @param o
     *            the object in question
     */
    @Override
    public boolean equals(Object o) {
        if (this.getClass().isInstance(o)) {
            return this.getName() == ((Player) o).getName();
        }
        return false;
    }

    @Override
    public int compareTo(Object o) {
        if (this.getClass().isInstance(o)) {
            return this.getName().compareTo(((Player) o).getName());
        }
        return 0;
    }
}
