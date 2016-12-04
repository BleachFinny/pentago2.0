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
    public static final int SIZE = 700;
    public static final int BLOCK_SIZE = 3; // note: measured in # of marbles
    public static final int MARBLE_SIZE = SIZE / (BLOCK_SIZE * 4 + 2);

    // four 2-D arrays representing the four rotating blocks
    // starts with block1 in top left; top to bottom, then left to right
    // block 4 will be to bottom right
    private Marble[][] block1 = new Marble[BLOCK_SIZE][BLOCK_SIZE];
    private Marble[][] block2 = new Marble[BLOCK_SIZE][BLOCK_SIZE];
    private Marble[][] block3 = new Marble[BLOCK_SIZE][BLOCK_SIZE];
    private Marble[][] block4 = new Marble[BLOCK_SIZE][BLOCK_SIZE];

    // players that login;
    private Player player1;
    private Player player2;

    // turn status (next move to be played)
    // 0: p1 place | 1: p1 rotate | 2: p2 place | 3: p2 rotate | -1: win
    // turn will increment constantly, multiples of 4 of the options above
    // correspond to that multiple (e.g. 5 -> 1 -> p1 rotate)
    private int turn;

    // status of game label
    private JLabel status;

    /**
     * Board constructor the sets up player objects and turn indicator. Also
     * initializes the blocks.
     * 
     * @param p1
     *            player 1 (white)
     * @param p2
     *            player 2 (black)
     * @param s
     *            the turn status of the game
     */
    public Board(Player p1, Player p2, JLabel s) {
        player1 = p1;
        player2 = p2;
        status = s;
        turn = -1; // advanceTurn() will begin the game at turn = 0

        setBackground(Color.GRAY);
        setLayout(new GridLayout(2, 2));
        setFocusable(false); // default: focus on marbles instead

        initBlock(block1);
        initBlock(block2);
        initBlock(block3);
        initBlock(block4);
        advanceTurn(null);
    }

    /**
     * Initializes a block by surrounding each element with a rigid spacer of
     * the same size (BLOCK_SIZE). Marbles will be initialized to null color,
     * representing an empty space where no player has placed a real marble yet
     * 
     * @param block
     *            the block to be initialized
     * 
     */
    private void initBlock(Marble[][] block) {
        JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(BLOCK_SIZE * 2 + 1, BLOCK_SIZE * 2 + 1));
        grid.setFocusable(false);

        for (int c = 0; c < BLOCK_SIZE * 2 + 1; c++) {
            for (int r = 0; r < BLOCK_SIZE * 2 + 1; r++) {
                // insert spacer if row or column is even
                if (c % 2 == 0 || r % 2 == 0) {
                    insertRigidSpace(grid);
                } else {
                    // insert marble if both r and c are odd
                    block[r / 2][c / 2] = new Marble(null, MARBLE_SIZE);
                    block[r / 2][c / 2].addMouseListener(new MouseAdapter() {

                        @Override
                        public void mouseClicked(MouseEvent e) {
                            Marble m = (Marble) e.getComponent();
                            if (m.getColor() == null) {
                                switch (turn % 4) {
                                case 0:
                                    m.setColor(Color.WHITE);
                                    advanceTurn(null);
                                    repaint();
                                    break;
                                case 2:
                                    m.setColor(Color.BLACK);
                                    advanceTurn(null);
                                    repaint();
                                    break;
                                }
                            }
                        }
                    });
                    grid.add(block[r / 2][c / 2]); // add marble to block grid
                }
            }
        }
        add(grid); // add block grid to board
    }

    /**
     * Rotate a given block a certain direction
     * 
     * @param b
     *            the block number to be rotated
     * @param direction
     *            cw or ccw direction
     * @return if the rotation is valid/successful
     */
    public boolean rotate(int b, String direction) {
        if (turn % 4 == 1 || turn % 4 == 3) {
            Marble[][] block;
            switch (b) {
            case 1:
                block = block1;
                break;
            case 2:
                block = block2;
                break;
            case 3:
                block = block3;
                break;
            case 4:
                block = block4;
                break;
            default:
                System.out.println("ERROR IN ROTATION: CHECK BLOCK NUMBER");
                return false;
            }

            switch (direction) {
            case "CW":
                RotateButton.rotateCW(block);
                break;
            case "CCW":
                RotateButton.rotateCCW(block);
                break;
            default:
                System.out.println("ERROR IN ROTATION: CHECK ROTATION TAGS");
            }
            repaint();
            advanceTurn(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds a rigid area the size of a marble to the given grid. Used for
     * spacing of marbles in initBlock
     * 
     * @param grid
     *            the grid to have the space added to
     */
    private void insertRigidSpace(JPanel grid) {
        grid.add(Box.createRigidArea(new Dimension(MARBLE_SIZE, MARBLE_SIZE)));
    }

    private void advanceTurn(Color c) {
        turn++;
        switch (turn % 4) {
        case 0:
            status.setText("Player 1 (White) place a marble");
            break;
        case 1:
            status.setText("Player 1 (White) rotate a block");
            break;
        case 2:
            status.setText("Player 2 (Black) place a marble");
            break;
        case 3:
            status.setText("Player 2 (Black) rotate a block");
            break;
        case -1:
            if (c == Color.WHITE) {
                status.setText("White Wins!");
            } else if (c == Color.BLACK) {
                status.setText("Black Wins!");
            } else if (c == Color.GRAY) {
                status.setText("Tie!");
            } else {
                status.setText("ERROR: INVALID WIN PLAYER SUGGESTED");
            }
            break;
        }
    }

    /**
     * Start the recursive check algorithm for win conditions of a given color
     * at a given (r,c) of a whole board 2-D array (total)
     * 
     * @param x
     *            the row to start at
     * @param y
     *            the column to start at
     * @param col
     *            the color to check for win
     * @param total
     *            the whole board representation
     * @return if col wins or not
     */
    private boolean checkWinStart(int x, int y, Color col, Marble[][] total) {
        return (checkWinRec(x + 1, y - 1, 0, "NE", col, total)
                || checkWinRec(x + 1, y, 0, "E", col, total)
                || checkWinRec(x + 1, y + 1, 0, "SE", col, total)
                || checkWinRec(x, y + 1, 0, "S", col, total));
    }

    /**
     * Recursive part of win condition checking. Keeps track of a given
     * direction and how many in a row there are. When a marble of a different
     * color is reached, the search uses checkWinStart to reset. Due to
     * symmetry, only four directions needed to be accounted for.
     * 
     * @param x
     *            the row to start at
     * @param y
     *            the column to start at
     * @param inARow
     *            how many marbles of col there have been in direction
     * @param direction
     *            the direction that inARow keeps track of
     * @param col
     *            the color to check for win
     * @param total
     *            the whole board representation
     * @return if col wins or not
     */
    private boolean checkWinRec(int x, int y, int inARow, String direction, Color col,
            Marble[][] total) {
        if (inARow >= 5) {
            return true;
        } else if (x >= BLOCK_SIZE * 2 || x < 0 || y >= BLOCK_SIZE * 2 || y < 0) {
            return false;
        }

        Color test = total[x][y].getColor();

        switch (direction) {
        case "NE":
            if (test == col) {
                return (checkWinRec(x + 1, y - 1, inARow + 1, "NE", col, total)
                        || checkWinRec(x + 1, y, 1, "E", col, total)
                        || checkWinRec(x + 1, y + 1, 1, "SE", col, total)
                        || checkWinRec(x, y + 1, 1, "S", col, total));
            } else {
                return checkWinStart(x, y, col, total);
            }
        case "E":
            if (test == col) {
                return (checkWinRec(x + 1, y - 1, 1, "NE", col, total)
                        || checkWinRec(x + 1, y, inARow + 1, "E", col, total)
                        || checkWinRec(x + 1, y + 1, 1, "SE", col, total)
                        || checkWinRec(x, y + 1, 1, "S", col, total));
            } else {
                return checkWinStart(x, y, col, total);
            }
        case "SE":
            if (test == col) {
                return (checkWinRec(x + 1, y - 1, 1, "NE", col, total)
                        || checkWinRec(x + 1, y, 1, "E", col, total)
                        || checkWinRec(x + 1, y + 1, inARow + 1, "SE", col, total)
                        || checkWinRec(x, y + 1, 1, "S", col, total));
            } else {
                return checkWinStart(x, y, col, total);
            }
        case "S":
            if (test == col) {
                return (checkWinRec(x + 1, y - 1, 1, "NE", col, total)
                        || checkWinRec(x + 1, y, 1, "E", col, total)
                        || checkWinRec(x + 1, y + 1, 1, "SE", col, total)
                        || checkWinRec(x, y + 1, inARow + 1, "S", col, total));
            } else {
                return checkWinStart(x, y, col, total);
            }
        default: // this will only ever be called at (0, 0)
            if (test == col) {
                return (checkWinRec(x + 1, y - 1, 1, "NE", col, total)
                        || checkWinRec(x + 1, y, 1, "E", col, total)
                        || checkWinRec(x + 1, y + 1, 1, "SE", col, total)
                        || checkWinRec(x, y + 1, 1, "S", col, total));
            } else {
                return checkWinStart(x, y, col, total);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.MAGENTA);

        // win checking
        Marble[][] total = new Marble[BLOCK_SIZE * 2][BLOCK_SIZE * 2];
        for (int r = 0; r < BLOCK_SIZE; r++) {
            for (int c = 0; c < BLOCK_SIZE; c++) {
                total[r][c] = block1[r][c];
                total[r + BLOCK_SIZE][c] = block2[r][c];
                total[r][c + BLOCK_SIZE] = block3[r][c];
                total[r + BLOCK_SIZE][c + BLOCK_SIZE] = block4[r][c];
            }
        }
        boolean whiteWin = checkWinRec(0, 0, 0, "NONE", Color.WHITE, total);
        boolean blackWin = checkWinRec(0, 0, 0, "NONE", Color.BLACK, total);
        if (whiteWin && blackWin) {
            turn = -2;
            advanceTurn(Color.GRAY);
        } else if (whiteWin) {
            turn = -2;
            advanceTurn(Color.WHITE);
        } else if (blackWin) {
            turn = -2;
            advanceTurn(Color.BLACK);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(SIZE, SIZE);
    }
}
