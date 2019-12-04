package asteroids.participants;

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
		// TODO Auto-generated constructor stub
		
		
	}


	@Override
	public Shape getOutline() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void collidedWith(Participant p) {
		// TODO Auto-generated method stub
		
	}

}
