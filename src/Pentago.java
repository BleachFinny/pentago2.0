
// imports necessary libraries for Java swing
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.NumberFormat;
import java.util.TreeMap;

import javax.swing.*;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class Pentago implements Runnable {

    // Networking socket and read/write
    Socket connection;

    // Player or user
    private Player p1;

    // game frame for the main game
    final JFrame loginFrame = new JFrame("Pentago 2.0");

    // game frame for the main game
    final JFrame gameFrame = new JFrame("Pentago 2.0");

    // networking view model
    final JFrame netFrame = new JFrame("Pentago 2.0");

    public void run() {

        // beings game by asking user for client or server mode
        login();
        network();

        // Start game
        loginFrame.setVisible(true);
        loginFrame.setLocation(100, 100);
        loginFrame.pack();
        // loginFrame.setSize(900, 300);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        netFrame.setLocation(100, 100);
        netFrame.setSize(300, 100);
        netFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Runs the login portion of the application
     */
    private void login() {
        loginFrame.setLayout(new BoxLayout(loginFrame.getContentPane(), BoxLayout.Y_AXIS));
        TreeMap<Player, String> users = new TreeMap<Player, String>();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader("stats.txt"));
            String next = in.readLine();
            while (next != null) {
                next = next.trim();
                String[] args = next.split(" ");
                if (args.length != 6) {
                    throw new NumberFormatException("");
                }
                Player addMe = new Player(args[0], Integer.parseInt(args[2]),
                        Integer.parseInt(args[3]), Integer.parseInt(args[4]),
                        Integer.parseInt(args[5]));
                users.put(addMe, args[1]);
                next = in.readLine();
            }
        } catch (NumberFormatException e) {
            final JLabel error = new JLabel(
                    "File formatting is wrong! Please correct stats.txt or contact the developer!");
            loginFrame.add(error);
            return;
        } catch (FileNotFoundException e) {
            final JLabel error = new JLabel(
                    "stats.txt is missing! Please replace it or contact the developer!");
            loginFrame.add(error);
            return;
        } catch (IOException e) {
            final JLabel error = new JLabel(
                    "Error in loading users! Please contact the developer!");
            loginFrame.add(error);
            return;
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                // continue
            }
        }

        final JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        final JLabel subTitle = new JLabel("Login, create an account, or read the rules");
        final JTextField username = new JTextField();
        username.setToolTipText("Username");
        final JPasswordField password = new JPasswordField();
        password.setToolTipText("Password");
        loginPanel.add(subTitle);
        loginPanel.add(username);
        loginPanel.add(password);

        final JPanel userButtons = new JPanel();
        final JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = username.getText();
                char[] pass = password.getPassword();
                String passString = "";
                for (char c : pass) {
                    passString = passString + c;
                }
                for (Player p : users.keySet()) {
                    if (p.getName().equals(name) && users.get(p).equals(passString)) {
                        p1 = p;
                        userButtons.setVisible(false);
                        loginFrame.setVisible(false);
                        netFrame.setTitle("Pentago 2.0 - " + p1.getName());
                        netFrame.setVisible(true);
                        return;
                    }
                }
                username.setText("");
                password.setText("");
                subTitle.setText("Username/password not found, try again");
            }
        });
        final JButton createButton = new JButton("New User");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = username.getText();
                char[] pass = password.getPassword();
                String passString = "";
                for (char c : pass) {
                    passString = passString + c;
                }
                // valid string check
                if (name.contains(" ") || name.length() == 0 || passString.contains(" ")
                        || passString.length() == 0) {
                    username.setText("");
                    password.setText("");
                    subTitle.setText("No spaces in username/password allowed, try again");
                    return;
                }
                // already exists check
                for (Player p : users.keySet()) {
                    if (p.getName().equals(name)) {
                        username.setText("");
                        password.setText("");
                        subTitle.setText("Username/password already exists, try again");
                        return;
                    }
                }
                // add user if passes check
                BufferedWriter write = null;
                try {
                    write = new BufferedWriter(new FileWriter("stats.txt", true));
                    write.write(name + " " + passString + " 0 0 0 0\n");
                    users.put(new Player(name, 0, 0, 0, 0), passString);
                } catch (IOException ex) {
                    final JLabel error = new JLabel(
                            "Error in loading users! Please contact the developer!");
                    loginFrame.add(error);
                } finally {
                    try {
                        write.close();
                    } catch (Exception ex) {
                        // continue
                    }
                }
                username.setText("");
                password.setText("");
                subTitle.setText("Username/password registered, login to continue");
            }
        });
        userButtons.add(loginButton);
        userButtons.add(createButton);

        loginFrame.add(loginPanel);
        loginFrame.add(userButtons);
    }

    /**
     * Runs the networking for this game
     */
    private void network() {
        netFrame.setLayout(new BoxLayout(netFrame.getContentPane(), BoxLayout.Y_AXIS));

        // status panel
        final JPanel netPanel = new JPanel();
        netPanel.setLayout(new BoxLayout(netPanel, BoxLayout.X_AXIS));
        final JLabel status = new JLabel("Choose to be a client or host");
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
            gameFrame.setTitle("Pentago 2.0 - " + p1.getName() + " - White");
        } else if (col == Color.BLACK) {
            gameFrame.setTitle("Pentago 2.0 - " + p1.getName() + " - Black");
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
