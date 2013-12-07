import javax.swing.JInternalFrame;


public class MyInternalFrame extends JInternalFrame {

	static int openFrameCount = 0;
	static final int xOffset = 30, yOffset = 30;

	public MyInternalFrame() {
	    super("Document #" + (++openFrameCount),true,true,true,true);
	    //...Create the GUI and put it in the window...
	    //...Then set the window size or call pack...
	 
	    //Set the window's location.
	    setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
	}

}
