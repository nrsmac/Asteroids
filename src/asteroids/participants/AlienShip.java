package asteroids.participants;

import java.awt.Shape;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.util.Random;

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

	/** Determines when bullets are fired */
	Timer bulletTimer;

	/** Timer tracks when we change directions in zigzag */
	Timer zigZagTimer;

	/** tells which direction it's going */
	int direction;

	/** tells which angle it's going */
	int angle;

	public final static int LEFT_DIRECTION = 0;
	public final static int RIGHT_DIRECTION = 1;

	/**
	 * Constructs a ship at the specified coordinates and with a given size of 0
	 * or 1
	 */

	// TODO add random motion (see spec.)

	public AlienShip(int x, int y, Controller controller, int size,
			int direction) {
		super(x, y, 0, controller);
		this.size = size;
		this.direction = direction;
		this.angle = 0;

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
		generateRandomAngle();
		setShipVelocity();

		// Start bullet timer
		bulletTimer = new Timer(2000, this);
		bulletTimer.start();
		
		//Start zigzag timer
		zigZagTimer = new Timer(500, controller);
		zigZagTimer.start();
	}

	/** Sets based on this.direction and this.angle */
	private void setShipVelocity() {
		if (this.direction == RIGHT_DIRECTION) {
			setVelocity(3, angle);
		} else if (this.direction == LEFT_DIRECTION) {
			setVelocity(-3, angle);
		}
	}

	/**
	 * gives a random direction between -1rad, 0rad and 1rad
	 */
	public void generateRandomAngle() {
		Random rand = new Random();
		// If 0 = -1rad, 1 = 0rad and 2 = +1rad
		int directionValue = rand.nextInt(3);
		int directionInRadians = 0; // 0rad default

		if (directionValue == 0) {
			directionInRadians = -1;
		}
		if (directionValue == 1) {
			directionInRadians = 0;
		}
		if (directionValue == 2) {
			directionInRadians = 1;
		}

		this.angle = directionInRadians; // store to invert for zigzag
	}

	/** Return alien outline instead of inherited ship outline */
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
		if (p instanceof Bullet && ((Bullet) p).isAlienBullet == false
				&& !(p instanceof Debris)) {
			if (size == 0)
				controller.genDebris(getX(), getY(), "alienship");
			else
				controller.genDebris(getX(), getY(), "alienshipsmall");
			Participant.expire(this);
		}
	}

	public int getSize() {
		return size;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bulletTimer && this instanceof AlienShip
				&& this.isExpired() == false) {
			getController().shootAlienBullet(this);
			bulletTimer = new Timer(2000, this);
			bulletTimer.start();
		}

		if (e.getSource() == zigZagTimer) {
			System.out.println("Im a zigzag timer");
			if (angle == -1) { // invert angle for zigzag
				angle = 1;
				setShipVelocity();
			} else if (angle == 1) { // invert angle for zigzag
				angle = -1;
				setShipVelocity();
			}
			zigZagTimer = new Timer(2000, this);
			zigZagTimer.start();
		}
	}

}
