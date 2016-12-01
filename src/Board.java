import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * 
 * @author ericzeng
 *
 */
@SuppressWarnings("serial")
public class Board extends JPanel {

    // constants
    public static final int SIZE = 800;

    // four 2-D arrays representing the four rotating blocks
    // starts with block1 in top right, moving cc-w to block4 in bottom right
    private Marble[][] block1 = new Marble[3][3];
    private Marble[][] block2 = new Marble[3][3];
    private Marble[][] block3 = new Marble[3][3];
    private Marble[][] block4 = new Marble[3][3];

    // players that login;
    private Player player1;
    private Player player2;

    // the player whose's turn it is
    private Player active;

    // status of game (next move to be played)
    private JLabel status;

    public Board(Player p1, Player p2, JLabel s) {
        player1 = p1;
        player2 = p2;
        status = s;
        active = p1; // player1 plays first
    }

    /**
     * Draws the board with the 4 blocks and 9 marbles in each
     */
    private void drawBoard(Graphics g) {
        // g.drawRect(0, 0, WIDTH, HEIGHT);
        // g.drawLine(WIDTH / 2, 0, WIDTH / 2, 0);
        // g.drawLine(0, HEIGHT / 2, 0, HEIGHT / 2);
        setBackground(Color.GRAY);

    }

    private void drawBlock(Graphics g, Marble[][] block, int x, int y) {
        g.setColor(Color.BLACK);
        g.drawRect(x, y, SIZE / 4, SIZE / 4);

        int eleSize = SIZE / 28;
        int xPlacement = eleSize;
        for (int r = 0; r < block.length; r++) {
            int yPlacement = eleSize;
            for (int c = 0; c < block.length; c++) {
                block[r][c].draw(g, xPlacement, yPlacement, eleSize);
                yPlacement += eleSize * 2;
            }
            xPlacement += eleSize * 2;
        }

        g.setColor(Color.BLACK);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(SIZE, SIZE);
    }
}
