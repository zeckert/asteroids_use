import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.LinkedList;
import java.awt.Image.*;

import javax.imageio.ImageIO;
import javax.sound.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;



public class Asteroids {

   
	
	public static void main(String args[]) {
		Asteroids asteroidsGame = new Asteroids();
		asteroidsGame.run();
	}

	public static final String HIGHSCORE_FILENAME = "hs.txt";
	
	private static int NUM_ASTEROIDS = 3;
	private static int CURR_LEVEL = 1;
	private static final int playerSpeed = 10;
	private static final int ALIENSHIP_SPAWN_INTERVAL = 3; 
	public static boolean ALIENSHIP_ALIVE = false;
	private static int ALIENSHIP_SPEED = 2;


	public static boolean GRAVITATIONAL_OBJECT_ACTIVE = false;
	public static boolean GRAVITATIONAL_OBJECT_VISIBLE = false;
	public static int GRAVITATIONAL_OBJECT_STRENGTH = 4;
	public static final float BASE_ASTEROID_SPEED = 0.05f;

	public static boolean MULTIPLAYER_ACTIVE = false;

	public static boolean SAVE_GAME = false;
	public static boolean LOAD_GAME = false;
	public static String filename = "saved.ast";

	private static ScreenManager screen;
	//private Image bgImage;

	public static boolean UNLIMITED_LIVES = false;
	static Image asteroidImage = loadImage("images/asteroids3.png");
	static Image childAsteroidImage = loadImage("images/asteroids4.png");
	static Image playerImage = loadImage("images/greenShip.png");
	static Image gravitationObjectImage = loadImage("images/Blackhole1.png");

	// contains references to all the asteroid and player objects
	public static LinkedList<Sprite> asteroidSprites = new LinkedList<Sprite>(); 
	public static LinkedList<Player> playerSprites = new LinkedList<Player>();
	public static Sprite alienSprite;
	public static Sprite gravitationalObjectSprite;

	static int[] directionList = new int[]{1,-1};

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
	
	
	private boolean isShipMoving = false;
	
	public static void screenshot(Graphics2D g){
		try{
		Robot robot = new Robot();
		// The hard part is knowing WHERE to capture the screen shot from
		GraphicsDevice currentDevice = MouseInfo.getPointerInfo().getDevice();
		robot.createScreenCapture(currentDevice.getDefaultConfiguration().getBounds());
		BufferedImage exportImage = robot.createScreenCapture(currentDevice.getDefaultConfiguration().getBounds());
	//	imageGraphics = (Graphics2D) exportImage.getGraphics();
		// Add a label to the screen shot

		// Save your screen shot with its label
			ImageIO.write(exportImage, "png", new File("myScreenShot.png"));
		}
		catch (Exception exp) {
			exp.printStackTrace();
		}
		finally{
	//		imageGraphics.dispose();
		}
	}
	
	
	public static void loadPlayerImage() {

		playerSprites.clear();
		
		Animation anim = new Animation();
		anim.addFrame(playerImage, 250);
		playerSprites.add(new Player(anim, 3));
		playerSprites.get(0).setX(400);
		playerSprites.get(0).setY(400);
	}

	public static void loadAsteroidImages() {

		asteroidSprites.clear();
		
		// create and init sprites
		for (int i = 0; i < NUM_ASTEROIDS; i++) {
			Animation anim = new Animation();
			anim.addFrame(asteroidImage, 250);
			asteroidSprites.add(new Sprite(anim, "parent"));

			// select random starting location
			asteroidSprites.get(i).setX((float)Math.random() *
					(screen.getWidth() - asteroidSprites.get(i).getWidth()));
			asteroidSprites.get(i).setY((float)Math.random() * 
					(screen.getHeight() - asteroidSprites.get(i).getHeight()));

			// select random velocity
			int index = new Random().nextInt(directionList.length);


			Random random = new Random();

			asteroidSprites.get(i).setVelocityX(directionList[index]*(BASE_ASTEROID_SPEED + (CURR_LEVEL*0.01f)) *
					(float)( (random.nextBoolean() ? 1 : -1) * Math.sin(Math.toRadians(Math.random() * 1000 % 360))));
			asteroidSprites.get(i).setVelocityY(directionList[index]*(BASE_ASTEROID_SPEED + (CURR_LEVEL*0.01f)) *
					(float)( (random.nextBoolean() ? 1 : -1) * Math.cos(Math.toRadians(Math.random() * 1000 % 360))));

		}

	}

