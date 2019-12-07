package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.game.Constants;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

public class Bullet extends Participant implements AsteroidDestroyer {
	/** Game controller */
	private Controller controller;

	/* Contains the shape */
	private Shape outline;

	public boolean isAlienBullet = false;

	public Bullet(Ship ship, Controller controller, Double direction) {
		Ellipse2D.Double poly = new Ellipse2D.Double(0, 0, 1, 1);
		outline = poly;

		this.controller = controller;
		setPosition(ship.getXNose(), ship.getYNose());
		if (ship instanceof AlienShip) {
			setPosition(ship.getX(), ship.getY());
		}

		setVelocity(Constants.BULLET_SPEED, direction);

		// // Schedule an acceleration in two seconds
		// new ParticipantCountdownTimer(this, "move", 100);
		new ParticipantCountdownTimer(this, "shoot", Constants.BULLET_DURATION);
	}

	@Override
	protected Shape getOutline() {

		return outline;
	}

	@Override
	public void collidedWith(Participant p) {
		if (!this.isAlienBullet) { //Point funtionality only works if not alien
			if (p instanceof Asteroid) {
				if (((Asteroid) p).getSize() == 2 && !this.isAlienBullet) {
					controller.addPoints(20);
				}
				if (((Asteroid) p).getSize() == 1 && !this.isAlienBullet) {
					controller.addPoints(50);
				}
				if (((Asteroid) p).getSize() == 0 && !this.isAlienBullet) {
					controller.addPoints(100);
				}
				// Tell the controller the asteroid was destroyed
				controller.asteroidDestroyed((Asteroid) p);
	
			}
	
			if (p instanceof AlienShip) {
				if (((AlienShip) p).getSize() == 1) {
					controller.addPoints(200);
				}
				if (((AlienShip) p).getSize() == 0) {
					controller.addPoints(50);
				}
	
				if (!isAlienBullet) {
					// Expire the bullet from the game
					Participant.expire(p);
					controller.alienDestroyed((AlienShip) p);
				}
			}
		}
		
		if (p instanceof AlienShip) {
			if (!isAlienBullet) {
				// Expire the bullet from the game
				Participant.expire(p);
				controller.alienDestroyed((AlienShip) p);
			}
		}
		
		if (p instanceof Asteroid) {
			// Tell the controller the asteroid was destroyed
			controller.asteroidDestroyed((Asteroid) p);

		}
		// Expire the bullet from the game
		Participant.expire(this);
	}

	public void setAlienBullet() {
		isAlienBullet = true;
	}

	@Override
	public void countdownComplete(Object payload) {
		expire(this);
	}

}
