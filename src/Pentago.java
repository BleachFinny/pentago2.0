
// imports necessary libraries for Java swing
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

    // Map of Users in stat.txt
    TreeMap<Player, String> users;

    // statistics file
    private String statistics = "stats.txt";

    // game frame for the main game
    final JFrame loginFrame = new JFrame("Pentago 2.0");

    // game frame for the main game
    final JFrame gameFrame = new JFrame("Pentago 2.0");

    // networking view model
    final JFrame netFrame = new JFrame("Pentago 2.0");

    public void run() {

        // beings game with login screen, networking can run in the background
        // for now
        final JFrame prompt = new JFrame("Pentago 2.0");
        prompt.setLayout(new BoxLayout(prompt.getContentPane(), BoxLayout.X_AXIS));
        prompt.add(Box.createHorizontalGlue());
        final JPanel promptPanel = new JPanel();
        promptPanel.setLayout(new BoxLayout(promptPanel, BoxLayout.Y_AXIS));
        prompt.add(promptPanel);
        prompt.add(Box.createHorizontalGlue());

        final JLabel instruct = new JLabel("Select player statistics file directory:");
        promptPanel.add(instruct);
        final JTextField dir = new JTextField(statistics);
        promptPanel.add(dir);
        final JButton enter = new JButton("OK");
        promptPanel.add(enter);
        promptPanel.add(Box.createVerticalGlue());
        ActionListener changeFileAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File trial = new File(dir.getText().trim());
                if (trial.isFile()) {
                    statistics = dir.getText().trim();

                    // begin login, and networking in background
                    prompt.dispose();
                    login();
                    network();
                } else {
                    instruct.setText("Invalid file path/name, try again");
                }
            }
        };
        enter.addActionListener(changeFileAction);
        dir.addActionListener(changeFileAction);

        prompt.setLocation(200, 200);
        prompt.setSize(300, 100);
        prompt.setResizable(false);
        prompt.setVisible(true);
    }

    /**
     * Runs the login portion of the application
     */
    private void login() {
        loginFrame.setLayout(new BoxLayout(loginFrame.getContentPane(), BoxLayout.Y_AXIS));

        users = new TreeMap<Player, String>();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(statistics));
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
            final JLabel error = new JLabel("File formatting is wrong! Please correct " + statistics
                    + " or contact the developer!");
            loginFrame.add(error);
            return;
        } catch (FileNotFoundException e) {
            final JLabel error = new JLabel(
                    "statsitics file is missing! Please replace it or contact the developer!");
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

        // login instruction line
        final JPanel title = new JPanel();
        title.setLayout(new BoxLayout(title, BoxLayout.X_AXIS));
        title.add(Box.createHorizontalGlue());
        final JLabel subTitle = new JLabel("Login, create an account, or read the rules");
        title.add(subTitle);
        title.add(Box.createHorizontalGlue());
        loginFrame.add(title);

        // text fields
        final JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        final JTextField username = new JTextField();
        username.setToolTipText("Username");
        final JPasswordField password = new JPasswordField();
        password.setToolTipText("Password");
        loginPanel.add(username);
        loginPanel.add(password);

        // login or create a new user panel
        final JPanel userButtons = new JPanel();

        // play, check statistics, or log out panel
        final JPanel controlPanel = new JPanel();

        ActionListener loginAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = username.getText();
                char[] pass = password.getPassword();
                username.setText("");
                password.setText("");
                String passString = "";
                for (char c : pass) {
                    passString = passString + c;
                    c = ' '; // security erase
                }
                for (Player p : users.keySet()) {
                    if (p.getName().equals(name) && users.get(p).equals(passString)) {
                        p1 = p;
                        loginPanel.setVisible(false);
                        userButtons.setVisible(false);
                        controlPanel.setVisible(true);
                        subTitle.setText("Welcome!");
                        return;
                    }
                }
                subTitle.setText("Username/password not found, try again");
            }
        };
        username.addActionListener(loginAction);
        password.addActionListener(loginAction);

        // continued from controlPanel
        final JButton statsButton = new JButton("My Stats");
        statsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayStats();
            }
        });

        final JButton playButton = new JButton("Play");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginFrame.setVisible(false);
                netFrame.setTitle("Pentago 2.0 - " + p1.getName());
                netFrame.setVisible(true);
            }
        });

        final JButton logOutButton = new JButton("Log out");
        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controlPanel.setVisible(false);
                p1 = null;
                loginPanel.setVisible(true);
                userButtons.setVisible(true);
                subTitle.setText("Logged out!");
            }
        });
        controlPanel.add(statsButton);
        controlPanel.add(playButton);
        controlPanel.add(logOutButton);

        // continued from userButtons
        final JButton loginButton = new JButton("Login");
        loginButton.addActionListener(loginAction);
        final JButton createButton = new JButton("New User");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = username.getText();
                char[] pass = password.getPassword();
                username.setText("");
                password.setText("");
                String passString = "";
                for (char c : pass) {
                    passString = passString + c;
                    c = ' '; // security erase
                }
                // valid string check
                if (name.contains(" ") || name.length() == 0 || passString.contains(" ")
                        || passString.length() == 0) {
                    subTitle.setText("No spaces in username/password allowed, try again");
                    return;
                }
                // already exists check
                for (Player p : users.keySet()) {
                    if (p.getName().equals(name)) {
                        subTitle.setText("Username already exists, try again");
                        return;
                    }
                }
                // add user if passes checks
                BufferedWriter write = null;
                try {
                    write = new BufferedWriter(new FileWriter(statistics, true));
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
                subTitle.setText("Username/password registered, login to continue");
            }
        });
        final JButton instructions = new JButton("Instructions");
        instructions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayInstructions();
            }
        });
        userButtons.add(loginButton);
        userButtons.add(createButton);
        userButtons.add(instructions);

        loginFrame.add(loginPanel);
        loginFrame.add(userButtons);
        loginFrame.add(controlPanel);
        controlPanel.setVisible(false);

        loginFrame.setLocation(100, 100);
        loginFrame.setSize(500, 130);
        loginFrame.setResizable(false);
        loginFrame.setVisible(true);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Displays the statistics of the current player reference by p1
     */
    private void displayStats() {
        final JFrame displayStats = new JFrame();
        displayStats.setLayout(new BoxLayout(displayStats.getContentPane(), BoxLayout.X_AXIS));
        final JPanel categories = new JPanel();
        categories.setLayout(new BoxLayout(categories, BoxLayout.Y_AXIS));
        final JPanel values = new JPanel();
        values.setLayout(new BoxLayout(values, BoxLayout.Y_AXIS));

        final JLabel user = new JLabel("User:");
        final JLabel name = new JLabel(p1.getName() + "");
        categories.add(user);
        values.add(name);

        final JLabel wins = new JLabel("Wins:");
        final JLabel winval = new JLabel(p1.getGamesWon() + "");
        categories.add(wins);
        values.add(winval);

        final JLabel losses = new JLabel("Losses:");
        final JLabel loseval = new JLabel(p1.getGamesLost() + "");
        categories.add(losses);
        values.add(loseval);

        final JLabel marbs = new JLabel("Marbles Placed:");
        final JLabel marbval = new JLabel(p1.getMarblePlacements() + "");
        categories.add(marbs);
        values.add(marbval);

        final JLabel blocks = new JLabel("Blocks Tunred:");
        final JLabel bval = new JLabel(p1.getBlockTurns() + "");
        categories.add(blocks);
        values.add(bval);

        displayStats.add(categories);
        displayStats.add(values);
        displayStats.setLocation(200, 200);
        displayStats.pack();
        displayStats.setVisible(true);
        displayStats.setResizable(false);
        displayStats.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * Displays the instructions for this game
     */
    private void displayInstructions() {
        final JFrame instructions = new JFrame("Instructions");
        final JTextArea content = new JTextArea();
        content.setEditable(false);
        instructions.add(content);

        content.setText(
                "Pentago is played with two players, white and black as denoted by their marbles."
                        + "\n"
                        + "White starts by placing one marble on any open space (click), then rotating any one\n"
                        + "of the four blocks that comprise the whole board. Then Black will do the same.\n"
                        + "The goal is to get 5 in a row spanning across any number of blocks. This can be\n"
                        + "done horizontaly, diagonally, or vertically.\n\n"
                        + "For this game application, your player statistics will be stored in the file you\n"
                        + "specified at the start. You must connect to an opponent via IP address (can be local,\n"
                        + "but be careful not select the same statistics file!). Have fun!");

        instructions.setLocation(200, 200);
        instructions.pack();
        instructions.setVisible(true);
        instructions.setResizable(false);
        instructions.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * Runs the networking for this game
     */
    private void network() {
        netFrame.setLayout(new BoxLayout(netFrame.getContentPane(), BoxLayout.Y_AXIS));

        // status panel
        final JPanel netPanel = new JPanel();
        netPanel.setLayout(new BoxLayout(netPanel, BoxLayout.X_AXIS));
        final JLabel status = new JLabel("Host a game or connect to an IP address as client");
        netPanel.add(Box.createHorizontalGlue());
        netPanel.add(status);
        netPanel.add(Box.createHorizontalGlue());
        netFrame.add(netPanel);

        // IP address field
        final JTextField ipField = new JTextField("localhost");
        ipField.setToolTipText("Enter target IP address");
        netFrame.add(ipField);

        // button panel
        final JPanel butPanel = new JPanel();
        butPanel.setLayout(new BoxLayout(butPanel, BoxLayout.X_AXIS));
        butPanel.add(Box.createHorizontalGlue());
        netFrame.add(butPanel);

        final JButton client = new JButton("Client");
        client.setText("Client");
        client.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    connection = new Socket(ipField.getText().trim(), 21212);
                    gameFrame.setVisible(true);
                    netFrame.setVisible(false);
                    status.setText("Host a game or connect to an IP address as client");
                    // opens the board once connection has been established
                    game(Color.BLACK);
                } catch (IOException ex) { // change this
                    status.setText("Failed to connect as a client, try again");
                }
            }
        });

        final JButton host = new JButton("Host");
        host.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.setEnabled(false);
                host.setEnabled(false);
                ipField.setEnabled(false);
                // creates a new networking thread to check for a client
                Runnable network = new Runnable() {
                    @Override
                    public void run() {
                        ServerSocket server = null;
                        try {
                            server = new ServerSocket(21212);
                            status.setText("Waiting for client...");
                            connection = server.accept();
                            status.setText("Host a game or connect to an IP address as client");
                            gameFrame.setVisible(true);
                            netFrame.setVisible(false);
                            // opens the board once connection has been
                            // established
                            game(Color.WHITE);
                        } catch (IOException ex) {
                            status.setText("Failed to instantiate server, try again");
                        } finally {
                            try {
                                client.setEnabled(true);
                                host.setEnabled(true);
                                ipField.setEnabled(true);
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

        netFrame.setLocation(100, 100);
        netFrame.setSize(400, 100);
        netFrame.setResizable(false);
        netFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        // Return button
        // TODO fix stackoverflow bug
        final JButton reset = new JButton("Return");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameFrame.setVisible(false);
                loginFrame.setVisible(true);
                BufferedReader read = null;
                BufferedWriter write = null;
                try {
                    File statsFile = new File(statistics);
                    File temp = new File("10jas30o2.txt");
                    read = new BufferedReader(new FileReader(statsFile));
                    write = new BufferedWriter(new FileWriter(temp));
                    String next = read.readLine();
                    while (next != null) {
                        next = next.trim();
                        if (next.substring(0, next.indexOf(" ")).equals(p1.getName())) {
                            String output = p1.toString();
                            write.write(p1.getName() + " " + users.get(p1)
                                    + output.substring(output.indexOf(" "), output.length())
                                    + "\n");
                            users.replace(p1, users.get(p1));
                        } else {
                            write.write(next + "\n");
                        }
                        next = read.readLine();
                    }
                    statsFile.delete();
                    temp.renameTo(new File(statistics));
                } catch (IOException ex) {
                    // OK
                } finally {
                    try {
                        read.close();
                        write.close();
                        connection.close();
                    } catch (Exception ex) {
                        // OK
                    }
                }
            }
        });
        s_panel.add(reset);
        s_panel.add(Box.createHorizontalGlue());

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
        gameFrame.setResizable(false);
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
