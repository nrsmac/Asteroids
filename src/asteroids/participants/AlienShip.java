package asteroids.participants;

import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;

import javax.swing.Timer;

import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Controller;
import asteroids.game.Participant;

public class AlienShip extends Ship
		implements
			ShipDestroyer,
			AsteroidDestroyer,
			ActionListener {

	/** The outline of the ship */
	public Shape outline;

	/** The size of the alien ship, 1 for big, 0 for small */
	public int size;

	Timer bulletTimer;

	/** When this timer goes off, shoots bullet */

	public final static int LEFT_DIRECTION = 0;
	public final static int RIGHT_DIRECTION = 1;

	/**
	 * Constructs a ship at the specified coordinates and with a given size of
	 * 1,2 or 3
	 */

	public AlienShip(int x, int y, Controller controller, int size,
			int direction) {
		super(x, y, 0, controller);
		this.size = size;

		Path2D.Double poly = new Path2D.Double();

		if (size == 1) { // big alien outline
			poly.moveTo(-25, 0);
			poly.lineTo(-15, 6);
			poly.lineTo(15, 6);
			poly.lineTo(25, 0);
			poly.lineTo(12, -5);
			poly.lineTo(8, -10);
			poly.lineTo(-8, -10);
			poly.lineTo(-12, -5);
			poly.lineTo(12, -5);
			poly.lineTo(-12, -5);
			poly.lineTo(-25, 0);
			poly.lineTo(25, 0);
			poly.closePath();
			outline = poly;
		} else if (size == 0) { // Small Outline
			poly.moveTo(-11, 0);
			poly.lineTo(-7, 3);
			poly.lineTo(17, 3);
			poly.lineTo(11, 0);
			poly.lineTo(6, -2);
			poly.lineTo(4, -5);
			poly.lineTo(-4, -5);
			poly.lineTo(-6, -2);
			poly.lineTo(6, -2);
			poly.lineTo(-6, -2);
			poly.lineTo(-11, 0);
			poly.lineTo(11, 0);
			poly.closePath();
			outline = poly;
		}
		setPosition(x, y);
		if (direction == RIGHT_DIRECTION) {
			setVelocity(3, 0);
		} else {
			setVelocity(-3, 0);
		}

		// Start bullet timer
		bulletTimer = new Timer(2000, this);
		bulletTimer.start();
	}

	@Override
	public Shape getOutline() {
		return outline;
	}

	/**
	 * Customizes the base move method by imposing friction
	 */
	@Override
	public void move() {
		super.move();
	}

	@Override
	public void collidedWith(Participant p) {
		if (size == 0) controller.genDebris(getX(), getY(), "alienship");
		else controller.genDebris(getX(), getY(), "alienshipsmall");
		Participant.expire(this);
	}

	public int getSize() {
		return size;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bulletTimer && this instanceof AlienShip) {
			getController().shootAlienBullet(this);
		}
	}

}
