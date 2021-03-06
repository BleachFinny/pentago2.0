import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
    public static final int MARBLE_SIZE = SIZE / (BLOCK_SIZE * 4 + 1);

    // four 2-D arrays representing the four rotating blocks
    // starts with block1 in top left; top to bottom, then left to right
    // block 4 will be to bottom right
    public final Marble[][] block1 = new Marble[BLOCK_SIZE][BLOCK_SIZE];
    public final Marble[][] block2 = new Marble[BLOCK_SIZE][BLOCK_SIZE];
    public final Marble[][] block3 = new Marble[BLOCK_SIZE][BLOCK_SIZE];
    public final Marble[][] block4 = new Marble[BLOCK_SIZE][BLOCK_SIZE];

    // players that login;
    private final Player player;
    private final Color color;

    // turn status (next move to be played)
    // 0: p1 place | 1: p1 rotate | 2: p2 place | 3: p2 rotate | -1: win
    // turn will increment constantly, multiples of 4 of the options above
    // correspond to that multiple (e.g. 5 -> 1 -> p1 rotate)
    private int turn;

    // status of game label
    private final JLabel status;

    // Reset button that is shown when game ends
    private final JButton reset;

    // networking socket
    private Socket connection;
    private BufferedReader read;
    private PrintWriter write;

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
    public Board(Player p, JLabel s, JButton r, Socket c, Color col) {
        player = p;
        status = s;
        reset = r;
        connection = c;
        color = col;
        try {
            read = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            write = new PrintWriter(connection.getOutputStream(), true);
        } catch (IOException e) {
            status.setText("FAILURE TO ESTABLISH CONNECTION, PLEASE RESTART APPLICATION");
        }
        turn = -1; // when advanceTurn(null) is called, the game starts

        setFocusable(false); // default: focus on marbles instead

        // networking read thread setup
        Runnable network = new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String command = read.readLine();
                        if (command == null) {
                            // other user disconnected
                            if (turn != -1) {
                                status.setText("Connection issue, game aborted");
                                reset.setVisible(true);
                                // end game
                                turn = -1;
                            }
                            break;
                        }
                        if (process(command)) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    // if exception, stop thread
                }
            }
        };
        Thread netThread = new Thread(network);
        netThread.start();

        initBlocks();
        advanceTurn(null);
    }

    /**
     * Initializes the four blocks with rigid spacers in between which are the
     * same size (BLOCK_SIZE). Marbles will be initialized to null color,
     * representing an empty space where no player has placed a real marble yet
     */
    private void initBlocks() {
        JPanel grid = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawLine(SIZE / 2, MARBLE_SIZE / 2, SIZE / 2, SIZE - MARBLE_SIZE / 2);
                g.drawLine(MARBLE_SIZE / 2, SIZE / 2, SIZE - MARBLE_SIZE / 2, SIZE / 2);
                g.drawRect(MARBLE_SIZE / 2, MARBLE_SIZE / 2, SIZE - MARBLE_SIZE,
                        SIZE - MARBLE_SIZE);
            }
        };
        grid.setLayout(new GridLayout(BLOCK_SIZE * 4 + 1, BLOCK_SIZE * 4 + 1));
        grid.setFocusable(false);
        for (int c = 0; c < BLOCK_SIZE * 4 + 1; c++) {
            for (int r = 0; r < BLOCK_SIZE * 4 + 1; r++) {
                if (c % 2 == 0 || r % 2 == 0) {
                    insertRigidSpace(grid);
                } else {
                    Marble marb = new Marble(null, MARBLE_SIZE - 1);
                    if (r < BLOCK_SIZE * 2 && c < BLOCK_SIZE * 2) {
                        block1[r / 2][c / 2] = marb;
                    } else if (r > BLOCK_SIZE * 2 && c < BLOCK_SIZE * 2) {
                        block2[r / 2 - BLOCK_SIZE][c / 2] = marb;
                    } else if (r < BLOCK_SIZE * 2 && c > BLOCK_SIZE * 2) {
                        block3[r / 2][c / 2 - BLOCK_SIZE] = marb;
                    } else if (r > BLOCK_SIZE * 2 && c > BLOCK_SIZE * 2) {
                        block4[r / 2 - BLOCK_SIZE][c / 2 - BLOCK_SIZE] = marb;
                    }
                    marb.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            Marble m = (Marble) e.getComponent();
                            if (m.getColor() == null) {
                                String location;
                                if (turn % 4 == 0 && color == Color.WHITE) {
                                    m.setColor(color);
                                    player.incMarblePlacements();
                                    location = findBlockXY(m);
                                    if (location.equals("!")) {
                                        status.setText("COMM ERROR");
                                    } else {
                                        write.println("WHITE " + location);
                                    }
                                    advanceTurn(null);
                                    repaint();
                                } else if (turn % 4 == 2 && color == Color.BLACK) {
                                    m.setColor(color);
                                    player.incMarblePlacements();
                                    location = findBlockXY(m);
                                    if (location.equals("!")) {
                                        status.setText("COMM ERROR");
                                    } else {
                                        write.println("BLACK " + location);
                                    }
                                    advanceTurn(null);
                                    repaint();
                                }

                            }
                        }
                    });
                    grid.add(marb); // add marble to blocks' grid
                }
            }
        }
        add(grid); // add blocks' grid to board
    }

    /**
     * Returns a string of the block, row, and column a marble is in. Format is
     * "1 4 5", which corresponds to block1 at row 4 col 5
     * 
     * @param m
     *            the marble in question
     * @return a string as described
     */
    private String findBlockXY(Marble m) {
        for (int r = 0; r < BLOCK_SIZE; r++) {
            for (int c = 0; c < BLOCK_SIZE; c++) {
                if (m == block1[r][c]) {
                    return "1 " + r + " " + c;
                } else if (m == block2[r][c]) {
                    return "2 " + r + " " + c;
                } else if (m == block3[r][c]) {
                    return "3 " + r + " " + c;
                } else if (m == block4[r][c]) {
                    return "4 " + r + " " + c;
                }
            }
        }
        return "!"; // ! is an error string
    }

    /**
     * Combines the four block into one large block. Used for win testing
     * 
     * @return the combined Marble array
     */
    private Marble[][] totalBlock() {
        Marble[][] total = new Marble[BLOCK_SIZE * 2][BLOCK_SIZE * 2];
        for (int r = 0; r < BLOCK_SIZE; r++) {
            for (int c = 0; c < BLOCK_SIZE; c++) {
                total[r][c] = block1[r][c];
                total[r + BLOCK_SIZE][c] = block2[r][c];
                total[r][c + BLOCK_SIZE] = block3[r][c];
                total[r + BLOCK_SIZE][c + BLOCK_SIZE] = block4[r][c];
            }
        }
        return total;
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
        if ((turn % 4 == 1 && color == Color.WHITE) || (turn % 4 == 3 && color == Color.BLACK)) {
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
                rotateCW(block);
                player.incBlockTurns();
                write.println(direction + " " + b);
                break;
            case "CCW":
                rotateCCW(block);
                player.incBlockTurns();
                write.println(direction + " " + b);
                break;
            default:
                System.out.println("ERROR IN ROTATION: CHECK ROTATION TAGS");
                return false;
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

    /**
     * Advances the state of the game 0-3 represented the four move cycle of two
     * players (place, rotate, place rotate). If turn is -1, then the color
     * passed in is the winner. Color.GREY signals a tie.
     * 
     * @param c
     *            color of the Winner
     */
    public void advanceTurn(Color c) {
        turn++;
        // special case: all positions on board are filled
        if (turn == 72) {
            status.setText("No more possible moves. Tie!");
            player.incGamesLost();
            turn = -1;
            write.print("WIN");
            return;
        }

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
            if (color == c) {
                player.incGamesWon();
            } else {
                // ties are counted as losses
                player.incGamesLost();
            }

            if (c == Color.WHITE) {
                status.setText("White Wins!");
            } else if (c == Color.BLACK) {
                status.setText("Black Wins!");
            } else if (c == Color.GRAY) {
                status.setText("Tie!");
            } else {
                status.setText("ERROR: INVALID WIN PLAYER SUGGESTED");
            }

            write.println("WIN");
            break;
        }
    }

    /**
     * Process network inputs from the other player
     * 
     * @param command
     *            the update from the other user
     * @return if the opponents signals a win for either player
     */
    private boolean process(String command) {
        String[] cmds = command.split(" ");

        switch (cmds[0]) {
        case "WIN":
            turn = -1; // ensure that the game is over
            reset.setVisible(true);
            return true;
        case "CW":
        case "CCW":
            Marble[][] block = null;
            switch (cmds[1]) {
            case "1":
                block = block1;
                break;
            case "2":
                block = block2;
                break;
            case "3":
                block = block3;
                break;
            case "4":
                block = block4;
                break;
            }
            if (cmds[0].equals("CW")) {
                rotateCW(block);
            } else {
                rotateCCW(block);
            }
            break;
        case "WHITE":
        case "BLACK":
            int r = Integer.parseInt(cmds[2]);
            int c = Integer.parseInt(cmds[3]);
            Color col = Color.BLACK;
            if (cmds[0].equals("WHITE")) {
                col = Color.WHITE;
            }
            switch (cmds[1]) {
            case "1":
                block1[r][c].setColor(col);
                break;
            case "2":
                block2[r][c].setColor(col);
                break;
            case "3":
                block3[r][c].setColor(col);
                break;
            case "4":
                block4[r][c].setColor(col);
                break;
            }
            break;
        }
        advanceTurn(null);
        repaint();
        return false;
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
    public boolean checkWinStart(int x, int y, Color col, Marble[][] total) {
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
    public boolean checkWinRec(int x, int y, int inARow, String direction, Color col,
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

    /**
     * Gets the specified block. Testing purposes only
     * 
     * @param i
     *            the block number
     * @return the block
     */
    public Marble[][] getBlock(int i) {
        Marble[][] ret = new Marble[BLOCK_SIZE][BLOCK_SIZE];
        Marble[][] block = null;
        switch (i) {
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
        }

        for (int r = 0; r < BLOCK_SIZE; r++) {
            for (int c = 0; c < BLOCK_SIZE; c++) {
                ret[r][c] = new Marble(block[r][c].getColor(), block[r][c].getWidth());
            }
        }
        return ret;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // win checking
        Marble[][] total = totalBlock();
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
