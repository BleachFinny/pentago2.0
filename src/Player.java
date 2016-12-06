/**
 * 
 * @author ericzeng
 *
 */
public class Player {
    // data record of player information and statistics
    private final int id;
    private String name;

    private int gamesWon;
    private int gamesLost;

    private int marblePlacements;
    private int blockTurns;

    // rank 1 is stored as 0
    private int rank;

    public Player(int i, String na, int gw, int gl, int mp, int bt, int r) {
        id = i;
        name = na;
        gamesWon = gw;
        gamesLost = gl;
        marblePlacements = mp;
        blockTurns = bt;
        rank = r;
    }

    // accessor methods

    public int getId() {
        // TODO: get rid of me
        return id;
    }

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

    public int getRank() {
        return rank;
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

    public void setRank(int r) {
        rank = r;
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
            return this.getId() == ((Player) o).getId();
        }
        return false;
    }
}
