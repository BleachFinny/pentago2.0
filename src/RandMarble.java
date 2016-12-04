import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 
 * @author ericzeng
 *
 */
public class RandMarble extends Marble {

    /**
     * Constructor to set an initial color
     * 
     * @param color
     */
    public RandMarble(Color color, int s) {
        super(color, s);
    }

    /**
     * Static method to randomize a given block that is square
     * 
     * @param block
     *            the block to be randomized (must be square)
     */
    public static void randomize(Marble[][] block) {
        ArrayList<Marble> rand = new ArrayList<Marble>();
        for (int r = 0; r < block.length; r++) {
            // block[0] will never null given input invariant
            for (int c = 0; c < block.length; c++) {
                rand.add(block[r][c]);
            }
        }

        Collections.shuffle(rand);

        for (int i = 0; i < rand.size(); i++) {
            block[i / block.length][i % block.length] = rand.get(i);
        }
    }
}
