import java.awt.Color;
import java.awt.Graphics;

/**
 * 
 * @author ericzeng
 *
 */
public class Marble {
    private Color c;

    public Marble(Color color) {
        setColor(color);
    }

    /**
     * Outputs the Color of the marble
     * 
     * @return black or white
     */
    public Color getColor() {
        return c;
    }

    /**
     * Outputs the Color of the marble. Mainly used by child classes and
     * constructors
     * 
     * @param color
     *            the color this marble should be
     */
    private void setColor(Color color) {
        this.c = color;
    }

    /**
     * Draws the marble with x y as the center
     * 
     * @param g
     *            graphics
     * @param x
     *            x center
     * @param y
     *            y center
     * @param diameter
     *            diameter of circular marble
     */
    public void draw(Graphics g, int x, int y, int diameter) {
        g.drawOval(x - diameter, y - diameter, diameter, diameter);
    }
}
