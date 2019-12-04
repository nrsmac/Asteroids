package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

import asteroids.participants.Ship;

/**
 * The area of the display in which the game takes place.
 */
@SuppressWarnings("serial")
public class Screen extends JPanel {
	/** Legend that is displayed across the screen */
	private String legend;

	/** Game controller */
	private Controller controller;

	/** Lives */
	private int lives;

	/** Lives ship outlines */
	private ArrayList<Ship> livesAvatarArray;
	
	private int level;
	
	private int points;

	// private ArrayList<Ship> livesAvatarArray;

	/**
	 * Creates an empty screen
	 */
	public Screen(Controller controller) {
		this.controller = controller;
		livesAvatarArray = new ArrayList<Ship>();
		//// this.setLivesAvatar(3);
		// livesAvatarArray.add(new Ship(500, 20, -Math.PI/2, controller));

		legend = "";
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
	public void setLegend(String legend) {
		this.legend = legend;
	}

	public void setLives(int lives) {
		this.lives = lives;

		livesAvatarArray.clear();
		for (int i = 0; i < lives; i++) {
			Ship newShip = new Ship(0, 0, -Math.PI / 2, controller);

			if (i == 0) {
				newShip.setPosition(40, 80);
				newShip.move();
				livesAvatarArray.add(newShip);
			} else {
				double previousShipX = livesAvatarArray.get(i - 1).getX();
				double previousShipY = livesAvatarArray.get(i - 1).getY();
				newShip.setPosition(previousShipX + 40, previousShipY);
				newShip.move();
				livesAvatarArray.add(newShip);
			}
		}
	}
	
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Paint the participants onto this panel
	 */
	@Override
	public void paintComponent(Graphics graphics) {
		// Use better resolution
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		// Do the default painting
		super.paintComponent(g);

		// Draw each participant in its proper place
		for (Participant p : controller) {
			p.draw(g);
		}

		// Draw the legend across the middle of the panel
		int size = g.getFontMetrics().stringWidth(legend);
		g.drawString(legend, (SIZE - size) / 2, SIZE / 2);

		// Draw Other non-participant screen components
		Font smallFont = new Font("Monospace", Font.PLAIN, 40);
		g.setFont(smallFont);
		g.drawString(points + "", 80, 40);
		if (level != 0) {
			g.drawString(level + "", SIZE - 30, 50);
		}
		
		
		

		// Draw ships
		for (Ship ship : livesAvatarArray) {
			ship.draw(g);
		}
	}

	public void setPoints(int points) {
		this.points = points;
		
	}

}
