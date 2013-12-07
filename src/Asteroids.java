import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.imageio.*;



public class Asteroids {

	private static CollisionDetection cDetection;
   
	
	public static void main(String args[]) {
		Asteroids asteroidsGame = new Asteroids();
		cDetection = new CollisionDetection(); // Custom thread to always check for collision detection
	
	cDetection.start(); 
		asteroidsGame.run();
	}

	public static final String HIGHSCORE_FILENAME = "hs.txt";
	
	private static int NUM_OF_ASTEROIDS = 3;
	private static int LEVEL = 1;
	private static final int ALIEN_LEVEL_SPAWN = 3	; // spawns every 3 levels
	private static final int ALIENSHIP_HEIGHT = 75;
	public static boolean ALIEN_ACTIVE = false;
	private static int ALIEN_SPEED = 2;
	private static int ALIEN_FIRE_INTERVAL = 5000; // decrease this value to make it fire faster 
	private static long timeSinceAlienBullet = System.currentTimeMillis();
	private static int BULLET_COUNT = 0;

	public static boolean GRAV_OBJECT = false;
	public static boolean GRAV_OBJECT_VISIBLE = false;
	public static int GRAVITATIONAL_OBJECT_STRENGTH = 3;
	public static final float BASE_ASTEROID_SPEED = 0.05f;

	public static boolean MULTIPLAYER_ACTIVE = false;

	public static boolean SAVE_GAME = false;
	public static boolean LOAD_GAME = false;

	private static ScreenManager screen;

	public static boolean UNLIMITED_LIVES = false;
//	ImageIcon(fileName).getImage()
	static Image largeAsteroidImage = new ImageIcon("images/asteroids4.png").getImage();
	static Image smallAsteroidImage = new ImageIcon("images/asteroids3.png").getImage();
	static Image shipImage = new ImageIcon("images/greenShip.png").getImage();
	static Image alienImage = new ImageIcon("images/alienSpaceship.png").getImage();
	static Image bulletImage = new ImageIcon("images/bullets.png").getImage();
	static Image gravitationObjectImage = new ImageIcon("images/Blackhole1.png").getImage();

	private static final int playerSpeed = 10;
	private static final int bulletSpeed = 20;


	public static LinkedList<Sprite> asteroidSprites = new LinkedList<Sprite>(); 
	public static LinkedList<Player> playerSprites = new LinkedList<Player>();
	public static LinkedList<Bullet> bulletSprites = new LinkedList<Bullet>();
	public static Sprite alienSprite;
	public static Sprite gravitationalObjectSprite;

	// the following variables are all for getting the keyboard events and pausing the game itself
	protected GameAction turnLeftPlayer1;
	protected GameAction turnRightPlayer1;
	protected GameAction moveForwardPlayer1;
	protected GameAction moveBackwardPlayer1;
	protected GameAction turnLeftPlayer1Player2;
	protected GameAction turnRightPlayer1Player2;
	protected GameAction moveForwardPlayer1Player2;
	protected GameAction moveBackwardPlayer1Player2;

	protected GameAction fireBulletsPlayer1;
	protected GameAction fireBulletsPlayer2;
	protected GameAction exit;
	protected GameAction pause;
	protected GameAction pauseEnter;
	protected static InputManager inputManager;
	private boolean paused = true;
	public static boolean exitGame;

	
	//Menu
	private String[] buttonNames = {"Continue",
			"Save",
			"Load",
			"Gravitational Object: ",
			"Object Visible: ",
			"Unlimited Lives: ",
			"# Of Astroids: ",
			"Reset Scoreboard",
			"Starting Level: ",
			"Multiplayer: ",
			"Quit"
	};
	
	private int pauseMenuIndex = 0;
	public Map<String, Rectangle> buttonFrame = new HashMap<String,Rectangle>();
	private static Color buttonBackgroundColor = Color.red;
	private static Color buttonTextColor = Color.white;
	
	
	


	

	
	

	private boolean movingShip = false;
	
	
	public static void loadPlayerImage() {

		playerSprites.clear();
		
		Animation anim = new Animation();
		anim.addFrame(shipImage, 250);
		playerSprites.add(new Player(anim, 3));
		playerSprites.get(0).setX(400);
		playerSprites.get(0).setY(400);
	}

	public static void loadAsteroidImages() {

		asteroidSprites.clear();
		
		// create and init sprites
		for (int i = 0; i < NUM_OF_ASTEROIDS; i++) {
			Animation anim = new Animation();
			anim.addFrame(largeAsteroidImage, 250);
			asteroidSprites.add(new Sprite(anim, "large"));

			// select random starting location
			asteroidSprites.get(i).setX((float)Math.random() *
					(screen.getWidth() - asteroidSprites.get(i).getWidth()));
			asteroidSprites.get(i).setY((float)Math.random() * 
					(screen.getHeight() - asteroidSprites.get(i).getHeight()));

			// select random velocity

			Random random = new Random();

			asteroidSprites.get(i).setVelocityX((BASE_ASTEROID_SPEED + (LEVEL*0.01f)) * (float)( (random.nextBoolean() ? 1 : -1) * Math.sin(Math.toRadians(Math.random() * 1000 % 360))));
			asteroidSprites.get(i).setVelocityY((BASE_ASTEROID_SPEED + (LEVEL*0.01f)) * (float)( (random.nextBoolean() ? 1 : -1) * Math.cos(Math.toRadians(Math.random() * 1000 % 360))));
	//		asteroidSprites.get(i).setVelocityX((float)(BASE_ASTEROID_SPEED + (LEVEL*0.02f)*(Math.random()-1)*Math.sin((Math.random()*360))));
	//		asteroidSprites.get(i).setVelocityY((float)(BASE_ASTEROID_SPEED + (LEVEL*0.02f)*(Math.random()-1)*Math.cos((Math.random()*360))));
		}

	}

