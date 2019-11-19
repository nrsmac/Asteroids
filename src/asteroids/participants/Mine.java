package asteroids.participants;

import static asteroids.game.Constants.RANDOM;
import static asteroids.game.Constants.SHIP_FRICTION;
import static asteroids.game.Constants.SIZE;

import java.awt.Shape;
import java.awt.geom.Path2D;

import asteroids.destroyers.AsteroidDestroyer;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

public class Mine extends Participant implements AsteroidDestroyer {
	
	private Shape outline;

	public Mine () {
		setPosition(SIZE / 2, SIZE / 2);
		setRotation(30);
		setVelocity(2, RANDOM.nextDouble() * 2 * Math.PI);
		
		Path2D.Double poly = new Path2D.Double();
		poly.moveTo(55, 34);
        poly.lineTo(-1, 12);
        poly.lineTo(-14, 12);
        poly.lineTo(-6, -1);
        poly.lineTo(-21, -15);
        poly.closePath();
        outline = poly;
        
        new ParticipantCountdownTimer(this, "rotate", 2000);
	}
	 
	@Override
	protected Shape getOutline() {
		// TODO Auto-generated method stub
		return outline;
	}

	@Override
	public void collidedWith(Participant p) {
		// TODO Auto-generated method stub
		
	}
	/**
     * This method is invoked when a ParticipantCountdownTimer completes its countdown.
     */
	
    @Override
    public void countdownComplete (Object payload)
    {
        // Give a burst of acceleration, then schedule another
        // burst for 200 msecs from now.
        if (payload.equals("rotate"))
        {
            setVelocity (this.getSpeed(), this.getDirection()+Math.PI/2);
            new ParticipantCountdownTimer(this, "rotate", 2000);
        }
    }

}
