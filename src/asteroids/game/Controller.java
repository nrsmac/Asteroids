package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

import asteroids.participants.AlienShip;
import asteroids.participants.Asteroid;
import asteroids.participants.Bullet;
import asteroids.participants.Debris;
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

	/** Turning right checked every refresh */
	private boolean turningRight = false;

	/** Turning left checked every refresh */
	private boolean turningLeft = false;

	/** Going forward, checked every refresh */
	private boolean movingForward;

	/** level */
	public int level;

	/** points */
	public int points;

	/** beat timer */
	Timer beatTimer;

	/** alien timer */
	Timer alienTimer;

	/** Clip objects */
	Clip bangAlienShip;
	Clip bangShip;
	Clip bangLarge;
	Clip bangMedium;
	Clip bangSmall;
	Clip beat1;
	Clip beat2;
	Clip fireClip;
	Clip saucerBig;
	Clip saucerSmall;
	Clip thrust;

	/**
	 * Constructs a controller to coordinate the game and screen
	 */
	public Controller() {
		// Initialize the ParticipantState
		pstate = new ParticipantState();

		// Set up the refresh timer.
		refreshTimer = new Timer(FRAME_INTERVAL, this);

		// Set up the alien timer.
		alienTimer = new Timer(500, this); // TODO make this ALIEN_DELAY

		// Clear the transitionTime
		transitionTime = Long.MAX_VALUE;

		// Record the display object
		display = new Display(this);

		// Bring up the splash screen and start the refresh timer
		splashScreen();
		display.setVisible(true);
		refreshTimer.start();

		movingForward = false;

		// Create sound clips to reduce lag
		bangAlienShip = createClip("/sounds/bangAlienShip.wav");
		bangShip = createClip("/sounds/bangShip.wav");
		fireClip = createClip("/sounds/fire.wav");
		saucerSmall = createClip("/sounds/saucerSmall.wav");
		bangLarge = createClip("/sounds/bangLarge.wav");
		bangMedium = createClip("/sounds/bangMedium.wav");
		bangSmall = createClip("/sounds/bangSmall.wav");
		beat1 = createClip("/sounds/beat1.wav");
		beat2 = createClip("/sounds/beat2.wav");
		saucerBig = createClip("/sounds/saucerBig.wav");
		saucerSmall = createClip("/sounds/saucerSmall.wav");
		thrust = createClip("/sounds/thrust.wav");

		beatTimer = new Timer(INITIAL_BEAT, this);
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
		placeAsteroids(4);
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
		addParticipant(new Bullet(ship, this, ship.getRotation()));
		if (fireClip.isRunning()) {
			fireClip.stop();
		}
		fireClip.setFramePosition(0);
		fireClip.start();
	}

	// Shoots Big Alien bullets
	public void shootBigAlienBullet(AlienShip alien) {
		Bullet bullet = new Bullet(alien, this,
				alien.getRelativeAngle(getClosestParticipantToAlien(alien)));
		bullet.isAlienBullet = true;
		addParticipant(bullet);
		if (fireClip.isRunning()) {
			fireClip.stop();
		}
		fireClip.setFramePosition(0);
		fireClip.start();
	}

	// Shoots Big Alien bullets
	public void shootSmallAlienBullet(AlienShip alien) {
		if (fireClip.isRunning()) {
			fireClip.stop();
		}
		try {
			Bullet bullet = null;
			bullet = new Bullet(alien, this, ship.getRelativeAngle(alien));
			bullet.isAlienBullet = true;
			addParticipant(bullet);
			fireClip.setFramePosition(0);
			fireClip.start();
		} catch (Exception e) {

		}
	}

	private Participant getClosestParticipantToAlien(AlienShip alien) {
		LinkedList<Participant> participants = pstate.getParticipants();
		Participant closestParticipant = participants.get(0);

		for (Participant p : participants) {
			double pDistanceFromAlien = participantsDistance(closestParticipant,
					p);

			if (pDistanceFromAlien > participantsDistance(closestParticipant,
					alien)) {
				closestParticipant = p;
			}
		}

		return closestParticipant;
	}

	private double participantsDistance(Participant p1, Participant p2) {
		double p1X = p1.getX();
		double p1Y = p1.getY();
		double p2X = p2.getX();
		double p2Y = p2.getY();
		double pDistanceFromAlien = Math
				.sqrt(Math.pow((p2X - p1X), 2) + Math.pow((p2Y - p1Y), 2));

		return pDistanceFromAlien;

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
		beatTimer.start();
	}

	/**
	 * Places 4 asteroids near corners of the screen. Gives them a random
	 * velocity and rotation.
	 */
	private void placeAsteroids(int asteroids) {

		if (asteroids == 1) {
			addParticipant(
					new Asteroid(0, 1, EDGE_OFFSET, EDGE_OFFSET, 3, this));
		}

		if (asteroids == 4) {
			addParticipant(
					new Asteroid(0, 2, EDGE_OFFSET, EDGE_OFFSET, 3, this));
			addParticipant(
					new Asteroid(1, 2, -EDGE_OFFSET, EDGE_OFFSET, 3, this));
			addParticipant(
					new Asteroid(2, 2, EDGE_OFFSET, -EDGE_OFFSET, 3, this));
			addParticipant(
					new Asteroid(3, 2, -EDGE_OFFSET, -EDGE_OFFSET, 3, this));
		}

		if (asteroids == 5) {
			addParticipant(
					new Asteroid(0, 2, EDGE_OFFSET, EDGE_OFFSET, 3, this));
			addParticipant(
					new Asteroid(1, 2, -EDGE_OFFSET, EDGE_OFFSET, 3, this));
			addParticipant(
					new Asteroid(2, 2, EDGE_OFFSET, -EDGE_OFFSET, 3, this));
			addParticipant(
					new Asteroid(3, 2, -EDGE_OFFSET, -EDGE_OFFSET, 3, this));
			addParticipant(new Asteroid(0, 2, -EDGE_OFFSET, -EDGE_OFFSET - 45,
					3, this));
			// addParticipant(
			// new Asteroid(3, 0, -EDGE_OFFSET, -EDGE_OFFSET, 3, this));
		}

		if (asteroids == 6) {
			addParticipant(
					new Asteroid(0, 2, EDGE_OFFSET, EDGE_OFFSET, 3, this));
			addParticipant(
					new Asteroid(1, 2, -EDGE_OFFSET, EDGE_OFFSET, 3, this));
			addParticipant(
					new Asteroid(2, 2, EDGE_OFFSET, -EDGE_OFFSET, 3, this));
			addParticipant(
					new Asteroid(3, 2, -EDGE_OFFSET, -EDGE_OFFSET, 3, this));
			addParticipant(new Asteroid(0, 2, -EDGE_OFFSET, -EDGE_OFFSET - 45,
					3, this));
			addParticipant(new Asteroid(1, 2, -EDGE_OFFSET, EDGE_OFFSET + 45, 3,
					this));
			// addParticipant(
			// new Asteroid(3, 0, -EDGE_OFFSET, -EDGE_OFFSET, 3, this));
		}
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
	 * begins a new game.
	 */
	private void initialScreen() {
		// Clear the screen
		clear();

		// Reset statistics
		lives = 3;
		display.setLives(lives);

		level = 2;
		points = 0;

		// Start listening to events (but don't listen twice)
		display.removeKeyListener(this);
		display.addKeyListener(this);

		// Give focus to the game screen
		display.requestFocusInWindow();

		setUpLevel();
	}

	/**
	 * Sets differences in level
	 */
	private void setUpLevel() {

		// corresponding level parameters
		if (level == 1) {
			// Place asteroids
			placeAsteroids(4);

			// Place the ship
			placeShip();
		}

		if (level == 2) {
			// Place asteroids
			placeAsteroids(5);

			// Place the ship
			placeShip();

			alienTimer.start();
		}

		if (level == 3) {
			// Place asteroids
			placeAsteroids(6);

			// Place the ship
			placeShip();

			alienTimer.start();
		}
		// set text
		display.setLevel(level);

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

		// Make sure controls are cleared
		turningRight = false;
		turningLeft = false;
		movingForward = false;

		// make sure thrust sound doesnt continue
		thrust.stop();

		// Decrement lives
		lives--;
		display.setLives(lives);

		if (bangShip.isRunning()) {
			bangShip.stop();
		}
		bangShip.setFramePosition(0);
		bangShip.start();

		if (lives <= 0) {
			// Keep it quiet as to not rub it in their face and stop making aliens
			saucerBig.stop();
			saucerSmall.stop();
			alienTimer.stop();
			
			//Set legend
			display.setLegend("Game Over");

		} else {
			// Since the ship was destroyed, schedule a transition
			scheduleTransition(END_DELAY);
		}

	}

	/**
	 * An asteroid has been destroyed
	 */

	public void asteroidDestroyed(Asteroid asteroid) {
		// If all the asteroids are gone, schedule a transition
		if (countAsteroids() == 0) {
			scheduleTransition(END_DELAY);
			if (bangSmall.isRunning()) {
				bangSmall.stop();
			}
			bangSmall.setFramePosition(0);
			bangSmall.start();
		}

		// Expire the asteroid from the game
		Participant.expire(asteroid);

		/*
		 * When a large asteroid collides with a bullet or a ship, the asteroid
		 * splits into two medium (1) asteroids. When a medium asteroid
		 * collides, it splits into two small (0) asteroids. When a small
		 * asteroid collides, it disappears.
		 */
		if (asteroid.getSize() == 2) { // If large asteroid is destroyed
			if (bangLarge.isRunning()) {
				bangLarge.stop();
			}
			bangLarge.setFramePosition(0);
			bangLarge.start();

			addParticipant(new Asteroid(2, 1, asteroid.getX(), asteroid.getY(),
					3, this));
			addParticipant(new Asteroid(1, 1, asteroid.getX(), asteroid.getY(),
					3, this));
		}

		if (asteroid.getSize() == 1) { // If large asteroid is destroyed
			if (bangMedium.isRunning()) {
				bangMedium.stop();
			}
			bangMedium.setFramePosition(0);
			bangMedium.start();

			addParticipant(new Asteroid(0, 0, asteroid.getX(), asteroid.getY(),
					3, this));
			addParticipant(new Asteroid(2, 0, asteroid.getX(), asteroid.getY(),
					3, this));
		}

		if (asteroid.getSize() == 0) { // If large asteroid is destroyed
			if (bangSmall.isRunning()) {
				bangSmall.stop();
			}
			bangLarge.setFramePosition(0);
			bangLarge.start();
		}
	}

	// public void asteroidDestroyed() {
	// // If all the asteroids are gone, schedule a transition
	// if (countAsteroids() == 0) {
	// scheduleTransition(END_DELAY);
	// }
	// }

	public void alienDestroyed(AlienShip alien) {
		saucerBig.stop();
		saucerSmall.stop();

		if (alien.size == 1)
			genDebris(alien.getX(), alien.getY(), "alienship");
		else
			genDebris(alien.getX(), alien.getY(), "alienshipsmall");

	}

	public void genDebris(double d, double e, String type) {
		int numGen = 0;
		switch (type) {
			case "asteroid" :
				numGen = 4;
				break;
			case "playership" :
				numGen = 2;
				addParticipant(new Debris(d, e, "playershipshort"));
				break;
			case "alienship" :
			case "alienshipsmall" :
				numGen = 6;
				break;
		}
		for (int i = 0; i < numGen; i++) {
			addParticipant(new Debris(d + i, e + i, type));
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
		} else if (e.getSource() == beatTimer
				&& beatTimer.getDelay() <= FASTEST_BEAT) {
			beatTimer.setDelay(beatTimer.getDelay() + 1);
			if (beat1.isRunning()) {
				beat1.stop();
			}
			beat1.setFramePosition(0);
			beat1.start();
			try {
				beatTimer.wait(beatTimer.getDelay() / 2);
				if (beat2.isRunning()) {
					beat2.stop();
				}
				beat2.setFramePosition(0);
				beat2.start();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == alienTimer && countAlienShips() == 0
				&& level == 2) {
			placeNewAlienShip(1);
		} else if (e.getSource() == alienTimer && countAlienShips() == 0
				&& level == 3) {
			placeNewAlienShip(0);
		}

		display.setPoints(points); // make sure our point text is being updated!

		// Turning logic
		if (turningRight && ship != null) {
			ship.turnRight();
		}

		if (turningLeft && ship != null) {
			ship.turnLeft();
		}

		if (movingForward && ship != null) {
			ship.accelerate();
		}

		// Manages Level variable
		if (countAsteroids() == 0 && !ship.isExpired()
				&& countAlienShips() == 0) {
			if (level > 3) {
				display.setLegend("You Won!");
				display.setLevel(3);
			} else {
				scheduleTransition(END_DELAY);
				level++;
				setUpLevel();

			}
		}

		// Plays alien sounds
		if (countBigAlienShips() > 0) {
			saucerBig.loop(Clip.LOOP_CONTINUOUSLY);
		} else {
			saucerBig.stop();
		}

		if (countSmallAlienShips() > 0) {
			saucerSmall.loop(Clip.LOOP_CONTINUOUSLY);
		} else {
			saucerSmall.stop();
		}

	}

	private void placeNewAlienShip(int size) {

		int x;
		int y;
		int direction;

		Random rand = new Random();

		y = rand.nextInt((490 - 20) + 1) + 490; // within acceptable x range
		if (rand.nextInt(2) == 0) {
			x = 0;
			direction = AlienShip.RIGHT_DIRECTION;
		} else {
			x = SIZE;
			direction = AlienShip.LEFT_DIRECTION;
		}

		addParticipant(new AlienShip(x, y, this, size, direction));
	}

	/**
	 * If the transition time has been reached, transition to a new state
	 */
	private void performTransition() {
		// Do something only if the time has been reached
		if (transitionTime <= System.currentTimeMillis()) {
			// Clear the transition time
			transitionTime = Long.MAX_VALUE;

			placeShip();
			// If there are no lives left, the game is over. Show the final
			// screen.
			if (lives <= 0) {
				finalScreen();
			}
		}
	}

	private void startThrustSound() {
		if (ship.lit) {
			thrust.loop(Clip.LOOP_CONTINUOUSLY);
		} else {
			thrust.stop();
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
	 * Returns the number of big aliens that are active participants
	 */
	private int countBigAlienShips() {
		int count = 0;
		for (Participant p : this) {
			if (p instanceof AlienShip && ((AlienShip) p).getSize() == 1) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Returns the number of small aliens that are active participants
	 */
	private int countSmallAlienShips() {
		int count = 0;
		for (Participant p : this) {
			if (p instanceof AlienShip && ((AlienShip) p).getSize() == 0) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Returns the total number of aliens that are active participants
	 */
	private int countAlienShips() {
		int count = 0;
		count += countSmallAlienShips();
		count += countBigAlienShips();
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

	public void addPoints(int points) {
		this.points += points;
	}

	/**
	 * Creates an audio clip from a sound file.
	 */
	public Clip createClip(String soundFile) {
		// Opening the sound file this way will work no matter how the
		// project is exported. The only restriction is that the
		// sound files must be stored in a package.
		try (BufferedInputStream sound = new BufferedInputStream(
				getClass().getResourceAsStream(soundFile))) {
			// Create and return a Clip that will play a sound file. There are
			// various reasons that the creation attempt could fail. If it
			// fails, return null.
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(sound));
			return clip;
		} catch (LineUnavailableException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (UnsupportedAudioFileException e) {
			return null;
		}
	}

	/**
	 * If a key of interest is pressed, record that it is down.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if ((e.getKeyCode() == KeyEvent.VK_RIGHT
				|| e.getKeyCode() == KeyEvent.VK_D) && ship != null) {
			turningRight = true;
			startThrustSound();
		}
		e.getClass();
		if ((e.getKeyCode() == KeyEvent.VK_LEFT
				|| e.getKeyCode() == KeyEvent.VK_A) && ship != null) {
			turningLeft = true;
		}
		if ((e.getKeyCode() == KeyEvent.VK_UP
				|| e.getKeyCode() == KeyEvent.VK_W) && ship != null) {
			movingForward = true;

			ship.lit = true;
			ship.firing = true;

			new ParticipantCountdownTimer(ship, "flicker", 50);
			startThrustSound();

		}
		if ((e.getKeyCode() == KeyEvent.VK_DOWN
				|| e.getKeyCode() == KeyEvent.VK_S
				|| e.getKeyCode() == KeyEvent.VK_SPACE) && ship != null) {
			if (countBullets() < 9) {
				shootBullet();
			}
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if ((e.getKeyCode() == KeyEvent.VK_UP
				|| e.getKeyCode() == KeyEvent.VK_W) && ship != null) {
			movingForward = false;
			ship.firing = false;
			ship.lit = false;
			thrust.stop();
		}
		if ((e.getKeyCode() == KeyEvent.VK_RIGHT
				|| e.getKeyCode() == KeyEvent.VK_D) && ship != null) {
			turningRight = false;
		}
		if ((e.getKeyCode() == KeyEvent.VK_LEFT
				|| e.getKeyCode() == KeyEvent.VK_A) && ship != null) {
			turningLeft = false;
		}
	}

}