	public static void asteroidDestroyed(Sprite s, int numOfAsteroids) {
		for(int i = 0; i < numOfAsteroids; i++) {
			Animation anim = new Animation();
			anim.addFrame(smallAsteroidImage, 250);
			Sprite newAsteroid = new Sprite(anim, "small");

			newAsteroid.setX(s.getX());
			newAsteroid.setY(s.getY());


			Random random = new Random();

			// select random velocity
			newAsteroid.setVelocityX((BASE_ASTEROID_SPEED + (LEVEL*0.01f))* (float)( (random.nextBoolean() ? 1 : -1) * Math.sin(Math.toRadians(Math.random() * 1000 % 360))));
			newAsteroid.setVelocityY((BASE_ASTEROID_SPEED + (LEVEL*0.01f))*(float)( (random.nextBoolean() ? 1 : -1) * Math.sin(Math.toRadians(Math.random() * 1000 % 360))));

			asteroidSprites.add(newAsteroid);
		}
	}

	
	private void loadHighScore()
	{
		try
		{
			File fileObject = new File(HIGHSCORE_FILENAME); 
			FileReader fileReader = new FileReader(fileObject);
			BufferedReader bufferReader = new BufferedReader(fileReader);
			String str; 
			
			while((str = bufferReader.readLine()) != null) {
				String name = str.split(" ")[0];
				String score = str.split(" ")[1];
			}
			fileReader.close(); 			
		}
		catch (Exception e) {}
	}
	
	public static void initilization()
	{
		ALIEN_ACTIVE = false; 
		NUM_OF_ASTEROIDS = 3;
		LEVEL = 1;
		ALIEN_ACTIVE = false;
		ALIEN_SPEED = 2;
		ALIEN_FIRE_INTERVAL = 5000; // decrease this value to make it fire faster 
		timeSinceAlienBullet = System.currentTimeMillis();
		BULLET_COUNT = 0;

		GRAV_OBJECT = false;
		GRAV_OBJECT_VISIBLE = false;
		GRAVITATIONAL_OBJECT_STRENGTH = 3;

		MULTIPLAYER_ACTIVE = false;

		SAVE_GAME = false;
	    LOAD_GAME = false;
	    UNLIMITED_LIVES = false;
		 
		inputManager.resetAllGameActions();
		
		loadPlayerImage();
		loadAsteroidImages();
		
	}
	

	public void run() {
		screen = new ScreenManager();
		try {
			DisplayMode displayMode = screen.getCurrentDisplayMode();
			screen.setFullScreen(displayMode);
		    Window window = screen.getFullScreenWindow();
			inputManager = new InputManager(window);
			
			createButtons();
			
			
			initilization();
			
			
			
			
			
			loadHighScore();

	
			createAlienShip();
			createGravitationalObject();
			
			
			createGameActions();
			gameLoop();
		}
		finally {
			screen.restoreScreen();
		}
	}


	public void gameLoop() {
		long startTime = System.currentTimeMillis();
		long currTime = startTime;

		while (!exitGame) { // as long as 'E' is not pressed keep playing game
			if(asteroidSprites.size() == 0) {
				for(int i = 0; i < playerSprites.size(); i++) {
					Asteroids.playerSprites.get(i).setPlayerScore(Asteroids.playerSprites.get(i).getPlayerScore() + LEVEL*100);
				}
				LEVEL++;
				NUM_OF_ASTEROIDS = 2*LEVEL + 1;
				//System.out.println("Starting Level => " + LEVEL);
				if(LEVEL % ALIEN_LEVEL_SPAWN == 0) {
					ALIEN_ACTIVE = true;
				}
				loadAsteroidImages();
			}
			if(MULTIPLAYER_ACTIVE && playerSprites.size() == 1) {
				Animation anim = new Animation();
				anim.addFrame(shipImage, 250);
				playerSprites.add(new Player(anim, 3));

				
				playerSprites.get(1).setX(500);
				playerSprites.get(1).setY(500);


			}
			if(SAVE_GAME) {
	        		SAVE_GAME = false;
	        		cDetection.stop();
	        		saveGame();
	        	//	saveObjectsToFile();
	        		cDetection = new CollisionDetection();
	        		cDetection.start();
	        	}
			if(LOAD_GAME) {
				LOAD_GAME = false;
				cDetection.stop();
				loadGame();
	        	//	readObjectsFromFile();
	        		cDetection = new CollisionDetection();
	       		cDetection.start();
			}
			long elapsedTime =
				System.currentTimeMillis() - currTime;
			currTime += elapsedTime;

			// update the sprites
			update(elapsedTime);

			// draw and update screen
			Graphics2D g = screen.getGraphics();
			g.setBackground(Color.black); // set Background color
			g.clearRect(0, 0, screen.getWidth(), screen.getHeight());  // to clear the screen each time before drawing stuff

			updateScore(g);
			draw(g);
			if (isPaused())
			{
				checkGameInputPause();
			}
			g.dispose();
			screen.update();


			// take a nap
			try {
				Thread.sleep(20);
			}
			catch (InterruptedException ex) { }
		}

	}

