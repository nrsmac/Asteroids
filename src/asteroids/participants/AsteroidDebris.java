package asteroids.participants;

import static asteroids.game.Constants.RANDOM;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

public class AsteroidDebris extends Participant{
	
	private Shape outline;

	public AsteroidDebris(double x, double y) {
		super();
		Ellipse2D.Double poly = new Ellipse2D.Double(0,0,1,1);
		outline = poly;
		setVelocity(1, RANDOM.nextDouble() * 2 * Math.PI);
		setRotation(2 * Math.PI * RANDOM.nextDouble());
		setPosition(x, y);
		setInert(true);
		new ParticipantCountdownTimer(this, "expire", 800);
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
