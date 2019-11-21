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

	public Bullet(Ship ship, Controller controller) {

		Ellipse2D.Double poly = new Ellipse2D.Double(0,0,1,1);
		outline = poly;

		this.controller = controller;
		 setPosition(ship.getXNose(), ship.getYNose());

		setVelocity(Constants.BULLET_SPEED, ship.getRotation());
		
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
		if (p instanceof Asteroid) {
//			System.out.println("bullet destroyed");
			// Expire the bullet from the game
			Participant.expire(this);
			// Tell the controller the asteroid was destroyed
			controller.asteroidDestroyed((Asteroid)p);
		}
	}

	@Override
	public void countdownComplete(Object payload) {

	}

}
