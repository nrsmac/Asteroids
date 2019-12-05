package asteroids.participants;

import static asteroids.game.Constants.RANDOM;

import java.awt.Shape;
import java.awt.geom.Path2D;

import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Controller;
import asteroids.game.Participant;

public class AlienShip extends Ship implements ShipDestroyer {
	
	/** The outline of the ship */
    public Shape outline;

    /**
     * Constructs a ship at the specified coordinates and with a given size of 1,2 or 3
     */   
    
    public AlienShip(int x, int y, Controller controller, int size) {
		super(x, y, 0, controller);
		//TODO update outline so it's an alien ship shape, this is still the default ship shape.
		Path2D.Double poly = new Path2D.Double();
        poly.moveTo(21, 0);
        poly.lineTo(-21, 12);
        poly.lineTo(-14, 10);
        poly.lineTo(-14, -10);
        poly.lineTo(-21, -12);
        poly.closePath();
        outline = poly;
        
        setPosition(x, y);
		setVelocity(3, 0);
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
//    	applyFriction(0.05);
        super.move();
    }

	@Override
	public void collidedWith(Participant p) {
		// TODO Auto-generated method stub
		
	}

}
