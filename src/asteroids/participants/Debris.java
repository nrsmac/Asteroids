package asteroids.participants;

import static asteroids.game.Constants.RANDOM;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

import asteroids.game.Constants;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

public class Debris extends Participant{
	
	/**The outline of the debris, based on which entity it was created from*/
	private Shape outline;

	/**
	 * Constructs a piece of debris with random velocity and rotation 
	 * from one of the possible units upon whose "death" debris may be generated.
	 */
	public Debris(double x, double y, String type) {
		super();
		switch (type)
		{
		case "asteroid":
			outline = outlineAsteroid();
			break;
		case "playership":
			outline = outlinePlayerShip();
			break;
		case "playershipshort":
			outline = outlinePlayerShipShort();
		case "alienship":
			outline = outlineAlienShip();
			break;
		case "alienshipsmall":
			outline = outlineAlienShip();
			break;
		}
		setVelocity(.2, RANDOM.nextDouble() * 2 * Math.PI);
		setRotation(2 * Math.PI * RANDOM.nextDouble());
		setPosition(x, y);
		setInert(true);
		new ParticipantCountdownTimer(this, "expire", Constants.END_DELAY);
	}
	
	/**
	 * Returns the shape of asteroid debris
	 */
	public Shape outlineAsteroid()
	{ 
		Ellipse2D.Double poly = new Ellipse2D.Double(0,0,1,1);
		return poly;
	}
	
	/**
	 * Returns the shape of the player ship debris (long segment)
	 */
	public Shape outlinePlayerShip()
	{
		Path2D.Double poly = new Path2D.Double();
		poly.moveTo(0, 0);
		poly.lineTo(24, 0);
		return poly;
	}
	
	/**
	 * Returns the shape of the player ship debris (short segment)
	 */
	public Shape outlinePlayerShipShort()
	{
		Path2D.Double poly = new Path2D.Double();
		poly.moveTo(0, 0);
		poly.lineTo(6, 0);
		return poly;
	}
	
	/**
	 * Returns the shape of an alien ship debris segment
	 */
	public Shape outlineAlienShip()
	{
		Path2D.Double poly = new Path2D.Double();
		poly.moveTo(0, 0);
		poly.lineTo(12, 0);
		return poly;
	}
	
	/**
	 * Returns the shape of an alien ship small debris segment
	 */
	public Shape outlineAlienShipSmall()
	{
		Path2D.Double poly = new Path2D.Double();
		poly.moveTo(0, 0);
		poly.lineTo(6, 0);
		return poly;
	}

	/**
	 * Expires the debris when the countdown timer triggers, set in the constructor
	 */
	@Override
	public void countdownComplete(Object payload)
	{
		if (payload.equals("expire"))
			Participant.expire(this);
	}


	@Override
	protected Shape getOutline() {
		return outline;
	}


	@Override
	public void collidedWith(Participant p) {
		//no action on collision; inert
	}

}
