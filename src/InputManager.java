

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

/**
    The InputManager manages input of key and mouse events.
    Events are mapped to GameActions.
*/
public class InputManager implements KeyListener
{
   // key codes are defined in java.awt.KeyEvent.
    // most of the codes (except for some rare ones like
    // "alt graph") are less than 600.
    private static final int NUM_KEY_CODES = 600;

    private GameAction[] keyActions =
        new GameAction[NUM_KEY_CODES];
   
    private Component comp;
    private Robot robot;
    
    /**
        Creates a new InputManager that listens to input from the
        specified component.
    */
    public InputManager(Component comp) {
        this.comp = comp;
    
        // register key and mouse listeners
        comp.addKeyListener(this);
    
        // allow input of the TAB key and other keys normally
        // used for focus traversal
        comp.setFocusTraversalKeysEnabled(false);
    }

    /**
        Maps a GameAction to a specific key. The key codes are
        defined in java.awt.KeyEvent. If the key already has
        a GameAction mapped to it, the new GameAction overwrites
        it.
    */
    public void mapToKey(GameAction gameAction, int keyCode) {
        keyActions[keyCode] = gameAction;
    }


    /**
        Clears all mapped keys and mouse actions to this
        GameAction.
    */
    public void clearMap(GameAction gameAction) {
        for (int i=0; i<keyActions.length; i++) {
            if (keyActions[i] == gameAction) {
                keyActions[i] = null;
            }
        }

        gameAction.reset();
    }


    /**
        Gets a List of names of the keys and mouse actions mapped
        to this GameAction. Each entry in the List is a String.
    */
    public List getMaps(GameAction gameCode) {
        ArrayList list = new ArrayList();

        for (int i=0; i<keyActions.length; i++) {
            if (keyActions[i] == gameCode) {
                list.add(getKeyName(i));
            }
        }

        return list;
    }


    /**
        Resets all GameActions so they appear like they haven't
        been pressed.
    */
    public void resetAllGameActions() {
        for (int i=0; i<keyActions.length; i++) {
            if (keyActions[i] != null) {
                keyActions[i].reset();
            }
        }

    }


    /**
        Gets the name of a key code.
    */
    public static String getKeyName(int keyCode) {
        return KeyEvent.getKeyText(keyCode);
    }


    private GameAction getKeyAction(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode < keyActions.length) {
            return keyActions[keyCode];
        }
        else {
            return null;
        }
    }

    // from the KeyListener interface
    public void keyPressed(KeyEvent e) {
        GameAction gameAction = getKeyAction(e);
        if (gameAction != null) {
            gameAction.press();
        }
        // make sure the key isn't processed for anything else
        e.consume();
    }


    // from the KeyListener interface
    public void keyReleased(KeyEvent e) {
        GameAction gameAction = getKeyAction(e);
        if (gameAction != null) {
            gameAction.release();
        }
        // make sure the key isn't processed for anything else
        e.consume();
    }


    // from the KeyListener interface
    public void keyTyped(KeyEvent e) {
        // make sure the key isn't processed for anything else
        e.consume();
    }
}