	public void update(long elapsedTime) {
		// check input that can happen whether paused or not
		checkSystemInput();

		if (!isPaused()) {
			// check game input
			pauseMenuIndex = 0;
			checkGameInput();

			for (int i = 0; i < asteroidSprites.size(); i++) {

				Sprite s = asteroidSprites.get(i);

				// check sprite bounds
				if (s.getX() < 0.) {
					s.setX(screen.getWidth());
				}
				else if (s.getX() >= screen.getWidth()) {
					s.setX(0);
				}
				if (s.getY() < 0) {
					s.setY(screen.getHeight());
				}
				else if (s.getY() >= screen.getHeight()) {
					s.setY(0);
				}

				// update sprite
				s.update(elapsedTime);
			}

			for (int i = 0; i < playerSprites.size(); i++) {

				Player p = playerSprites.get(i);

				// check sprite bounds
				if (p.getX() < 0.) {
					p.setX(screen.getWidth());
				}
				else if (p.getX() >= screen.getWidth()) {
					p.setX(0);
				}
				if (p.getY() < 0) {
					p.setY(screen.getHeight());
				}
				else if (p.getY() >= screen.getHeight()) {
					p.setY(0);
				}

				// update sprite
				p.update(elapsedTime);
			}

			for (int i = 0; i < bulletSprites.size(); i++) {

				try 
				{
					Bullet b = bulletSprites.get(i);
					b.setX(b.getX() + (float)(bulletSpeed* Math.sin( Math.toRadians(b.getAngle())) ));
					b.setY(b.getY() - (float)(bulletSpeed* Math.cos( Math.toRadians(b.getAngle())) ));
					b.updateBulletDistance((int)Math.sqrt(2*Math.pow(bulletSpeed, 2)));

					// check sprite bounds
					if (b.getX() < 0.) {
						b.setX(screen.getWidth());
					}
					else if (b.getX() >= screen.getWidth()) {
						b.setX(0);
					}
					if (b.getY() < 0) {
						b.setY(screen.getHeight());
					}
					else if (b.getY() >= screen.getHeight()) {
						b.setY(0);
					}
				}

				catch (Exception e)
				{
				}
			}

			// if alienship alive then move it!
			if(ALIEN_ACTIVE) {
				if(alienSprite.getX() > screen.getWidth()) {
					alienSprite.setX(0);
				}
				else {
					alienSprite.setX(alienSprite.getX() + (LEVEL)*ALIEN_SPEED);
				}

				addAlienBulletsToScreen();
			}

			if(GRAV_OBJECT) {
				for(int i = 0; i < playerSprites.size(); i++) {
					if(playerSprites.get(i).getX() < gravitationalObjectSprite.getX()+100) {
						playerSprites.get(i).setX(playerSprites.get(i).getX() + GRAVITATIONAL_OBJECT_STRENGTH);
					}
					else {
						playerSprites.get(i).setX(playerSprites.get(i).getX() - GRAVITATIONAL_OBJECT_STRENGTH);
					}
					if(playerSprites.get(i).getY() < gravitationalObjectSprite.getY()+100) {
						playerSprites.get(i).setY(playerSprites.get(i).getY() + GRAVITATIONAL_OBJECT_STRENGTH);
					}
					else {
						playerSprites.get(i).setY(playerSprites.get(i).getY() - GRAVITATIONAL_OBJECT_STRENGTH);
						
					}	
				}
			}
		}
	}


	public void draw(Graphics2D g) {
		AffineTransform transform = new AffineTransform();

		if(GRAV_OBJECT && GRAV_OBJECT_VISIBLE) {
			transform.setToTranslation(gravitationalObjectSprite.getX(), gravitationalObjectSprite.getY());
			if (gravitationalObjectSprite.getVelocityX() < 0) {
				transform.scale(-1, 1);
				transform.translate(-gravitationalObjectSprite.getWidth(), 0);
			}
			g.drawImage(gravitationalObjectSprite.getImage(), transform, null);
		}
		if(asteroidSprites != null){
		for (int i = 0; i < asteroidSprites.size(); i++) {
			Sprite sprite = asteroidSprites.get(i);

			// translate the sprite
			transform.setToTranslation(sprite.getX(),
					sprite.getY());

			// if the sprite is moving left, flip the image
			if (sprite.getVelocityX() < 0) {
				transform.scale(-1, 1);
				transform.translate(-sprite.getWidth(), 0);
			}

			// draw it
			g.drawImage(sprite.getImage(), transform, null);
		}
		}
		if (playerSprites != null){
		for(int i = 0; i < playerSprites.size(); i++) {
			Player player = playerSprites.get(i);
			// translate the sprite
			transform.setToTranslation(player.getX(), player.getY());

			//Player rotation
			transform.rotate(Math.toRadians(player.getAngle()));

			// if the sprite is moving left, flip the image
			if (player.getVelocityX() < 0) {
				transform.scale(-1, 1);
				transform.translate(-player.getWidth(), 0);
			}
			g.drawImage(player.getImage(), transform, null);
		}
		}
		
		for(int i = 0; i < bulletSprites.size(); i++) {
			Bullet bullet = bulletSprites.get(i);
			// translate the sprite
			transform.setToTranslation(bullet.getX(), bullet.getY());

			// if the sprite is moving left, flip the image
			if (bullet.getVelocityX() < 0) {
				transform.scale(-1, 1);
				transform.translate(-bullet.getWidth(), 0);
			}
			g.drawImage(bullet.getImage(), transform, null);
		}

		if(ALIEN_ACTIVE) { // if alienShip spawn interval has been reached spawn it
			transform.setToTranslation(alienSprite.getX(), alienSprite.getY());
			// 	if the sprite is moving left, flip the image
			if (alienSprite.getVelocityX() < 0) {
				transform.scale(-1, 1);
				transform.translate(-alienSprite.getWidth(), 0);
			}
			g.drawImage(alienSprite.getImage(), transform, null);
		}

		if (isPaused())
		{
			Font font = new Font("Dialog", Font.PLAIN, 15); 
			for (int i=0 ; i < buttonFrame.size() ; i++)
			{
				g.setFont(font);
				g.setColor(buttonBackgroundColor);
				g.fillRoundRect(buttonFrame.get(buttonNames[i]).x, buttonFrame.get(buttonNames[i]).y, buttonFrame.get(buttonNames[i]).width, buttonFrame.get(buttonNames[i]).height,10,10);
				if (pauseMenuIndex == i) g.setColor(Color.black);
				else g.setColor(buttonTextColor);

				String optionString = buttonNames[i];

				if (optionString == "Gravitational Object: "){ 
					if (GRAV_OBJECT){
						optionString += "on";
					}
					else{
						optionString += "off";
					}
				}
				else if (optionString == "Object Visible: "){
					if (GRAV_OBJECT_VISIBLE){
						optionString += "on";
					}
					else{
						optionString += "off";
					}
				}
				else if (optionString == "Unlimited Lives: "){
					if(UNLIMITED_LIVES){
						optionString += "on";
					}
					else{
						optionString += "off";
					}						
				}
				else if (optionString == "# Of Astroids: "){
					optionString += NUM_OF_ASTEROIDS;
				}
				else if (optionString == "Starting Level: ") {
					optionString += LEVEL;
				}
				else if (optionString == "Multiplayer: "){
				
					if(MULTIPLAYER_ACTIVE){
						optionString += "on";
					}
					else{
						optionString += "off";
					}
				}
				g.drawString(optionString, buttonFrame.get(buttonNames[i]).x + 15 , buttonFrame.get(buttonNames[i]).y + 15);

			}

		}

	}

