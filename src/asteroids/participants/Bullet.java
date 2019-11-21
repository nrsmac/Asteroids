package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.Path2D;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Constants;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

public class Bullet extends Participant implements AsteroidDestroyer {
	/** Game controller */
	private Controller controller;

	/* Contains the shape */
	private Shape outline;

	public Bullet(Ship ship, Controller controller) {

		Path2D.Double poly = new Path2D.Double();
		poly.moveTo(ship.getXNose(), ship.getYNose());
		poly.lineTo(ship.getXNose(), ship.getYNose() + 1);
		poly.lineTo(ship.getXNose(), ship.getYNose() - 2);
		poly.moveTo(ship.getXNose(), ship.getYNose());
		poly.lineTo(ship.getXNose() - 1, ship.getYNose());
		poly.lineTo(ship.getXNose() + 2, ship.getYNose());
		poly.lineTo(ship.getXNose(), ship.getYNose());
		outline = poly;

		this.controller = controller;
		// setPosition(ship.getXNose(), ship.getYNose());

		setVelocity(Constants.BULLET_SPEED, ship.getRotation());
		poly.closePath();
		
		// // Schedule an acceleration in two seconds
		// new ParticipantCountdownTimer(this, "move", 100);
		new ParticipantCountdownTimer(this, "shoot", 100);
	}

	@Override
	protected Shape getOutline() {
		return outline;
	}

	@Override
	public void collidedWith(Participant p) {
		if (p instanceof ShipDestroyer) {
			// Expire the bullet from the game
			Participant.expire(this);
			// Tell the controller the asteroid was destroyed
//			controller.asteroidDestroyed();
		}
	}

	@Override
	public void countdownComplete(Object payload) {

	}

}
