import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JOptionPane;

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
	    	s = s+".txt";
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
	    	   
	    	   if(ALIENSHIP_ALIVE){
	    		   Sprite aa = alienSprite;
	    		   out.write("Alien"+":"+aa.getAlienLives()+":"+aa.getX()+":"+aa.getY()+":"+aa.getAngle()+":"+aa.getVelocityX()+":"+aa.getVelocityY()+"\n");
	    	   }	
	    		out.write("NUM_ASTEROIDS"+":"+NUM_ASTEROIDS+"\n");
	    		out.write("CURR_LEVEL"+":"+CURR_LEVEL+"\n");
	    		out.write("playerSpeed"+":"+playerSpeed+"\n");
	    		out.write("ALIENSHIP_SPAWN_INTERVAL"+":"+ALIENSHIP_SPAWN_INTERVAL+"\n");
	    		out.write("ALIENSHIP_ALIVE"+":"+ALIENSHIP_ALIVE+"\n");
	    		out.write("ALIENSHIP_SPEED"+":"+ALIENSHIP_SPEED+"\n");
	    		out.write("isShipMoving"+":"+isShipMoving+"\n");
	    		
	    		out.write("GRAVITATIONAL_OBJECT_ACTIVE"+":"+GRAVITATIONAL_OBJECT_ACTIVE+"\n");
	    		out.write("GRAVITATIONAL_OBJECT_VISIBLE"+":"+GRAVITATIONAL_OBJECT_VISIBLE+"\n");
	    		
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
	             if (files.endsWith(".txt") || files.endsWith(".TXT"))
	             {
	                textFiles.add(files);	              
                }
	           }
	        }
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
	                	anim.addFrame(playerImage, 250);
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
	                	if(x[1].equals("Parent")){
	                		anim.addFrame(asteroidImage, 250);
	                	}else{
	                		anim.addFrame(childAsteroidImage, 250);
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
	                else if(x[0].equals("NUM_ASTEROIDS")){
	                	NUM_ASTEROIDS = Integer.parseInt(x[1]);
	                }
	                else if(x[0].equals("CURR_LEVEL")){
	                	CURR_LEVEL = Integer.parseInt(x[1]);
	                }
	                
	                else if(x[0].equals("ALIENSHIP_ALIVE")){
	                	ALIENSHIP_ALIVE = Boolean.parseBoolean(x[1]);
	                }
	                else if(x[0].equals("ALIENSHIP_SPEED")){
	                	ALIENSHIP_SPEED = Integer.parseInt(x[1]);
	                }
	                else if(x[0].equals("isShipMoving")){
	                	isShipMoving = Boolean.parseBoolean(x[1]);
	                }
	                else if(x[0].equals("GRAVITATIONAL_OBJECT_ACTIVE")){
	                	GRAVITATIONAL_OBJECT_ACTIVE = Boolean.parseBoolean(x[1]);
	                }
	                else if(x[0].equals("GRAVITATIONAL_OBJECT_VISIBLE")){
	                	GRAVITATIONAL_OBJECT_VISIBLE = Boolean.parseBoolean(x[1]);
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
	        
	        