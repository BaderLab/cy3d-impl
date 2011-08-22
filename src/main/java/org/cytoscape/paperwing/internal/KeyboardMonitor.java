package org.cytoscape.paperwing.internal;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;


/** This class represents a keyboard monitor that is able of keeping
 * track of keyboard events
 * 
 * @author Paperwing (Yue Dong)
 */
public class KeyboardMonitor implements KeyListener, FocusListener {

	/** A set of keys that were just pressed */
	private Set<Integer> justPressed;
	
	/** A set of keys that are currently pressed down, including 
	 * "just pressed" keys */
	private Set<Integer> keysDown;
	
	/** A set of keys that are just released */
	private Set<Integer> justReleased;
	
	/** Keeps track of whether a new key was pressed or released */
	private boolean hasNew = false;
	
	/** Checks if there is at least 1 key pressed down */
	private boolean hasHeld = false;
	
	/** Create a new KeyboardMonitor object */
	public KeyboardMonitor() {
		justPressed = new HashSet<Integer>();
		keysDown = new HashSet<Integer>();
		justReleased = new HashSet<Integer>();
	}
	
	/** Checks if a key was recently pressed or released */
	public boolean hasNew() {
		return hasNew;
	}
	
	/** Checks if a key is being held down */
	public boolean hasHeld() {
		return !keysDown.isEmpty();
	}
	
	/** Return the set of all recently pressed keys */
	public Set<Integer> getPressed() {
		return justPressed;
	}
	
	/** Return the set of all keys being held down */
	public Set<Integer> getHeld() {
		return keysDown;
	}
	
	/** Return the set of all keys just released */
	public Set<Integer> getReleased() {
		return justReleased;
	}

	@Override
	public void keyPressed(KeyEvent event) {
		Integer key = event.getKeyCode();
		
		if (!keysDown.contains(key)) {
			justPressed.add(key);
			keysDown.add(key);
			hasNew = true;
		}
		
		event.consume();
	}

	@Override
	public void keyReleased(KeyEvent event) {
		justReleased.add(event.getKeyCode());
		keysDown.remove(event.getKeyCode());
		hasNew = true;
		
		event.consume();
	}
	
	@Override
	public void keyTyped(KeyEvent event) {
	}
	
	/** Clear the list of newly pressed and released keys.
	 * 
	 * Brief note: This method should be called when wanting to clear the
	 * "hasNew" flag and move all just-pressed keys to held-down keys
	 */
	public void update() {
		justPressed.clear();
		justReleased.clear();
		hasNew = false;
	}

	@Override
	public void focusGained(FocusEvent event) {
		
	}

	@Override
	public void focusLost(FocusEvent event) {
		// Release all keys
		justReleased.addAll(keysDown);
		keysDown.clear();
		hasNew = true;
	}
	

}
