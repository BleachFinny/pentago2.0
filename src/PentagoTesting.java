import static org.junit.Assert.*;

import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JLabel;

import org.junit.Before;
import org.junit.Test;

public class PentagoTesting {

    private Board board;

    private Player player;

    private Socket connection;

    private JLabel status;

    @Before
    public void SetUp() {
        try {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    try {
                        ServerSocket host = new ServerSocket(21212);
                        Socket other = host.accept();
                        host.close();
                        while (true) {
                            // infinite loop, keep connection open
                        }
                    } catch (IOException e) {
                        System.out.println("CLOSE ALL OTHER ServerSockets, THEN RERUN");
                    }
                }
            };
            new Thread(run).start();

            connection = new Socket("localhost", 21212);
        } catch (IOException e) {
            System.out.println("CLOSE ALL OTHER ServerSockets, THEN RERUN");
        }
        player = new Player("p1", 2, 1, 29, 28);
        status = new JLabel("Testing");
        board = new Board(player, status, new JButton(), connection, Color.WHITE);
    }

    @Test
    public void rotationEmpty() {
        Marble[][] ret = new Marble[Board.BLOCK_SIZE][Board.BLOCK_SIZE];
        for (int r = 0; r < Board.BLOCK_SIZE; r++) {
            for (int c = 0; c < Board.BLOCK_SIZE; c++) {
                ret[r][c] = new Marble(null, board.getBlock(1)[r][c].SIZE);
            }
        }

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

        board.rotate(1, "CW");

        assertEquals(board.getBlock(1)[1][1].getColor(), target);
    }

    @Test
    public void rotationCorner() {
        board.block1[0][0].setColor(Color.WHITE);
        board.block2[0][0].setColor(Color.WHITE);

        board.rotate(1, "CW");
        board.rotate(2, "CCW");
        board.rotate(2, "CCW");
        board.rotate(2, "CCW");

        for (int r = 0; r < Board.BLOCK_SIZE; r++) {
            for (int c = 0; c < Board.BLOCK_SIZE; c++) {
                assertEquals(board.getBlock(1)[r][c], board.getBlock(2)[r][c]);
            }
        }

    }

}