	/**
	    Tests whether the game is paused or not.
	 */
	public boolean isPaused() {
		return paused;
	}


	/**	
	    Sets the paused state.
	 */
	public void setPaused(boolean p) {
		if (paused != p) {
			this.paused = p;
			inputManager.resetAllGameActions();
		}
	}


	/**
	        Checks input from GameActions that can be pressed
	        regardless of whether the game is paused or not.
	 */
	public void checkSystemInput() {
		if (pause.isPressed()) {
			setPaused(!isPaused());
		}
		if(exit.isPressed()) {
			exitGame = true;
		}
	}

	/**
        Checks input from GameActions that can be pressed
        only when the game is not paused.
	 */
	public void checkGameInput() {
		boolean holding = false;
		if (turnRightPlayer1.isPressed()) {
			playerSprites.get(0).setAngle(playerSprites.get(0).getAngle() + Math.toRadians(360));

		}
		if (turnLeftPlayer1.isPressed()) {
			playerSprites.get(0).setAngle(playerSprites.get(0).getAngle() - Math.toRadians(360));
		}
		if(moveForwardPlayer1.isPressed()) {
			playerSprites.get(0).setX(playerSprites.get(0).getX() + (float)(playerSpeed* Math.sin( Math.toRadians(playerSprites.get(0).getAngle())) ));
			playerSprites.get(0).setY(playerSprites.get(0).getY() - (float)(playerSpeed* Math.cos( Math.toRadians(playerSprites.get(0).getAngle())) ));
			
			movingShip = true;
			holding = true;

		}
		if(moveBackwardPlayer1.isPressed()) {
			playerSprites.get(0).setX(playerSprites.get(0).getX() - (float)(playerSpeed* Math.sin( Math.toRadians(playerSprites.get(0).getAngle())) ));
			playerSprites.get(0).setY(playerSprites.get(0).getY() + (float)(playerSpeed* Math.cos( Math.toRadians(playerSprites.get(0).getAngle())) ));
			
			movingShip = true;
			holding = true;
		}
		if (fireBulletsPlayer1.isPressed()) {
			addBullets(0);
		}
		if(MULTIPLAYER_ACTIVE) {
			if (turnRightPlayer1Player2.isPressed()) {
				playerSprites.get(1).setAngle(playerSprites.get(1).getAngle() + Math.toRadians(225));    		
			}
			if (turnLeftPlayer1Player2.isPressed()) {
				playerSprites.get(1).setAngle(playerSprites.get(1).getAngle() - Math.toRadians(225) + 360);
			}
			if(moveForwardPlayer1Player2.isPressed()) {
				playerSprites.get(1).setX(playerSprites.get(1).getX() + (float)(playerSpeed* Math.sin( Math.toRadians(playerSprites.get(1).getAngle())) ));
				playerSprites.get(1).setY(playerSprites.get(1).getY() - (float)(playerSpeed* Math.cos( Math.toRadians(playerSprites.get(1).getAngle())) ));
				
				movingShip = true;
				holding = true;
			}
			if(moveBackwardPlayer1Player2.isPressed()) {
				playerSprites.get(1).setX(playerSprites.get(1).getX() - (float)(playerSpeed* Math.sin( Math.toRadians(playerSprites.get(1).getAngle())) ));
				playerSprites.get(1).setY(playerSprites.get(1).getY() + (float)(playerSpeed* Math.cos( Math.toRadians(playerSprites.get(1).getAngle())) ));
				
				movingShip = true;
				holding = true;
			}
			if (fireBulletsPlayer2.isPressed()) {
				addBullets(1);
			}
		}
		if (holding != true)
		{
			movingShip = false;
		}
	}

