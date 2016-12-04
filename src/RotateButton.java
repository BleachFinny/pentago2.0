import java.awt.Color;

public class RotateButton {

    /**
     * Rotate a block 90 degrees clockwise.
     *
     * @param block
     *            to be rotated
     */
    public static void rotateCW(Marble[][] block) {
        Color[][] temp = new Color[block.length][block[0].length];
        for (int x = 0; x < block.length; x++) {
            for (int y = 0; y < block[0].length; y++) {
                temp[x][y] = block[x][y].getColor();
            }
        }

        for (int x = 0; x < block.length; x++) {
            for (int y = 0; y < block[0].length; y++) {
                block[block[0].length - y - 1][x].setColor(temp[x][y]);
            }
        }
    }

    /**
     * Rotate a block 90 degrees counter-clockwise.
     *
     * @param block
     *            to be rotated
     */
    public static void rotateCCW(Marble[][] block) {
        Color[][] temp = new Color[block.length][block[0].length];
        for (int x = 0; x < block.length; x++) {
            for (int y = 0; y < block[0].length; y++) {
                temp[x][y] = block[x][y].getColor();
            }
        }

        for (int x = 0; x < block.length; x++) {
            for (int y = 0; y < block[0].length; y++) {
                block[y][block.length - x - 1].setColor(temp[x][y]);
            }
        }
    }
}
