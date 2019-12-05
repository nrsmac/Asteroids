package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.Path2D;

import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Controller;
import asteroids.game.Participant;

public class AlienShip extends Ship implements ShipDestroyer, AsteroidDestroyer {
	
	/** The outline of the ship */
    public Shape outline;
    
    /** The size of the alien ship, 1 for big, 0 for small */
    public int size;

    /**
     * Constructs a ship at the specified coordinates and with a given size of 1,2 or 3
     */   
    
    public AlienShip(int x, int y, Controller controller, int size) {
		super(x, y, 0, controller);
		this.size = size;
		
		//TODO update outline so it's an alien ship shape, this is still the default ship shape.
        if (size == 1) {
        	Path2D.Double poly = new Path2D.Double();
            poly.moveTo(21, 0);
            poly.lineTo(-21, 12);
            poly.lineTo(-14, 10);
            poly.lineTo(-14, -10);
            poly.lineTo(-21, -12);
            poly.closePath();
            outline = poly;
        } else if (size == 0) {
        	Path2D.Double poly = new Path2D.Double();
            poly.moveTo(10, 0);
            poly.lineTo(-10, 6);
            poly.lineTo(-7, 5);
            poly.lineTo(-7, -5);
            poly.lineTo(-10, -6);
            poly.closePath();
            outline = poly;
        } 
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
		Participant.expire(this);		
	}
	
	public int getSize() {
		return size;
	}

}