	/**
        Creates GameActions and maps them to keys.
	 */
	public void createGameActions() {
		fireBulletsPlayer1 = new GameAction("fireBullets",
				GameAction.DETECT_INITAL_PRESS_ONLY);
		exit = new GameAction("exit",
				GameAction.DETECT_INITAL_PRESS_ONLY);
		turnLeftPlayer1 = new GameAction("turnLeftPlayer1");
		turnRightPlayer1 = new GameAction("turnRightPlayer1");
		moveForwardPlayer1 = new GameAction("moveForwardPlayer1");
		moveBackwardPlayer1 = new GameAction("moveBackwardPlayer1");
		pause = new GameAction("pause",GameAction.DETECT_INITAL_PRESS_ONLY);

		fireBulletsPlayer2 = new GameAction("fireBullets1",GameAction.DETECT_INITAL_PRESS_ONLY);
		turnLeftPlayer1Player2 = new GameAction("turnLeftPlayer1Player2");
		turnRightPlayer1Player2 = new GameAction("turnRightPlayer1Player2");
		moveForwardPlayer1Player2 = new GameAction("moveForwardPlayer1Player2");
		moveBackwardPlayer1Player2 = new GameAction("moveBackwardPlayer1Player2");
		pauseEnter = new GameAction("pauseEnter");

		//inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
		inputManager.mapToKey(pause, KeyEvent.VK_ESCAPE);
		inputManager.mapToKey(exit, KeyEvent.VK_E);
		// 	jump with spacebar
		inputManager.mapToKey(fireBulletsPlayer1, KeyEvent.VK_SHIFT);

		// move with the arrow keys... player 1
		inputManager.mapToKey(turnLeftPlayer1, KeyEvent.VK_LEFT);
		inputManager.mapToKey(turnRightPlayer1, KeyEvent.VK_RIGHT);
		inputManager.mapToKey(moveForwardPlayer1, KeyEvent.VK_UP);
		inputManager.mapToKey(moveBackwardPlayer1, KeyEvent.VK_DOWN);

		//// move with the arrow keys...
		inputManager.mapToKey(turnLeftPlayer1Player2, KeyEvent.VK_A);
		inputManager.mapToKey(turnRightPlayer1Player2, KeyEvent.VK_D);
		inputManager.mapToKey(moveForwardPlayer1Player2, KeyEvent.VK_W);
		inputManager.mapToKey(moveBackwardPlayer1Player2, KeyEvent.VK_S);

		inputManager.mapToKey(fireBulletsPlayer2, KeyEvent.VK_SPACE);

		
		inputManager.mapToKey(pauseEnter, KeyEvent.VK_ENTER);

	}

	private void addAlienBulletsToScreen() {
		if(System.currentTimeMillis() - timeSinceAlienBullet > (ALIEN_FIRE_INTERVAL/(LEVEL-1))) {
			timeSinceAlienBullet = System.currentTimeMillis();
			for(int i = 0; i < 4; i++) {
				addAlienBullet(i);
			}
		}

	}

	private void addAlienBullet(int i) {
		Animation anim = new Animation();
		anim.addFrame(bulletImage, 250);
		bulletSprites.add(new Bullet(anim, screen.getWidth(), 2));

		// set bullet starting location
		bulletSprites.get(bulletSprites.size()-1).setX(alienSprite.getCenterX());
		bulletSprites.get(bulletSprites.size()-1).setY(alienSprite.getCenterY() + i*i*bulletSprites.get(0).getHeight());

		// set velocity
		bulletSprites.get(bulletSprites.size()-1).setVelocityX(0.5f);
		bulletSprites.get(bulletSprites.size()-1).setVelocityY(0.5f);
		double number = 90+Math.random()*180;
		bulletSprites.get(bulletSprites.size()-1).setAngle(number);
	}

	/** 
	 * Creates bullets whenever spacebar is pressed
	 */
	public void addBullets(int playerIndex) {
		if(BULLET_COUNT > 3) {
			BULLET_COUNT = 0;
			return;
		}
		else {
			BULLET_COUNT++;
		}
		Animation anim = new Animation();
		anim.addFrame(bulletImage, 250);
		bulletSprites.add(new Bullet(anim, screen.getWidth(), playerIndex));

		float x = playerSprites.get(playerIndex).getX();
		float y = playerSprites.get(playerIndex).getY();
		double angle = playerSprites.get(playerIndex).getAngle();
		//x=10 y=10
		double bulletx = x+(10*Math.cos(angle)-10*Math.sin(angle));
		double bullety = y+(10*Math.sin(angle)+10*Math.cos(angle));
		bulletSprites.get(bulletSprites.size()-1).setX((float)bulletx);
		bulletSprites.get(bulletSprites.size()-1).setY((float)bullety);
		bulletSprites.get(bulletSprites.size()-1).setVelocityX(0.5f);
		bulletSprites.get(bulletSprites.size()-1).setVelocityY(0.5f);

		bulletSprites.get(bulletSprites.size()-1).setAngle(playerSprites.get(playerIndex).getAngle());
	//	System.out.println(playerSprites.get(playerIndex).getAngle());
	}

	public static void respawnSpaceship(int playerIndex) {
		playerSprites.get(playerIndex).setX(400);
		playerSprites.get(playerIndex).setY(400);
	}

	public static ScreenManager getScreen() {
		return screen;
	}

	public void updateScore(Graphics2D g) {
		Font font = new Font("Dialog", Font.PLAIN, 15);
		g.setFont(font);
		g.setColor(Color.green);

		g.drawString("Level : " + LEVEL, 50, 50);

		for(int i = 0; i < playerSprites.size(); i++) {
			g.drawString("Player " + (i+1) + " Score: " + playerSprites.get(i).getPlayerScore(), ((i+1)*200) + (i*400), 50);
			if (UNLIMITED_LIVES)
			{
				g.drawString("Player " + (i+1) + " Lives: " + "Unlimited", ((i+2)*200) + (i*400), 50);
			}
			else g.drawString("Player " + (i+1) + " Lives: " + playerSprites.get(i).getPlayerLife(), ((i+2)*200) + (i*400), 50);
		}

		g.setFont(null);

	}

	private static void createAlienShip() {
		Animation anim = new Animation();
		anim.addFrame(alienImage, 250);
		alienSprite = new Sprite(anim);
		alienSprite.setX(10);
		alienSprite.setY(ALIENSHIP_HEIGHT);
	}

