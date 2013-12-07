import java.awt.Image;

public class Sprite {

    private Animation anim;
    // position (pixels)
    private float x;
    private float y;
    // velocity (pixels per millisecond)
    private float dx;
    private float dy;
    //Angle
    private double angle;

    // Asteroid Type = "parent" if first time asteroid, = "child" if its one of three asteroids that spawned from a bullet hit asteroid
    private String asteroidType;
    
    // Alien ship hits taken so far
    private int alienLives;
    
    /**
        Creates a new Sprite object with the specified Animation.
    */
    public Sprite(Animation anim) {
        this.anim = anim;
        alienLives = 2;
    }

    public Sprite(Animation anim, String type) {
    	this.anim = anim;
    	asteroidType = type;
    	alienLives = 2;
    }
    public void setAsteroidType(String type){
    	asteroidType = type;
    }
    /**
        Updates this Sprite's Animation and its position based
        on the velocity.
    */
    public void update(long elapsedTime) {
        x += dx * elapsedTime;
        y += dy * elapsedTime;
        anim.update(elapsedTime);
    }

    /**
        Gets this Sprite's current x position.
    */
    public float getX() {
        return x;
    }

    /**
        Gets this Sprite's current y position.
    */
    public float getY() {
        return y;
    }

    /**
        Sets this Sprite's current x position.
    */
    public void setX(float x) {
        this.x = x;
    }

    /**
    	Gets this Sprite's angle
	*/
	public double getAngle() {
	    return angle;
	}

	/**
	    Sets this Sprite's angle.
	*/
	public void setAngle(double angleInDegree) {
	    this.angle = angleInDegree % 360;
	}
	

	
	/**
        Sets this Sprite's current y position.
    */
    public void setY(float y) {
        this.y = y;
    }

    /**
        Gets this Sprite's width, based on the size of the
        current image.
    */
    public int getWidth() {
        return anim.getImage().getWidth(null);
    }

    /**
        Gets this Sprite's height, based on the size of the
        current image.
    */
    public int getHeight() {
        return anim.getImage().getHeight(null);
    }

    /**
        Gets the horizontal velocity of this Sprite in pixels
        per millisecond.
    */
    public float getVelocityX() {
        return dx;
    }

    /**
        Gets the vertical velocity of this Sprite in pixels
        per millisecond.
    */
    public float getVelocityY() {
        return dy;
    }

    /**
        Sets the horizontal velocity of this Sprite in pixels
        per millisecond.
    */
    public void setVelocityX(float dx) {
        this.dx = dx;
    }

    /**
        Sets the vertical velocity of this Sprite in pixels
        per millisecond.
    */
    public void setVelocityY(float dy) {
        this.dy = dy;
    }

    /**
        Gets this Sprite's current image.
    */
    public Image getImage() {
        return anim.getImage();
    }
    
    /**
     * Return asteroidType, either "parent" or "child"
     * If "parent" then if asteroid is killed, spawn three "child" asteroid, else kill asteroid and end of story
     * @return
     */
    public String getAsteroidType() {
    	return asteroidType;
    }
    
    public int getAlienLives() {
    	return alienLives;
    }
    
    public void setAlienLives(int life) {
    	alienLives = life;
    }
    
    private double getRadius() {
    	return Math.sqrt(Math.pow(this.getWidth()/2, 2) + Math.pow(this.getHeight()/2, 2));
    }
    
    public int getCenterX() {
    	return (int)(x + (getRadius()*Math.cos(angle+45)));
    }
    
    public int getCenterY() {
    	return (int)(y + (getRadius()*Math.sin(angle+45)));
    }
}
