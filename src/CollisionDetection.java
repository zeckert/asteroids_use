import java.awt.Graphics2D;
import java.util.LinkedList;

import javax.swing.JOptionPane;


public class CollisionDetection extends Thread{
	public CollisionDetection() {
		
	}
	
	private void playerDied()
	{
	}
	
	private void highScore()
	{
		playerDied();
		int p1Score = Asteroids.playerSprites.get(0).getPlayerScore();

		if (Asteroids.MULTIPLAYER_ACTIVE)
		{	
			int p2Score = Asteroids.playerSprites.get(1).getPlayerScore();
		}
	}
	
	public void run() {
		while(!Asteroids.exitGame) {
			
			try{

		/*		for(int i = 0; i < Asteroids.asteroidSprites.size(); i++) {
					for(int j = 0; j < Asteroids.playerSprites.size(); j++) {
						for(int k = 0; i < Asteroids.bulletSprites.size(); k++){
							if(isPlayerWithinAsteroidsRange(Asteroids.asteroidSprites.get(i), Asteroids.playerSprites.get(j))){ //player colliding with ast
								if(Asteroids.playerSprites.get(j).getPlayerLife() > 0){
									int lifes = Asteroids.playerSprites.get(j).getPlayerLife();
									Asteroids.playerSprites.get(j).setPlayerLife(lifes-1);
								    playerDied();
									Asteroids.respawnSpaceship(j);
								}
								else{
									gameEnd();
									break;
								}
							}
							if(bulletTouchingAsteroid(Asteroids.bulletSprites.get(k), Asteroids.asteroidSprites.get(i))) { //bullet with ast
								if(Asteroids.asteroidSprites.get(i).getAsteroidType().equals("large")) { // check if asteroid is a parent, in which case spawn three new asteroids before killing this one
									Asteroids.asteroidDestroyed(Asteroids.asteroidSprites.get(i), 3);							
								}
								int playerIndex = Asteroids.bulletSprites.get(k).getPlayerIndex();
								Asteroids.playerSprites.get(playerIndex).setPlayerScore(Asteroids.playerSprites.get(playerIndex).getPlayerScore() + 5);
								Asteroids.asteroidSprites.remove(i); // kill asteroid
								Asteroids.bulletSprites.remove(k); // kill bullet
								
		
								break;
							}// if collision exists
							
						}
					}
				}
			}*/
				// Check for Asteroid-Player Collision
				for(int i = 0; i < Asteroids.asteroidSprites.size(); i++) {
					for(int j = 0; j < Asteroids.playerSprites.size(); j++) {
						if(isPlayerWithinAsteroidsRange(Asteroids.asteroidSprites.get(i), Asteroids.playerSprites.get(j))) {
								if(Asteroids.playerSprites.get(j).getPlayerLife() > 0) {  // decrement player life if there's lives remaining and there's a collision
									Asteroids.playerSprites.get(j).setPlayerLife(Asteroids.playerSprites.get(j).getPlayerLife() - 1);
									
									playerDied();
									Asteroids.respawnSpaceship(j);
									
								}	
								else {
									
	
									Asteroids.inputManager.resetAllGameActions();
									Asteroids.asteroidSprites.clear();
									highScore();	
									Asteroids.initilization();
									
									break;
									
								}				
						}
					}
				}
				
				// Check for Bullet-Asteroid Collision
				for(int j = 0; j < Asteroids.asteroidSprites.size(); j++) {
					for(int i = 0; i < Asteroids.bulletSprites.size(); i++) {
						if(bulletTouchingAsteroid(Asteroids.bulletSprites.get(i), Asteroids.asteroidSprites.get(j))) { // if collision exists
							if(Asteroids.asteroidSprites.get(j).getAsteroidType().equals("large")) { // check if asteroid is a parent, in which case spawn three new asteroids before killing this one
								Asteroids.asteroidDestroyed(Asteroids.asteroidSprites.get(j), 3);							
							}
							int playerIndex = Asteroids.bulletSprites.get(i).getPlayerIndex();
							Asteroids.playerSprites.get(playerIndex).setPlayerScore(Asteroids.playerSprites.get(playerIndex).getPlayerScore() + 5);
							Asteroids.asteroidSprites.remove(j); // kill asteroid
							Asteroids.bulletSprites.remove(i); // kill bullet
							
	
							break;
						}
					}
				}
				// Check for player bullets colliding with alienship
				if(Asteroids.ALIEN_ACTIVE) {
					for(int i = 0; i < Asteroids.bulletSprites.size(); i++) {
						if(bulletTouchingAlienship(Asteroids.bulletSprites.get(i), Asteroids.alienSprite)) { // if collision exists
							if(Asteroids.alienSprite.getAlienLives() > 0) {
								//	System.out.println("alien hit");
								Asteroids.alienSprite.setAlienLives(Asteroids.alienSprite.getAlienLives() - 1);
								int playerIndex = Asteroids.bulletSprites.get(i).getPlayerIndex();
								Asteroids.playerSprites.get(playerIndex).setPlayerScore(Asteroids.playerSprites.get(playerIndex).getPlayerScore() + 100);
							}
							else {
								Asteroids.ALIEN_ACTIVE = false;
								Asteroids.alienSprite.setAlienLives(3);							
							}
							Asteroids.bulletSprites.remove(i); 
						}
					}
				}
				// Check for player and alien bullet collision
				for(int j = 0; j < Asteroids.playerSprites.size(); j++) {
					for(int i = 0; i < Asteroids.bulletSprites.size(); i++) {
						if(alienBulletTouchingPlayer(Asteroids.bulletSprites.get(i), Asteroids.playerSprites.get(j))) { // if collision exists
							if(Asteroids.playerSprites.get(j).getPlayerLife() > 0) {  // decrement player life if there's lives remaining and there's a collision
								Asteroids.playerSprites.get(j).setPlayerLife(Asteroids.playerSprites.get(j).getPlayerLife() - 1);
								playerDied();
								Asteroids.respawnSpaceship(j);
							}	
							else { // player has no more lives and there's a collision, so end game!
	
								Asteroids.inputManager.resetAllGameActions();
								Asteroids.asteroidSprites.clear();
								highScore();	
								Asteroids.initilization();
								break;
							}
						}
					}
				}
				// check for player-alienship collision
				if(Asteroids.ALIEN_ACTIVE) {
					for(int j = 0; j < Asteroids.playerSprites.size(); j++) {
						if(isPlayerTouchingAlien(Asteroids.playerSprites.get(j), Asteroids.alienSprite)) {
								if(Asteroids.playerSprites.get(j).getPlayerLife() > 0) {  // decrement player life if there's lives remaining and there's a collision
									Asteroids.playerSprites.get(j).setPlayerLife(Asteroids.playerSprites.get(j).getPlayerLife() - 1);
									playerDied();
									Asteroids.respawnSpaceship(j);
								}	
								else { // player has no more lives and there's a collision, so end game!
									Asteroids.inputManager.resetAllGameActions();
									Asteroids.asteroidSprites.clear();
									highScore();	
									Asteroids.initilization();
									break;
								}						
						}
					}
				}
				checkBulletDistanceTravelled(); // To delete bullets that have travelled more than the screen.width()
				
				System.out.print(""); // for some reason all the above code only works if this print statement is in here, WTF?
			}
			catch (Exception e){ }
		}
	}
	
