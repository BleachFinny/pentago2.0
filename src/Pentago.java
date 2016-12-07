
// imports necessary libraries for Java swing
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.*;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class Pentago implements Runnable {

    // Networking socket and read/write
    Socket connection;

    // game frame for the main game
    final JFrame gameFrame = new JFrame("Pentago 2.0");

    // networking view model
    final JFrame netFrame = new JFrame("Pentago 2.0");

    public void run() {

        // beings game by asking user for client or server mode
        network();

        netFrame.setLocation(100, 100);
        netFrame.setSize(300, 100);
        netFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Start game
        netFrame.setVisible(true);
    }

    /**
     * Runs the networking for this game
     */
    private void network() {
        netFrame.setLayout(new BoxLayout(netFrame.getContentPane(), BoxLayout.Y_AXIS));

        // status panel
        final JPanel netPanel = new JPanel();
        netPanel.setLayout(new BoxLayout(netPanel, BoxLayout.X_AXIS));
        final JLabel status = new JLabel();
        status.setText("Choose to be a client or host");
        netPanel.add(Box.createHorizontalGlue());
        netPanel.add(status);
        netPanel.add(Box.createHorizontalGlue());
        netFrame.add(netPanel);

        // button panel
        final JPanel butPanel = new JPanel();
        butPanel.setLayout(new BoxLayout(butPanel, BoxLayout.X_AXIS));
        butPanel.add(Box.createHorizontalGlue());
        netFrame.add(butPanel);

        final JButton client = new JButton();
        client.setText("Client");
        client.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    connection = new Socket("localhost", 21212);
                    gameFrame.setVisible(true);
                    netFrame.setVisible(false);
                    // opens the board once connection has been established
                    game(Color.BLACK);
                } catch (IOException ex) { // change this
                    status.setText("Failed to connect as a client, try again");
                }
            }
        });

        final JButton host = new JButton();
        host.setText("Host");
        host.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.setEnabled(false);
                host.setEnabled(false);
                // creates a new networking thread to check for a client
                Runnable network = new Runnable() {
                    @Override
                    public void run() {
                        ServerSocket server = null;
                        try {
                            server = new ServerSocket(21212);
                            status.setText("Waiting for client...");
                            connection = server.accept();
                            status.setText("Connected!");
                            gameFrame.setVisible(true);
                            netFrame.setVisible(false);
                            // opens the board once connection has been
                            // established
                            game(Color.WHITE);
                        } catch (IOException ex) {
                            status.setText("Failed to instantiate server, try again");
                            client.setEnabled(true);
                            host.setEnabled(true);
                        } finally {
                            try {
                                server.close();
                            } catch (Exception ex) {
                                // OK, continue if server is null
                            }
                        }
                    }
                };
                new Thread(network).start();
            }
        });
        butPanel.add(host);
        butPanel.add(client);
        butPanel.add(Box.createHorizontalGlue());
    }

    /**
     * Runs the main game
     * 
     * @param isHost
     *            is this player the host?
     */
    private void game(Color col) {
        // status/turn indicator
        final JLabel status = new JLabel("Welcome");

        // Main playing area
        Player p1 = new Player(0, "p1", 0, 0, 0, 0, 0); // TODO: implement
                                                        // player stats
        final Board board = new Board(p1, status, connection, col);
        gameFrame.add(board, BorderLayout.CENTER);

        // top panel
        final JPanel n_panel = new JPanel();
        gameFrame.add(n_panel, BorderLayout.NORTH);
        n_panel.setLayout(new BoxLayout(n_panel, BoxLayout.X_AXIS));

        final JButton cw1 = new JButton();
        cw1.setText("CW");
        cw1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.rotate(1, "CW");
            }
        });
        n_panel.add(cw1);

        n_panel.add(Box.createHorizontalGlue());
        n_panel.add(status);
        n_panel.add(Box.createHorizontalGlue());

        final JButton ccw2 = new JButton();
        ccw2.setText("CCW");
        ccw2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.rotate(2, "CCW");
            }
        });
        n_panel.add(ccw2);

        // bottom panel
        final JPanel s_panel = new JPanel();
        gameFrame.add(s_panel, BorderLayout.SOUTH);
        s_panel.setLayout(new BoxLayout(s_panel, BoxLayout.X_AXIS));

        final JButton ccw3 = new JButton();
        ccw3.setText("CCW");
        ccw3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.rotate(3, "CCW");
            }
        });
        s_panel.add(ccw3);
        s_panel.add(Box.createHorizontalGlue());

        // // Return button
        // final JButton reset = new JButton("Return");
        // reset.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // // TODO: fix me
        // final Board b = new Board(p1, p2, status, connection);
        // gameFrame.add(b, BorderLayout.CENTER);
        // }
        // });
        // s_panel.add(reset);
        // s_panel.add(Box.createHorizontalGlue());

        final JButton cw4 = new JButton();
        cw4.setText("CW");
        cw4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.rotate(4, "CW");
            }
        });
        s_panel.add(cw4);

        // left panel
        final JPanel w_panel = new JPanel();
        gameFrame.add(w_panel, BorderLayout.WEST);
        w_panel.setLayout(new BoxLayout(w_panel, BoxLayout.Y_AXIS));

        final JButton ccw1 = new JButton();
        ccw1.setText("CCW");
        ccw1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.rotate(1, "CCW");
            }
        });
        w_panel.add(ccw1);

        w_panel.add(Box.createVerticalGlue());

        final JButton cw3 = new JButton();
        cw3.setText("CW");
        cw3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.rotate(3, "CW");
            }
        });
        w_panel.add(cw3);

        // right panel
        final JPanel e_panel = new JPanel();
        gameFrame.add(e_panel, BorderLayout.EAST);
        e_panel.setLayout(new BoxLayout(e_panel, BoxLayout.Y_AXIS));

        final JButton cw2 = new JButton();
        cw2.setText("CW");
        cw2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.rotate(2, "CW");
            }
        });
        e_panel.add(cw2);

        e_panel.add(Box.createVerticalGlue());

        final JButton ccw4 = new JButton();
        ccw4.setText("CCW");
        ccw4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.rotate(4, "CCW");
            }
        });
        e_panel.add(ccw4);

        // setup frames' properties
        if (col == Color.WHITE) {
            gameFrame.setTitle("Pentago 2.0 - (user) - White");
        } else if (col == Color.BLACK) {
            gameFrame.setTitle("Pentago 2.0 - (user) - Black");
        }

        gameFrame.setLocation(100, 100);
        gameFrame.pack();
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /*
     * Main method run to start and run the game Initializes the GUI elements
     * specified in Game and runs it IMPORTANT: Do NOT delete! You MUST include
     * this in the final submission of your game.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Pentago());
    }
}
