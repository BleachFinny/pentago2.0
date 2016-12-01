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
    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;

    // four 2-D arrays representing the four rotating blocks
    // starts with block1 in top right, moving cc-w to block4 in bottom right
    private Marble[][] block1 = new Marble[3][3];
    private Marble[][] block2 = new Marble[3][3];
    private Marble[][] block3 = new Marble[3][3];
    private Marble[][] block4 = new Marble[3][3];

    // players that login;
    private Player player1;
    private Player player2;

    // status of game (next move to be played)
    private JLabel status;

    public Board(Player p1, Player p2, JLabel s) {
        player1 = p1;
        player2 = p2;
        status = s;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }
}