	private static void createGravitationalObject() {
		Animation anim = new Animation();
		anim.addFrame(gravitationObjectImage, 250);
		gravitationalObjectSprite= new Sprite(anim);
		gravitationalObjectSprite.setX((screen.getWidth()/2) - gravitationalObjectSprite.getWidth()/2);
		gravitationalObjectSprite.setY((screen.getHeight()/2)- gravitationalObjectSprite.getHeight()/2);
	}

	private void createButtons()
	{
		for (int i = 0 ; i < buttonNames.length ; i++)
		{    	
			Rectangle temp = new Rectangle(screen.getWidth() / 2 - 75,
					200 + i * 50,
					180,
					20);
			buttonFrame.put(buttonNames[i],temp);
		}
	}

	public void checkGameInputPause() {
		if(moveForwardPlayer1.isPressed() && pauseMenuIndex > 0) {
			pauseMenuIndex--;
			moveForwardPlayer1.reset();
		//	System.out.println(pauseMenuIndex);
		}
		if(moveBackwardPlayer1.isPressed() &&  pauseMenuIndex < buttonNames.length - 1) {
			moveBackwardPlayer1.reset();
			pauseMenuIndex++;
		}
		if (turnLeftPlayer1.isPressed())
		{
			turnLeftPlayer1.reset();
			if (pauseMenuIndex == 6 && NUM_OF_ASTEROIDS > 3){
				NUM_OF_ASTEROIDS--;
				loadAsteroidImages();
			}
			if (pauseMenuIndex == 8 && LEVEL > 1) {
				asteroidSprites.clear(); 
				LEVEL--;
				NUM_OF_ASTEROIDS = 2*LEVEL + 1;
				loadAsteroidImages();}
			
		}
		if (turnRightPlayer1.isPressed())
		{
			turnRightPlayer1.reset();
			if (pauseMenuIndex == 6) {
				NUM_OF_ASTEROIDS++;
				loadAsteroidImages();
			}
			if (pauseMenuIndex == 8) {
				asteroidSprites.clear(); 
				LEVEL++;
				NUM_OF_ASTEROIDS = 2*LEVEL + 1;
				loadAsteroidImages();}
		}
		if(pauseEnter.isPressed())
		{	  
			pauseEnter.reset();

			switch (pauseMenuIndex)
			{
			case 0:
				pause.press();
				break;
			case 1:
				SAVE_GAME = true;
				break;
			case 2:
				LOAD_GAME = true;
				break;
			case 3:
				GRAV_OBJECT = !GRAV_OBJECT;
				break;
			case 4:
				GRAV_OBJECT_VISIBLE = !GRAV_OBJECT_VISIBLE;
				break;
			case 5:
				UNLIMITED_LIVES = !UNLIMITED_LIVES;
				if (UNLIMITED_LIVES) 
				{
					for (int i = 0 ; i < playerSprites.size() ; i++) playerSprites.get(i).setPlayerLife(100000);
				}
				else
				{
					for (int i = 0 ; i < playerSprites.size() ; i++) playerSprites.get(i).setPlayerLife(3);
				}
				break;
			case 6:
				break;
			case 7:
				break;
			case 8:
				break;
			case 9:
				MULTIPLAYER_ACTIVE = !MULTIPLAYER_ACTIVE;
				if (!MULTIPLAYER_ACTIVE) playerSprites.remove(1);
				break;
			case 10:
				exit.press();
				break;
			}
		}

	}
	    