	public static void addAsteroidsInPlaceOfKilledAsteroid(Sprite s, int numOfAsteroids) {
		for(int i = 0; i < numOfAsteroids; i++) {
			Animation anim = new Animation();
			anim.addFrame(childAsteroidImage, 250);
			Sprite newAsteroid = new Sprite(anim, "child");

			newAsteroid.setX(s.getX());
			newAsteroid.setY(s.getY());


			Random random = new Random();

			// select random velocity
			int index = new Random().nextInt(directionList.length);
			newAsteroid.setVelocityX(directionList[index]*(i+1)*(BASE_ASTEROID_SPEED + (CURR_LEVEL*0.01f))*
					(float)( (random.nextBoolean() ? 1 : -1) * Math.sin(Math.toRadians(Math.random() * 1000 % 360))));
			newAsteroid.setVelocityY(directionList[index]*(i+1)*(BASE_ASTEROID_SPEED + (CURR_LEVEL*0.01f))*
					(float)( (random.nextBoolean() ? 1 : -1) * Math.sin(Math.toRadians(Math.random() * 1000 % 360))));

			asteroidSprites.add(newAsteroid);
		}
	}

	private static Image loadImage(String fileName) {
		return new ImageIcon(fileName).getImage();
	}

	
	
	public static void initilization()
	{
		ALIENSHIP_ALIVE = false; 	
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
			createGameActions();
			createGravitationalObject();
			gameLoop();
		}
		finally {
			screen.restoreScreen();
		}
	}


	public void gameLoop() {
		long startTime = System.currentTimeMillis();
		long currTime = startTime;

		while (!exitGame) { 
			if(asteroidSprites.size() == 0) {
				for(int i = 0; i < playerSprites.size(); i++) {
					Asteroids.playerSprites.get(i).setPlayerScore(Asteroids.playerSprites.get(i).getPlayerScore() + CURR_LEVEL*100);
				}
				CURR_LEVEL++;
				NUM_ASTEROIDS = 2*CURR_LEVEL + 1;
				if(CURR_LEVEL % ALIENSHIP_SPAWN_INTERVAL == 0) {
					ALIENSHIP_ALIVE = true;
				}
				loadAsteroidImages();
			}
			if(MULTIPLAYER_ACTIVE && playerSprites.size() == 1) {
				Animation anim = new Animation();
				anim.addFrame(playerImage, 250);
				playerSprites.add(new Player(anim, 3));

				
				playerSprites.get(1).setX(500);
				playerSprites.get(1).setY(500);


			}
			if(SAVE_GAME) {
	        		SAVE_GAME = false;
	        	}
			if(LOAD_GAME) {
				LOAD_GAME = false;
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

			updateScoreAndLivesOnScreen(g);
			draw(g);
			if (isPaused())
			{
				checkGameInputPause();
			}
			g.dispose();
			screen.update();


			try {
				Thread.sleep(20);
			}
			catch (InterruptedException ex) { }
		}

	}
	
	private static void createGravitationalObject() {
		Animation anim = new Animation();
		anim.addFrame(gravitationObjectImage, 250);
		gravitationalObjectSprite= new Sprite(anim);
		gravitationalObjectSprite.setX((screen.getWidth()/2) - gravitationalObjectSprite.getWidth()/2);
		gravitationalObjectSprite.setY((screen.getHeight()/2)- gravitationalObjectSprite.getHeight()/2);
	}


	public void update(long elapsedTime) {
		checkSystemInput();

		if (!isPaused()) {
			pauseMenuIndex = 0;
			checkGameInput();

			for (int i = 0; i < asteroidSprites.size(); i++) {

				Sprite s = asteroidSprites.get(i);

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

			// if alienship alive then move it!
			if(ALIENSHIP_ALIVE) {
				if(alienSprite.getX() > screen.getWidth()) {
					alienSprite.setX(0);
				}
				else {
					alienSprite.setX(alienSprite.getX() + (CURR_LEVEL/ALIENSHIP_SPAWN_INTERVAL)*ALIENSHIP_SPEED);
				}

			}

			if(GRAVITATIONAL_OBJECT_ACTIVE) {
				for(int i = 0; i < playerSprites.size(); i++) {
					if(playerSprites.get(i).getX() < gravitationalObjectSprite.getX()+100) {
						playerSprites.get(i).setX(playerSprites.get(i).getX() + GRAVITATIONAL_OBJECT_STRENGTH);
					}
					else {
						playerSprites.get(i).setX(playerSprites.get(i).getX() - GRAVITATIONAL_OBJECT_STRENGTH);
					}
					if(playerSprites.get(i).getY()+100 < gravitationalObjectSprite.getY()+100) {
						playerSprites.get(i).setY(playerSprites.get(i).getY() + GRAVITATIONAL_OBJECT_STRENGTH);
					}
					else {
						playerSprites.get(i).setY(playerSprites.get(i).getY() - GRAVITATIONAL_OBJECT_STRENGTH);
						//playerSprites.get(i).setY(playerSprites.get(i).getY() + (float)(GRAVITATIONAL_OBJECT_STRENGTH* Math.cos( Math.toRadians(playerSprites.get(0).getAngle())) ));
					}	
				}
			}
		}
	}


	public void draw(Graphics2D g) {
		AffineTransform transform = new AffineTransform();

		if(GRAVITATIONAL_OBJECT_ACTIVE && GRAVITATIONAL_OBJECT_VISIBLE) {
			transform.setToTranslation(gravitationalObjectSprite.getX(), gravitationalObjectSprite.getY());
			if (gravitationalObjectSprite.getVelocityX() < 0) {
				transform.scale(-1, 1);
				transform.translate(-gravitationalObjectSprite.getWidth(), 0);
			}
			g.drawImage(gravitationalObjectSprite.getImage(), transform, null);
		}

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

		if(ALIENSHIP_ALIVE) { // if alienShip spawn interval has been reached spawn it
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
					if (GRAVITATIONAL_OBJECT_ACTIVE){
						optionString += "on";
					}
					else{
						optionString += "off";
					}
				}
				else if (optionString == "Object Visible: "){
					if (GRAVITATIONAL_OBJECT_VISIBLE){
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
				else if (optionString == "# Of Astroids: ") optionString += NUM_ASTEROIDS;
				else if (optionString == "Starting Level: ") optionString += CURR_LEVEL;
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

	public boolean isPaused() {
		return paused;
	}


	public void setPaused(boolean p) {
		if (paused != p) {
			this.paused = p;
			inputManager.resetAllGameActions();
		}
	}

	public void checkSystemInput() {
		if (pause.isPressed()) {
			setPaused(!isPaused());
		}
		if(exit.isPressed()) {
			exitGame = true;
		}
	}


	public void checkGameInput() {
	//	float velocityX = 0;
		boolean holding = false;
		if (turnRightPlayer1.isPressed()) {
			playerSprites.get(0).setAngle(playerSprites.get(0).getAngle() + Math.toRadians(225));

		}
		if (turnLeftPlayer1.isPressed()) {
			playerSprites.get(0).setAngle(playerSprites.get(0).getAngle() - Math.toRadians(225) + 360);
		}
		if(moveForwardPlayer1.isPressed()) {
			playerSprites.get(0).setX(playerSprites.get(0).getX() + (float)(playerSpeed* Math.sin( Math.toRadians(playerSprites.get(0).getAngle())) ));
			playerSprites.get(0).setY(playerSprites.get(0).getY() - (float)(playerSpeed* Math.cos( Math.toRadians(playerSprites.get(0).getAngle())) ));
			
			isShipMoving = true;
			holding = true;
	//		if (!sounds.soundDic.get("thrust").isLooping) sounds.soundDic.get("thrust").loop();

		}
		if(moveBackwardPlayer1.isPressed()) {
			playerSprites.get(0).setX(playerSprites.get(0).getX() - (float)(playerSpeed* Math.sin( Math.toRadians(playerSprites.get(0).getAngle())) ));
			playerSprites.get(0).setY(playerSprites.get(0).getY() + (float)(playerSpeed* Math.cos( Math.toRadians(playerSprites.get(0).getAngle())) ));
			
			isShipMoving = true;
			holding = true;
	//		if (!sounds.soundDic.get("thrust").isLooping) sounds.soundDic.get("thrust").loop();
		}
		if (fireBulletsPlayer1.isPressed()) {
		//	addBulletsToScreen(0);
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
				
				isShipMoving = true;
				holding = true;
	//			if (!sounds.soundDic.get("thrust").isLooping) sounds.soundDic.get("thrust").loop();
			}
			if(moveBackwardPlayer1Player2.isPressed()) {
				playerSprites.get(1).setX(playerSprites.get(1).getX() - (float)(playerSpeed* Math.sin( Math.toRadians(playerSprites.get(1).getAngle())) ));
				playerSprites.get(1).setY(playerSprites.get(1).getY() + (float)(playerSpeed* Math.cos( Math.toRadians(playerSprites.get(1).getAngle())) ));
				
				isShipMoving = true;
				holding = true;
	//			if (!sounds.soundDic.get("thrust").isLooping) sounds.soundDic.get("thrust").loop();
			}
			if (fireBulletsPlayer2.isPressed()) {
	//			addBulletsToScreen(1);
			}
		}
		if (holding != true)
		{
			isShipMoving = false;
//			sounds.soundDic.get("thrust").stop();
		}
	}


	public void createGameActions() {
		fireBulletsPlayer1 = new GameAction("fireBullets",
				GameAction.DETECT_INITAL_PRESS_ONLY);
		exit = new GameAction("exit",
				GameAction.DETECT_INITAL_PRESS_ONLY);
		turnLeftPlayer1 = new GameAction("turnLeftPlayer1");
		turnRightPlayer1 = new GameAction("turnRightPlayer1");
		moveForwardPlayer1 = new GameAction("moveForwardPlayer1");
		moveBackwardPlayer1 = new GameAction("moveBackwardPlayer1");
		pause = new GameAction("pause",
				GameAction.DETECT_INITAL_PRESS_ONLY);

		fireBulletsPlayer2 = new GameAction("fireBullets1",
				GameAction.DETECT_INITAL_PRESS_ONLY);
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





	public static void respawnSpaceship(int playerIndex) {
		playerSprites.get(playerIndex).setX((float)Math.random() *
				(screen.getWidth() - playerSprites.get(playerIndex).getWidth()));
		playerSprites.get(playerIndex).setY((float)Math.random() *
				(screen.getHeight() - playerSprites.get(playerIndex).getHeight()));
	}

	public static ScreenManager getScreen() {
		return screen;
	}

	public void updateScoreAndLivesOnScreen(Graphics2D g) {
		Font font = new Font("Dialog", Font.PLAIN, 15);
		g.setFont(font);
		g.setColor(Color.green);

		g.drawString("Level : " + CURR_LEVEL, 50, 50);

		for(int i = 0; i < playerSprites.size(); i++) {
			g.drawString("Player " + (i+1) + " Score : " + playerSprites.get(i).getPlayerScore(), ((i+1)*200) + (i*400), 50);
			if (UNLIMITED_LIVES)
			{
				g.drawString("Player " + (i+1) + " Lives Remaining : " + "Unlimited", ((i+2)*200) + (i*400), 50);
			}
			else g.drawString("Player " + (i+1) + " Lives Remaining : " + playerSprites.get(i).getPlayerLife(), ((i+2)*200) + (i*400), 50);
		}

		g.setFont(null);

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
			System.out.println(pauseMenuIndex);
		}
		if (turnLeftPlayer1.isPressed())
		{
			turnLeftPlayer1.reset();
			if (pauseMenuIndex == 6 && NUM_ASTEROIDS > 3) NUM_ASTEROIDS--;
			if (pauseMenuIndex == 8 && CURR_LEVEL > 1) {
				asteroidSprites.clear(); 
				CURR_LEVEL--;
				NUM_ASTEROIDS = 2*CURR_LEVEL + 1;
				loadAsteroidImages();}
			
		}
		if (turnRightPlayer1.isPressed())
		{
			turnRightPlayer1.reset();
			if (pauseMenuIndex == 6) NUM_ASTEROIDS++;
			if (pauseMenuIndex == 8) {
				asteroidSprites.clear(); 
				CURR_LEVEL++;
				NUM_ASTEROIDS = 2*CURR_LEVEL + 1;
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
				GRAVITATIONAL_OBJECT_ACTIVE = !GRAVITATIONAL_OBJECT_ACTIVE;
				break;
			case 4:
				GRAVITATIONAL_OBJECT_VISIBLE = !GRAVITATIONAL_OBJECT_VISIBLE;
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
	    
}
