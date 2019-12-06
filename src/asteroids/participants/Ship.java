package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import asteroids.destroyers.*;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

/**
 * Represents ships
 */
public class Ship extends Participant implements AsteroidDestroyer
{
    /** The outline of the ship */
    public Shape outline;

    /** Game controller */
    protected Controller controller;
    
    public boolean lit;
    
    public boolean firing;

    /**
     * Constructs a ship at the specified coordinates that is pointed in the given direction.
     */
    public Ship (int x, int y, double direction, Controller controller)
    {
        this.controller = controller;
        setPosition(x, y);
        setRotation(direction);

        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(21, 0);
        poly.lineTo(-21, 12);
        poly.lineTo(-14, 10);
        poly.lineTo(-14, -10);
        poly.lineTo(-21, -12);
        poly.closePath();
        outline = poly;

        lit = false;
        firing = false;
        // Schedule an acceleration in two seconds
//        new ParticipantCountdownTimer(this, "flicker", 100);
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's nose is located.
     */
    public double getXNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getX();
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's nose is located.
     */
    public double getYNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getY();
    }

    @Override
	public Shape getOutline ()
    {
        return outline;
    }
    

    /**
     * Customizes the base move method by imposing friction
     */
    @Override
    public void move ()
    {
    	//Dont apply friction if it's an alien ship
        if (!(this instanceof AlienShip)) {
        	applyFriction(SHIP_FRICTION);
        }
        super.move();
    }

    /**
     * Turns right by Pi/16 radians
     */
    public void turnRight ()
    {
        rotate(Math.PI / 16);
    }

    /**
     * Turns left by Pi/16 radians
     */
    public void turnLeft ()
    {
        rotate(-Math.PI / 16);
    }

    /**
     * Accelerates by SHIP_ACCELERATION
     */
    public void accelerate ()
    {
        accelerate(SHIP_ACCELERATION);
    }

    /**
     * Draws the flame tail from the rocket when the up key is pressed
     */
    public void drawFlame()
    {
    	Path2D.Double poly = new Path2D.Double();
        poly.moveTo(21, 0);
        poly.lineTo(-21, 12);
        poly.lineTo(-14, 10);
        poly.lineTo(-14, -10);
        //the flame
        poly.lineTo(-14, 6);
        poly.lineTo(-25, 0);
        poly.lineTo(-14, -6);
        //end flame
        poly.lineTo(-14, -10);
        poly.lineTo(-21, -12);
        poly.closePath();
        outline = poly;
    }

    /**
     * Draws the rocket without the flame
     */
    public void undrawFlame()
    {
    	Path2D.Double poly = new Path2D.Double();
        poly.moveTo(21, 0);
        poly.lineTo(-21, 12);
        poly.lineTo(-14, 10);
        poly.lineTo(-14, -10);
        poly.lineTo(-21, -12);
        poly.closePath();
        outline = poly;
    }

    public void showFlame()
    {
    	if (lit) {
    		drawFlame();
    		lit = false;
    	}
    	else {
    		undrawFlame();
    		lit = true;
    	}
    }

    /**
     * When a Ship collides with a ShipDestroyer, it expires
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof ShipDestroyer && (p instanceof Bullet && ((Bullet) p).isAlienBullet))
        {
        	controller.genDebris(getX(), getY(), "playership");
            // Expire the ship from the game
            Participant.expire(this);

            
        }
        
		if (p instanceof Asteroid) {
			// Tell the controller the asteroid was destroyed
			controller.asteroidDestroyed((Asteroid)p);
			// Tell the controller the ship was destroyed
            getController().shipDestroyed();
		}
		
		if (p instanceof Bullet) {
			// Expire the ship from the game
            Participant.expire(this);

            // Tell the controller the ship was destroyed
            getController().shipDestroyed();
		}
    }
    

    /**
     * This method is invoked when a ParticipantCountdownTimer completes its countdown.
     */
    @Override
    public void countdownComplete (Object payload)
    {
        if (payload.equals("flicker"))
        {
        	showFlame();
        	if (firing) {
        		new ParticipantCountdownTimer(this, "flicker", 50);
        	}
        	else 
        		undrawFlame();
            
        }
    }

	public Controller getController() {
		return controller;
	}

}
