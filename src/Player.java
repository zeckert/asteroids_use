
public class Player extends Sprite{
	private int playerLives;
	private int playerScore;
	
	public Player(Animation anim) {
		super(anim);
		playerLives = 3;
		playerScore = 0;
	}
	
	public Player(Animation anim, int lives) {
		super(anim);
		playerLives = lives;
		playerScore = 0;
	}
	
	public void setPlayerLife(int life) {
		playerLives = life;
	}
	
	public int getPlayerLife() {
		return playerLives;
	}
	
	public int getPlayerScore() {
		return playerScore;
	}
	
	public void setPlayerScore(int score) {
		playerScore = score;
	}
}
