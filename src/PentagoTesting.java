import static org.junit.Assert.*;

import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JLabel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PentagoTesting {

    private Board board;

    private Player player;

    private Socket connection, other;

    private JLabel status;

    private Marble[][] totalBlock() {
        Marble[][] total = new Marble[Board.BLOCK_SIZE * 2][Board.BLOCK_SIZE * 2];
        for (int r = 0; r < Board.BLOCK_SIZE; r++) {
            for (int c = 0; c < Board.BLOCK_SIZE; c++) {
                total[r][c] = board.block1[r][c];
                total[r + Board.BLOCK_SIZE][c] = board.block2[r][c];
                total[r][c + Board.BLOCK_SIZE] = board.block3[r][c];
                total[r + Board.BLOCK_SIZE][c + Board.BLOCK_SIZE] = board.block4[r][c];
            }
        }
        return total;
    }

    @Before
    public void SetUp() {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket host = new ServerSocket(21212);
                    other = host.accept();
                    host.close();
                } catch (IOException e) {
                    System.out.println("CLOSE ALL OTHER ServerSockets, THEN RERUN");
                }
            }
        };
        new Thread(run).start();
        while (connection == null || !connection.isConnected()) {
            try {
                connection = new Socket("localhost", 21212);
                player = new Player("p1", 2, 1, 29, 28);
                status = new JLabel("Testing");
                board = new Board(player, status, new JButton(), connection, Color.WHITE);
            } catch (IOException e) {
                // OK, then retry connection
            }
        }
    }

    @After
    public void Close() {
        try {
            connection.close();
            other.close();
        } catch (Exception e) {
            // OK
        }
    }

    @Test
    public void rotationEmpty() {
        Marble[][] ret = new Marble[Board.BLOCK_SIZE][Board.BLOCK_SIZE];
        for (int r = 0; r < Board.BLOCK_SIZE; r++) {
            for (int c = 0; c < Board.BLOCK_SIZE; c++) {
                ret[r][c] = new Marble(null, board.getBlock(1)[r][c].SIZE);
            }
        }

        board.advanceTurn(null);
        board.rotate(1, "CW");

        for (int r = 0; r < Board.BLOCK_SIZE; r++) {
            for (int c = 0; c < Board.BLOCK_SIZE; c++) {
                assertEquals(board.getBlock(1)[r][c], ret[r][c]);
            }
        }
    }

    @Test
    public void rotationMiddleOnly() {
        Color target = board.getBlock(1)[1][1].getColor();

        board.advanceTurn(null);
        board.rotate(1, "CW");

        assertEquals(board.getBlock(1)[1][1].getColor(), target);
    }

    @Test
    public void rotationCorner() {
        board.block1[0][0].setColor(Color.WHITE);
        board.block2[0][0].setColor(Color.WHITE);

        board.advanceTurn(null);
        board.rotate(1, "CW");
        board.advanceTurn(null);
        board.advanceTurn(null);
        board.advanceTurn(null);
        board.rotate(2, "CCW");
        board.advanceTurn(null);
        board.advanceTurn(null);
        board.advanceTurn(null);
        board.rotate(2, "CCW");
        board.advanceTurn(null);
        board.advanceTurn(null);
        board.advanceTurn(null);
        board.rotate(2, "CCW");

        for (int r = 0; r < Board.BLOCK_SIZE; r++) {
            for (int c = 0; c < Board.BLOCK_SIZE; c++) {
                assertEquals(board.getBlock(1)[r][c], board.getBlock(2)[r][c]);
            }
        }

    }

    @Test
    public void rotationMultiple() {
        board.block4[0][0].setColor(Color.WHITE);
        board.block3[0][0].setColor(Color.WHITE);
        board.block4[2][0].setColor(Color.BLACK);
        board.block3[2][0].setColor(Color.BLACK);

        board.advanceTurn(null);
        board.rotate(3, "CW");
        board.advanceTurn(null);
        board.advanceTurn(null);
        board.advanceTurn(null);
        board.rotate(4, "CCW");
        board.advanceTurn(null);
        board.advanceTurn(null);
        board.advanceTurn(null);
        board.rotate(4, "CCW");
        board.advanceTurn(null);
        board.advanceTurn(null);
        board.advanceTurn(null);
        board.rotate(4, "CCW");

        for (int r = 0; r < Board.BLOCK_SIZE; r++) {
            for (int c = 0; c < Board.BLOCK_SIZE; c++) {
                assertEquals(board.getBlock(3)[r][c], board.getBlock(4)[r][c]);
            }
        }
    }

    @Test
    public void winVertical() {
        board.block1[0][0].setColor(Color.WHITE);
        board.block1[1][0].setColor(Color.WHITE);
        board.block1[2][0].setColor(Color.WHITE);
        board.block2[0][0].setColor(Color.WHITE);
        board.block2[1][0].setColor(Color.WHITE);

        assertTrue(board.checkWinRec(0, 0, 0, "NONE", Color.WHITE, totalBlock()));
    }

    @Test
    public void winHorizontal() {
        board.block1[0][0].setColor(Color.BLACK);
        board.block1[0][1].setColor(Color.BLACK);
        board.block1[0][2].setColor(Color.BLACK);
        board.block3[0][0].setColor(Color.BLACK);
        board.block3[0][1].setColor(Color.BLACK);

        assertTrue(board.checkWinRec(0, 0, 0, "NONE", Color.BLACK, totalBlock()));
    }

    @Test
    public void winDiagonalTwoBlocks() {
        board.block1[0][0].setColor(Color.WHITE);
        board.block1[1][1].setColor(Color.WHITE);
        board.block1[2][2].setColor(Color.WHITE);
        board.block4[0][0].setColor(Color.WHITE);
        board.block4[1][1].setColor(Color.WHITE);

        assertTrue(board.checkWinRec(0, 0, 0, "NONE", Color.WHITE, totalBlock()));
    }

    @Test
    public void winDiagonalThreeBlocks() {
        board.block1[0][1].setColor(Color.WHITE);
        board.block1[1][2].setColor(Color.WHITE);
        board.block3[2][0].setColor(Color.WHITE);
        board.block4[0][1].setColor(Color.WHITE);
        board.block4[1][2].setColor(Color.WHITE);

        assertTrue(board.checkWinRec(0, 0, 0, "NONE", Color.WHITE, totalBlock()));
    }

    @Test
    public void winDiagonalThreeBlocks2() {
        board.block2[2][1].setColor(Color.WHITE);
        board.block2[1][2].setColor(Color.WHITE);
        board.block4[0][0].setColor(Color.WHITE);
        board.block3[2][1].setColor(Color.WHITE);
        board.block3[1][2].setColor(Color.WHITE);

        assertTrue(board.checkWinRec(0, 0, 0, "NONE", Color.WHITE, totalBlock()));
    }
}
