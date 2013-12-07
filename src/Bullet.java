
public class Bullet extends Sprite{
	// bullet distance travelled
	private static int screenWidth;
	private int bulletDistanceTravelled;
	
	private int playerIndex; // = 0 if bullet came from the first playerShip and = 1 if it came from the second and = 2 if it came from Alienship
	/*
	 * Bullet constructor
	 * @params anim - animation object, width - screen width since bullet only travels for "one screen width"
	 */
	public Bullet(Animation anim, int width, int index) {
		super(anim);
		screenWidth =  width;
		bulletDistanceTravelled = 0;
		playerIndex = index;
	}
	
	public void updateBulletDistance(int newDistance) {
		bulletDistanceTravelled += newDistance;
	}
	
	public int getBulletDistanceTravelled() {
		return bulletDistanceTravelled;
	}
	
	public int getPlayerIndex() {
		return playerIndex;
	}
}
