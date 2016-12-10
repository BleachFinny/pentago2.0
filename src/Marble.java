import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * 
 * @author ericzeng
 *
 */
public class Marble extends JComponent {
    private Color c;
    public final int SIZE;

    public Marble(Color color, int s) {
        setColor(color);
        SIZE = s;
        setFocusable(true);
    }

    /**
     * Outputs the Color of the marble
     * 
     * @return the color of the marble
     */
    public Color getColor() {
        return c;
    }

    /**
     * Sets the color of this marble
     * 
     * @param color
     *            the color this marble should be
     */
    public void setColor(Color color) {
        this.c = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this.getClass().isInstance(o)) {
            return (c == ((Marble) o).getColor() && SIZE == ((Marble) o).SIZE);
        }
        return false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (c == null) {
            g.setColor(Color.BLACK);
            g.drawOval(0, 0, SIZE, SIZE);
        } else {
            g.setColor(c);
            g.fillOval(0, 0, SIZE, SIZE);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(SIZE, SIZE);
    }
}
