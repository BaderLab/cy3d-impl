package org.cytoscape.paperwing.internal;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Set;
import java.util.TreeSet;


public class KeyboardMonitor implements KeyListener {

	private TreeSet<Integer> justPressed;
	private TreeSet<Integer> keysDown;
	private TreeSet<Integer> justReleased;
	
	private boolean hasNew = false;
	private boolean hasHeld = false;
	
	public KeyboardMonitor() {
		justPressed = new TreeSet<Integer>();
		keysDown = new TreeSet<Integer>();
		justReleased = new TreeSet<Integer>();
	}
	
	public boolean hasNew() {
		return hasNew;
	}
	
	public boolean hasHeld() {
		return !keysDown.isEmpty();
	}
	
	public Set<Integer> getPressed() {
		return justPressed;
	}
	
	public Set<Integer> getHeld() {
		return keysDown;
	}
	
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
	}

	@Override
	public void keyReleased(KeyEvent event) {
		justReleased.add(event.getKeyCode());
		keysDown.remove(event.getKeyCode());
		hasNew = true;
	}
	
	@Override
	public void keyTyped(KeyEvent event) {
	}
	
	// Method should be called when wanting to clear the list of newly pressed and released keys
	public void update() {
		justPressed.clear();
		justReleased.clear();
		hasNew = false;
	}
	

}
