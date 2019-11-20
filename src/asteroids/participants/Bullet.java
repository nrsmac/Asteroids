package asteroids.participants;

import static asteroids.game.Constants.RANDOM;
import static asteroids.game.Constants.SIZE;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import asteroids.destroyers.AsteroidDestroyer;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

public class Bullet extends Participant implements AsteroidDestroyer {
	/** Game controller */
	private Controller controller;

	/* Contains the shape */
	private Shape outline;

	public Bullet(Ship ship, Controller controller) {
		this.controller = controller;
		setPosition(ship.getXNose(), ship.getYNose());
		setRotation(ship.getDirection());
		setVelocity(50, ship.getDirection());

		Path2D.Double poly = new Path2D.Double();
		poly.moveTo(ship.getXNose(), ship.getYNose() + 1);
<<<<<<< Updated upstream
		poly.lineTo(ship.getXNose(), ship.getYNose() + 4);
		poly.lineTo(ship.getXNose() - 4, ship.getYNose());
		poly.lineTo(ship.getXNose() + 4, ship.getYNose());
=======
		poly.lineTo(ship.getXNose(), ship.getYNose() + 2);
		poly.lineTo(ship.getXNose(), ship.getYNose() + 70);
		poly.lineTo(ship.getXNose()+22, ship.getYNose() + 34);
>>>>>>> Stashed changes
		poly.closePath();
		outline = poly;

		// // Schedule an acceleration in two seconds
		// new ParticipantCountdownTimer(this, "move", 100);
	}

	@Override
	protected Shape getOutline() {
		return outline;
	}

	@Override
	public void collidedWith(Participant p) {
		if (p instanceof AsteroidDestroyer) {
			// Expire the bullet from the game
			Participant.expire(this);
<<<<<<< Updated upstream

			// Tell the controller the asteroid was destroyed
=======
			
			// Tell the controller the ship was destroyed
>>>>>>> Stashed changes
			controller.asteroidDestroyed();
		}

	}


}
