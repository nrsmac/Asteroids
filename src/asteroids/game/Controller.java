package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.*;
import asteroids.participants.Asteroid;
import asteroids.participants.Bullet;
import asteroids.participants.Ship;

/**
 * Controls a game of Asteroids.
 */
public class Controller
		implements
			KeyListener,
			ActionListener,
			Iterable<Participant> {
	/** The state of all the Participants */
	private ParticipantState pstate;

	/** The ship (if one is active) or null (otherwise) */
	private Ship ship;

	/** When this timer goes off, it is time to refresh the animation */
	private Timer refreshTimer;

	/**
	 * The time at which a transition to a new stage of the game should be made.
	 * A transition is scheduled a few seconds in the future to give the user
	 * time to see what has happened before doing something like going to a new
	 * level or resetting the current level.
	 */
	private long transitionTime;

	/** Number of lives left */
	private int lives;

	/** The game display */
	private Display display;

	/** Turning right */
	boolean turningRight = false;

	/** Turning left */
	boolean turningLeft = false;

	public HashSet<KeyEvent> pressedEvents = new HashSet<>();
	public HashSet<KeyEvent> releasedEvents = new HashSet<>();

	/**
	 * Going forward
	 */
	boolean forward;

	/**
	 * Constructs a controller to coordinate the game and screen
	 */
	public Controller() {
		// Initialize the ParticipantState
		pstate = new ParticipantState();

		// Set up the refresh timer.
		refreshTimer = new Timer(FRAME_INTERVAL, this);

		// Clear the transitionTime
		transitionTime = Long.MAX_VALUE;

		// Record the display object
		display = new Display(this);

		// Bring up the splash screen and start the refresh timer
		splashScreen();
		display.setVisible(true);
		refreshTimer.start();
		
		lives = 3;

		forward = false;
	}

	/**
	 * This makes it possible to use an enhanced for loop to iterate through the
	 * Participants being managed by a Controller.
	 */
	@Override
	public Iterator<Participant> iterator() {
		return pstate.iterator();
	}

	/**
	 * Returns the ship, or null if there isn't one
	 */
	public Ship getShip() {
		return ship;
	}

	/**
	 * Configures the game screen to display the splash screen
	 */
	private void splashScreen() {
		// Clear the screen, reset the level, and display the legend
		clear();
		display.setLegend("Asteroids");

		// Place four asteroids near the corners of the screen.
		placeAsteroids();
	}

	/**
	 * The game is over. Displays a message to that effect.
	 */
	private void finalScreen() {
		display.setLegend(GAME_OVER);
		display.removeKeyListener(this);
	}

	// Shoots bullets
	private void shootBullet() {
		addParticipant(new Bullet(ship, this));
	}

	/**
	 * Place a new ship in the center of the screen. Remove any existing ship
	 * first.
	 */
	private void placeShip() {
		// Place a new ship
		Participant.expire(ship);
		ship = new Ship(SIZE / 2, SIZE / 2, -Math.PI / 2, this);
		addParticipant(ship);
		display.setLegend("");
	}

	/**
	 * Places 4 asteroids near corners of the screen. Gives them a random
	 * velocity and rotation.
	 */
	private void placeAsteroids() {
		addParticipant(new Asteroid(0, 2, EDGE_OFFSET, EDGE_OFFSET, 3, this));
		addParticipant(new Asteroid(1, 2, -EDGE_OFFSET, EDGE_OFFSET, 3, this));
		addParticipant(new Asteroid(2, 2, EDGE_OFFSET, -EDGE_OFFSET, 3, this));
		addParticipant(new Asteroid(3, 2, -EDGE_OFFSET, -EDGE_OFFSET, 3, this));
	}

	/**
	 * Clears the screen so that nothing is displayed
	 */
	private void clear() {
		pstate.clear();
		display.setLegend("");
		ship = null;
	}

	/**
	 * Sets things up and begins a new game.
	 */
	private void initialScreen() {
		// Clear the screen
		clear();

		// Place asteroids
		placeAsteroids();

		// Place the ship
		placeShip();

		// Reset statistics
//		lives = 3;
		display.setLivesText(lives);

		// Start listening to events (but don't listen twice)
		display.removeKeyListener(this);
		display.addKeyListener(this);

		// Give focus to the game screen
		display.requestFocusInWindow();
	}

	/**
	 * Adds a new Participant
	 */
	public void addParticipant(Participant p) {
		pstate.addParticipant(p);
	}

	/**
	 * The ship has been destroyed
	 */
	public void shipDestroyed() {
		// Null out the ship
		ship = null;

		// Decrement lives
		lives--;
		display.setLivesText(lives);

		// Since the ship was destroyed, schedule a transition
		scheduleTransition(END_DELAY);
		// TODO: Make ship burst into dust
		
		placeShip();

	}

	/**
	 * An asteroid has been destroyed
	 */
	public void asteroidDestroyed(Asteroid asteroid) {
		// If all the asteroids are gone, schedule a transition
		if (countAsteroids() == 0) {
			scheduleTransition(END_DELAY);
		}

		// Asteroid split functionality
		/*
		 * When a large asteroid collides with a bullet or a ship, the asteroid
		 * splits into two medium (1) asteroids. When a medium asteroid
		 * collides, it splits into two small (0) asteroids. When a small
		 * asteroid collides, it disappears.
		 */
		if (asteroid.getSize() == 2) { // If large asteroid is destroyed
			addParticipant(new Asteroid(2, 1, asteroid.getX(), asteroid.getY(),
					3, this));
			addParticipant(new Asteroid(1, 1, asteroid.getX(), asteroid.getY(),
					3, this));
		}

		if (asteroid.getSize() == 1) { // If large asteroid is destroyed
			addParticipant(new Asteroid(0, 0, asteroid.getX(), asteroid.getY(),
					3, this));
			addParticipant(new Asteroid(2, 0, asteroid.getX(), asteroid.getY(),
					3, this));
		}
	}

	public void asteroidDestroyed() {
		// If all the asteroids are gone, schedule a transition
		if (countAsteroids() == 0) {
			scheduleTransition(END_DELAY);
		}
	}

	/**
	 * Schedules a transition m msecs in the future
	 */
	private void scheduleTransition(int m) {
		transitionTime = System.currentTimeMillis() + m;
	}

	/**
	 * This method will be invoked because of button presses and timer events.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// The start button has been pressed. Stop whatever we're doing
		// and bring up the initial screen
		if (e.getSource() instanceof JButton) {
			initialScreen();
		}

		// Time to refresh the screen and deal with keyboard input
		else if (e.getSource() == refreshTimer) {
			// It may be time to make a game transition
			performTransition();

			// Move the participants to their new locations
			pstate.moveParticipants();

			// Refresh screen
			display.refresh();
		}

		// Turning logic
		if (turningRight && ship != null) {
			ship.turnRight();
		}

		if (turningLeft && ship != null) {
			ship.turnLeft();
		}

		if (forward && ship != null) {
			// ship.fire(); this causes the flame to be animated the entire time
			// TODO fix
		}

	}

	/**
	 * If the transition time has been reached, transition to a new state
	 */
	private void performTransition() {
		// Do something only if the time has been reached
		if (transitionTime <= System.currentTimeMillis()) {
			// Clear the transition time
			transitionTime = Long.MAX_VALUE;

			// If there are no lives left, the game is over. Show the final
			// screen.
			if (lives <= 0) {
				finalScreen();
			}
		}
	}

	/**
	 * Returns the number of asteroids that are active participants
	 */
	private int countAsteroids() {
		int count = 0;
		for (Participant p : this) {
			if (p instanceof Asteroid) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Returns the number of asteroids that are active participants
	 */
	private int countBullets() {
		int count = 0;
		for (Participant p : this) {
			if (p instanceof Bullet) {
				count++;
			}
		}
		return count;
	}

	/**
	 * If a key of interest is pressed, record that it is down.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT && ship != null) { // Send
																	// KeyEvent.VJAFKf
																	// to
																	// HashSet
			turningRight = true;
		}
		e.getClass();
		if (e.getKeyCode() == KeyEvent.VK_LEFT && ship != null) {
			// ship.turnLeft();
			turningLeft = true;
		} // If space, fire bullet
		if (e.getKeyCode() == KeyEvent.VK_UP && ship != null) {
			ship.accelerate();
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE && ship != null) {
			if (countBullets() < 9) {
				shootBullet();
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_UP && ship != null) {
			ship.fire();
			forward = true;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP && ship != null) {
			forward = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT && ship != null) {
			turningRight = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT && ship != null) {
			ship.turnLeft();
			turningLeft = false;
		}
	}
}
