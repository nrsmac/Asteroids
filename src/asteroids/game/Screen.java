package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.*;
import javax.swing.*;

/**
 * The area of the display in which the game takes place.
 */
@SuppressWarnings("serial")
public class Screen extends JPanel
{
    /** Legend that is displayed across the screen */
    private String legend;

    /** Game controller */
    private Controller controller;
    
    /** How many lives we have today */
    private String livesText;

    /**
     * Creates an empty screen
     */
    public Screen (Controller controller)
    {
        this.controller = controller;
        legend = "";
        livesText = "";
        setPreferredSize(new Dimension(SIZE, SIZE));
        setMinimumSize(new Dimension(SIZE, SIZE));
        setBackground(Color.black);
        setForeground(Color.white);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 120));
        setFocusable(true);
    }

    /**
     * Set the legend
     */
    public void setLegend (String legend)
    {
        this.legend = legend;
    }

    public void setLivesText (String livesText) {
    	this.livesText = livesText; 
    }
    
    public void setLivesAvatar(Shape shipOutline) {
    	
    }
    
    public void setLevelText() {
    	
    }
    
    /**
     * Paint the participants onto this panel
     */
    @Override
    public void paintComponent (Graphics graphics)
    {
        // Use better resolution
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Do the default painting
        super.paintComponent(g);

        // Draw each participant in its proper place
        for (Participant p: controller)
        {
            p.draw(g);
        }

        // Draw the legend across the middle of the panel
        int size = g.getFontMetrics().stringWidth(legend);
        g.drawString(legend, (SIZE - size) / 2, SIZE / 2);
        
        //Draw Other non-participant screen components
        Font smallFont = new Font ("Courier New", Font.PLAIN, 40);
        g.setFont(smallFont);
        g.drawString(livesText, 80, 90);
    }
}
