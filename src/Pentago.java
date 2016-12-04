
// imports necessary libraries for Java swing
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class Pentago implements Runnable {
    public void run() {
        // NOTE : recall that the 'final' keyword notes immutability
        // even for local variables.

        // top-level frame for everything
        final JFrame frame = new JFrame("Pentago 2.0");
        frame.setLocation(300, 300);
        final JLabel status = new JLabel("Running...");

        // Main playing area
        Player p1 = new Player(0, "p1", 0, 0, 0, 0, 0);
        Player p2 = new Player(1, "p2", 0, 0, 0, 0, 1);
        final Board board = new Board(p1, p2, status);
        frame.add(board, BorderLayout.CENTER);

        // top panel
        final JPanel n_panel = new JPanel();
        frame.add(n_panel, BorderLayout.NORTH);
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
        frame.add(s_panel, BorderLayout.SOUTH);
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

        // Note here that when we add an action listener to the reset
        // button, we define it as an anonymous inner class that is
        // an instance of ActionListener with its actionPerformed()
        // method overridden. When the button is pressed,
        // actionPerformed() will be called.
        final JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final Board b = new Board(p1, p2, status);
                frame.add(b, BorderLayout.CENTER);
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
        frame.add(w_panel, BorderLayout.WEST);
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
        frame.add(e_panel, BorderLayout.EAST);
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

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start game
        // court.reset();
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
