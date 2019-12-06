package asteroids.participants;

import static asteroids.game.Constants.RANDOM;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

public class Debris extends Participant{
	
	private Shape outline;

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
		new ParticipantCountdownTimer(this, "expire", 1400);
	}
	
	public Shape outlineAsteroid()
	{ 
		Ellipse2D.Double poly = new Ellipse2D.Double(0,0,1,1);
		return poly;
	}
	
	public Shape outlinePlayerShip()
	{
		Path2D.Double poly = new Path2D.Double();
		poly.moveTo(0, 0);
		poly.lineTo(24, 0);
		return poly;
	}
	
	public Shape outlinePlayerShipShort()
	{
		Path2D.Double poly = new Path2D.Double();
		poly.moveTo(0, 0);
		poly.lineTo(6, 0);
		return poly;
	}
	
	public Shape outlineAlienShip()
	{
		Path2D.Double poly = new Path2D.Double();
		poly.moveTo(0, 0);
		poly.lineTo(12, 0);
		return poly;
	}
	
	public Shape outlineAlienShipSmall()
	{
		Path2D.Double poly = new Path2D.Double();
		poly.moveTo(0, 0);
		poly.lineTo(6, 0);
		return poly;
	}


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