	public void saveGame(){
        BufferedWriter out = null;
        MyInternalFrame myFrame = new MyInternalFrame();
        Icon icon = null;
        myFrame.toFront();
        myFrame.setVisible(true);;
           try {
        myFrame.setSelected(true);
    } catch (java.beans.PropertyVetoException e) {}
        
        
        String s = (String)JOptionPane.showInputDialog(myFrame,"Name save file:\n","Save File",JOptionPane.PLAIN_MESSAGE,icon,null,"");
        s = s+".sav";
        try {
            out = new BufferedWriter(new FileWriter(s));
            System.out.println(s);
            for(int i = 0; i < playerSprites.size(); i++){
                    Player p = playerSprites.get(i);
                    out.write("Player"+":"+p.getPlayerLife()+":"+p.getPlayerScore()+":"+p.getX()+":"+p.getY()+":"+p.getAngle()+":"+p.getVelocityX()+":"+p.getVelocityY()+"\n");
            }
            for(int i = 0; i < asteroidSprites.size(); i++){
                    Sprite a = asteroidSprites.get(i);
                    out.write("Asteroid"+":"+a.getAsteroidType()+":"+a.getX()+":"+a.getY()+":"+a.getAngle()+":"+a.getVelocityX()+":"+a.getVelocityY()+"\n");
            }
           
           if(ALIEN_ACTIVE){
                   Sprite aa = alienSprite;
                   out.write("Alien"+":"+aa.getAlienLives()+":"+aa.getX()+":"+aa.getY()+":"+aa.getAngle()+":"+aa.getVelocityX()+":"+aa.getVelocityY()+"\n");
           }        
                out.write("NUM_OF_ASTEROIDS"+":"+NUM_OF_ASTEROIDS+"\n");
                out.write("LEVEL"+":"+LEVEL+"\n");
                out.write("playerSpeed"+":"+playerSpeed+"\n");
                out.write("ALIEN_LEVEL_SPAWN"+":"+ALIEN_LEVEL_SPAWN+"\n");
                out.write("ALIEN_ACTIVE"+":"+ALIEN_ACTIVE+"\n");
                out.write("ALIEN_SPEED"+":"+ALIEN_SPEED+"\n");
                out.write("movingShip"+":"+movingShip+"\n");
                
                out.write("GRAV_OBJECT"+":"+GRAV_OBJECT+"\n");
                out.write("GRAV_OBJECT_VISIBLE"+":"+GRAV_OBJECT_VISIBLE+"\n");
                
                out.write("GRAVITATIONAL_OBJECT_STRENGTH"+":"+GRAVITATIONAL_OBJECT_STRENGTH+"\n");
                out.write("BASE_ASTEROID_SPEED"+":"+BASE_ASTEROID_SPEED+"\n");

                out.write("MULTIPLAYER_ACTIVE"+":"+MULTIPLAYER_ACTIVE+"\n");

                out.close();
        } catch (IOException e) {}
        
}

public void loadGame(){
        MyInternalFrame myFrame = new MyInternalFrame();
        Icon icon = null;
        myFrame.toFront();
        myFrame.setVisible(true);;
           try {
        myFrame.setSelected(true);
    } catch (java.beans.PropertyVetoException e) {}
        
 // Directory path here
    String path = "."; 
   
    String files;
    File folder = new File(path);
    File[] listOfFiles = folder.listFiles(); 
    ArrayList<Object> textFiles = new ArrayList<Object>();
    for (int i = 0; i < listOfFiles.length; i++) 
    {
   
     if (listOfFiles[i].isFile()) 
     {
     files = listOfFiles[i].getName();
         if ((files.endsWith(".sav") || files.endsWith(".SAV"))&&!files.startsWith("null"))
         {
            textFiles.add(files);                      
    }
       }
    }
    if(textFiles.toArray().length>0){
        String s = (String)JOptionPane.showInputDialog(myFrame,"Choose file to Load:\n","Load File",JOptionPane.PLAIN_MESSAGE,icon,textFiles.toArray(),"");
        playerSprites.clear();
        asteroidSprites.clear();
        try{
                File file = new File(s);
                FileReader reader = new FileReader(file);
                BufferedReader br = new BufferedReader(reader);
                //StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            String [] x = line.split(":");
            if(x[0].equals("Player")){
                    Animation anim = new Animation();
                    anim.addFrame(shipImage, 250);
                    Player temp = new Player(anim);
                    temp.setPlayerLife(Integer.parseInt(x[1]));
                    temp.setPlayerScore(Integer.parseInt(x[2]));
                    temp.setX(Float.parseFloat(x[3]));
                    temp.setY(Float.parseFloat(x[4]));
                    temp.setAngle(Float.parseFloat(x[5]));
                    temp.setVelocityX(Float.parseFloat(x[6]));
                    temp.setVelocityY(Float.parseFloat(x[7]));
                    playerSprites.add(temp);
            }
            else if(x[0].equals("Asteroid")){
                    Animation anim = new Animation();
                    if(x[1].equals("large")){
                            anim.addFrame(largeAsteroidImage, 250);
                    }else{
                            anim.addFrame(smallAsteroidImage, 250);
                    }
                    Sprite temp = new Sprite(anim);
                    temp.setAsteroidType(x[1]);
                    temp.setX(Float.parseFloat(x[2]));
                    temp.setY(Float.parseFloat(x[3]));
                    temp.setAngle(Float.parseFloat(x[4]));
                    temp.setVelocityX(Float.parseFloat(x[5]));
                    temp.setVelocityY(Float.parseFloat(x[6]));
                    asteroidSprites.add(temp);
            }
            else if(x[0].equals("Alien")){
                    Animation anim = new Animation();
                    anim.addFrame(alienImage, 250);
                    Sprite temp = new Sprite(anim);
                    temp.setAlienLives(Integer.parseInt(x[1]));
                    temp.setX(Float.parseFloat(x[2]));
                    temp.setY(Float.parseFloat(x[3]));
                    temp.setAngle(Float.parseFloat(x[4]));
                    temp.setVelocityX(Float.parseFloat(x[5]));
                    temp.setVelocityY(Float.parseFloat(x[6]));
                    alienSprite = temp;
            }
            else if(x[0].equals("NUM_OF_ASTEROIDS")){
                    NUM_OF_ASTEROIDS = Integer.parseInt(x[1]);
            }
            else if(x[0].equals("LEVEL")){
                   LEVEL = Integer.parseInt(x[1]);
            }
            
            else if(x[0].equals("ALIEN_ACTIVE")){
                    ALIEN_ACTIVE = Boolean.parseBoolean(x[1]);
            }
            else if(x[0].equals("ALIEN_SPEED")){
                    ALIEN_SPEED = Integer.parseInt(x[1]);
            }
            else if(x[0].equals("movingShip")){
                    movingShip = Boolean.parseBoolean(x[1]);
            }
            else if(x[0].equals("GRAV_OBJECT")){
                    GRAV_OBJECT = Boolean.parseBoolean(x[1]);
            }
            else if(x[0].equals("GRAV_OBJECT_VISIBLE")){
            	GRAV_OBJECT_VISIBLE = Boolean.parseBoolean(x[1]);
            }
            else if(x[0].equals("GRAVITATIONAL_OBJECT_STRENGTH")){
                    GRAVITATIONAL_OBJECT_STRENGTH = Integer.parseInt(x[1]);
            }
            
            else if(x[0].equals("MULTIPLAYER_ACTIVE")){
                    MULTIPLAYER_ACTIVE = Boolean.parseBoolean(x[1]);
            }
                         line = br.readLine();
        }
       
        
        br.close();
        reader.close();
        
    
    }
    
    catch(IOException e){}
        
    }
    else{
   	 String s = (String)JOptionPane.showInputDialog(myFrame,"No Saved Files to Load\n","Load File",JOptionPane.PLAIN_MESSAGE,icon,null,"");
   }
  }