	private boolean isPlayerTouchingAlien(Sprite p, Sprite alien) {
		double rangeOfAlien = Math.sqrt(Math.pow(alien.getHeight()/2, 2) + Math.pow(alien.getWidth()/2, 2));
		double distance = Math.sqrt(Math.pow(alien.getCenterX() - p.getCenterX(), 2) + Math.pow(alien.getCenterY() - p.getCenterY(), 2));
		if(distance < rangeOfAlien) {
			return true;
		}
		return false;
	}
	
	private boolean alienBulletTouchingPlayer(Bullet b, Sprite s) {
		if(b.getPlayerIndex() != 2) {
			return false;
		}
		double rangeOfPlayership = Math.sqrt(Math.pow(s.getHeight()/2, 2) + Math.pow(s.getWidth()/2, 2));
		double distance = Math.sqrt(Math.pow(s.getCenterX() - b.getCenterX(), 2) + Math.pow(s.getCenterY() - b.getCenterY(), 2));
		if(distance < rangeOfPlayership) {
			return true;
		}
		return false;
	}
	
	private boolean bulletTouchingAlienship(Bullet b, Sprite s) {
		if(b.getPlayerIndex() == 2) {
			return false;
		}
		double rangeOfAlienship = Math.sqrt(Math.pow(s.getHeight()/2, 2) + Math.pow(s.getWidth()/2, 2));
		double distance = Math.sqrt(Math.pow(s.getCenterX() - b.getCenterX(), 2) + Math.pow(s.getCenterY() - b.getCenterY(), 2));
		if(distance < rangeOfAlienship) {
			return true;
		}
		return false;
	}
	
	private void checkBulletDistanceTravelled() {
		for(int i = 0; i < Asteroids.bulletSprites.size(); i++) {	
			Bullet b = Asteroids.bulletSprites.get(i);
			if(b.getBulletDistanceTravelled() > Asteroids.getScreen().getWidth()) {
				Asteroids.bulletSprites.remove(i);
			}
		}
	}
	
	private boolean bulletTouchingAsteroid(Bullet b, Sprite s) {
		if(b.getPlayerIndex() == 2) {
			return false;
		}
		double rangeOfAsteroid = Math.sqrt(Math.pow(s.getHeight()/2, 2) + Math.pow(s.getWidth()/2, 2));
		double distance = Math.sqrt(Math.pow(s.getCenterX() - b.getCenterX(), 2) + Math.pow(s.getCenterY() - b.getCenterY(), 2));
		if(distance < rangeOfAsteroid) {
			return true;
		}
		return false;
	}
	
	private boolean isPlayerWithinAsteroidsRange(Sprite s, Player p) {
		double rangeOfAsteroid = Math.sqrt(Math.pow(s.getHeight()/2, 2) + Math.pow(s.getWidth()/2, 2));
		double distance = Math.sqrt(Math.pow(s.getCenterX() - p.getCenterX(), 2) + Math.pow(s.getCenterY() - p.getCenterY(), 2));
		if(distance < rangeOfAsteroid) {
			return true;
		}
		return false;
	}
}