public void highScore(){

	MyInternalFrame myFrame = new MyInternalFrame();
	Icon icon = null;
	myFrame.toFront();
	myFrame.setVisible(true);
	int line_num = 0;
	boolean highscore = false;
	String[] score_curr = null;
	String[] score_next = null;
	try {
		myFrame.setSelected(true);
	} catch (java.beans.PropertyVetoException e) {}
	try{
		File file = new File("highscore.txt");
		FileReader reader = new FileReader(file);
		BufferedReader br = new BufferedReader(reader);
		ArrayList<String> file_string = new ArrayList<String>();
		String line = br.readLine();
		while (line != null) {
			file_string.add(line);
		}

		for(int i = 9; i>=0; i--){
			score_curr = ((String) file_string.toArray()[i]).split(" ");
			
			int score_num = Integer.parseInt(score_curr[2]);
			if(i > 0){
				score_next = ((String) file_string.toArray()[i-1]).split(" ");
				int score_next_num = Integer.parseInt(score_next[2]);
				if(playerSprites.get(0).getPlayerScore() > score_num && playerSprites.get(0).getPlayerScore() < score_next_num){
					String s = (String)JOptionPane.showInputDialog(myFrame,"Congratulations! You have a high score!\n Please enter your 3 digit character tag\n","Congratulations",JOptionPane.PLAIN_MESSAGE,icon,null,"");
					s = s.substring(0,3);
					line_num = i;
					highscore = true;
					
				}
			}
			else if(i == 0){
				if(playerSprites.get(0).getPlayerScore() > score_num){
					String s = (String)JOptionPane.showInputDialog(myFrame,"Congratulations! You have a high score!\n Please enter your 3 digit character tag\n","Congratulations",JOptionPane.PLAIN_MESSAGE,icon,null,"");
					s = s.substring(0,3);
					line_num = i;
					highscore = true;
				}	
			}
		}
		if(highscore){
		for(int i = 9; i>line_num; i--){
			file_string.toArray()[i] = file_string.toArray()[i-1];
		}
		file_string.toArray()[line_num] = line_num+"s"+playerSprites.get(0).getPlayerScore();
		}
		for(int i = 0; i>file_string.toArray().length;i++){
			System.out.println(file_string.toArray()[i]);
		}
		br.close();
	}
	catch(IOException e){}
}
public static void highScorePls(int p1Score, int p2Score){
	MyInternalFrame myFrame = new MyInternalFrame();
	Icon icon = null;
	myFrame.toFront();
	myFrame.setVisible(true);
	try {
		myFrame.setSelected(true);
	} catch (java.beans.PropertyVetoException e) {}
	
	int line_num = 0;
	boolean highscore = false;
	boolean highscore2 = false;
	String[] score_curr = null;
	String[] score_next = null;
	String s = null;
	try{
		StringBuilder sb = new StringBuilder();
		
		File file = new File("highscore.txt");
		FileReader reader = new FileReader(file);
		BufferedReader br = new BufferedReader(reader);
		ArrayList<String> file_string = new ArrayList<String>();
		String line = br.readLine();
		while (line != null) {
			file_string.add(line);
			line = br.readLine();
		}
		
		for(int i = 9; i>=0; i--){
			score_curr = ((String) file_string.toArray()[i]).split(" ");
			
			int score_num = Integer.parseInt(score_curr[2]);
			if(i > 0){
				score_next = ((String) file_string.toArray()[i-1]).split(" ");
				int score_next_num = Integer.parseInt(score_next[2]);
				if(p1Score > score_num && p1Score < score_next_num){
					s = (String)JOptionPane.showInputDialog(myFrame,"Congratulations! You have a high score!\n Please enter your 3 digit character tag\n","Congratulations",JOptionPane.PLAIN_MESSAGE,icon,null,"");
					s = s.substring(0,3);
					line_num = i;
					highscore = true;
				}
				else if(p2Score > score_num && p2Score < score_next_num){
					s = (String)JOptionPane.showInputDialog(myFrame,"Congratulations! You have a high score!\n Please enter your 3 digit character tag\n","Congratulations",JOptionPane.PLAIN_MESSAGE,icon,null,"");
					s = s.substring(0,3);
					line_num = i;
					highscore2 = true;
				}
				
			
			}
			else if(i == 0){
				System.out.println(i);
				//int score_num = Integer.parseInt(score_curr[2]);
				if(p1Score > score_num){
					s = (String)JOptionPane.showInputDialog(myFrame,"Congratulations! You have a high score!\n Please enter your 3 digit character tag\n","Congratulations",JOptionPane.PLAIN_MESSAGE,icon,null,"");
					s = s.substring(0,3);
					line_num = i;
					highscore = true;
				}	
				else if(p2Score > score_num){
					s = (String)JOptionPane.showInputDialog(myFrame,"Congratulations! You have a high score!\n Please enter your 3 digit character tag\n","Congratulations",JOptionPane.PLAIN_MESSAGE,icon,null,"");
					s = s.substring(0,3);
					line_num = i;
					highscore2 = true;
				}
				System.out.println(i);
			}
		}
		System.out.println("here I am");
		if(highscore){
		for(int i = 9; i>line_num; i--){
			file_string.toArray()[i] = file_string.toArray()[i-1];
		}
		file_string.toArray()[line_num] = line_num+ s +p1Score;
		}
		else if(highscore2){
			for(int i = 9; i>line_num; i--){
				file_string.toArray()[i] = file_string.toArray()[i-1];
			}
			file_string.toArray()[line_num] = line_num+"s"+p2Score;
		}
		System.out.println(file_string.toArray()[line_num]);
		//for(int i = 0; i<file_string.toArray().length;i++){
			//sb.append(file_string.toArray()[i]);
			//System.out.println("hello");
			//System.out.println(file_string.toArray()[i]);
		//}
		//System.out.print(sb.toString());
		
		br.close();
	}
	catch(IOException e){}
}
}


