package org.cytoscape.paperwing.internal;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashSet;
import java.util.Set;

/** This class represents a monitor that can keep track of mouse events.
 * 
 * Note that losing focus through the FocusListener will cause all mouse
 * buttons to be treated as released, and the next mouse move event will be
 * ignored.
 * 
 * @author Paperwing (Yue Dong)
 */
public class MouseMonitor implements MouseListener, MouseMotionListener,
		MouseWheelListener, FocusListener {
	
	/** The previous mouse x-coordinate, since the previous frame */
	private int prevX;
	
	/** The previous mouse y-coordinate, since the previous frame */
	private int prevY;
	
	/** The current mouse x-coordinate, since the previous frame */
	private int currentX;
	
	/** The current mouse y-coordinate, since the previous frame */
	private int currentY;
	
	/** The change in the mouse wheel position, since the previous frame */
	private int wheelChange;
	
	/** Keep track of if the mouse has moved */
	private boolean hasMoved;
	
	/** Keep track of if a mouse button was recently pressed or released */
	private boolean hasNew;
	
	/** Flag to ignore the next mouse move event */
	private boolean ignoreNext;
	
	/** The set of all recently pressed mouse keys */
	private Set<Integer> justPressed;
	
	/** The set of all currently held down mouse buttons */
	private Set<Integer> buttonsDown;
	
	/** The set of all recently released mouse buttons */
	private Set<Integer> justReleased;
	
	/** Construct a new mouse monitor object */
	public MouseMonitor() {
		justPressed = new HashSet<Integer>();
		buttonsDown = new HashSet<Integer>();
		justReleased = new HashSet<Integer>();
		
		ignoreNext = true;
	}
	
	/** Return the set of all recently pressed keys */
	public Set<Integer> getPressed() {
		return justPressed;
	}
	
	/** Return the set of all recently released keys */
	public Set<Integer> getReleased() {
		return justReleased;
	}
	
	/** Return the set of all currently held down mouse buttons */
	public Set<Integer> getHeld() {
		return buttonsDown;
	}
	
	/** Check if a button was recently pressed or released */
	public boolean hasNew() {
		return hasNew;
	}
	
	/** Check if the mouse has moved since the last frame */
	public boolean hasMoved() {
		return hasMoved;
	}
	
	/** Return the current mouse x position */
	public int x() {
		return currentX;
	}
	
	/** Return the difference in the mouse x coordinate since the last frame */
	public int dX() {
		return currentX - prevX;
	}
	
	/** Return the current mouse y position */
	public int y() {
		return currentY;
	}
	
	/** Return the difference in the mouse y coordinate since the last frame */
	public int dY() {
		return currentY - prevY;
	}
	
	/** Return the difference in the mouse wheel position since the last 
	 * frame */
	public int dWheel() {
		return wheelChange;
	}
	
	@Override
	public void mouseDragged(MouseEvent event) {
		mouseMoved(event);
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		hasMoved = true;
		currentX = event.getX();
		currentY = event.getY();
		
		if (ignoreNext) {
			prevX = currentX;
			prevY = currentY;
			ignoreNext = false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent event) {
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		ignoreNext = false;
	}

	@Override
	public void mouseExited(MouseEvent event) {
		ignoreNext = false;
	}

	@Override
	public void mousePressed(MouseEvent event) {
		Integer button = event.getButton();
		
		if (button != MouseEvent.NOBUTTON) {
			justPressed.add(button);
			buttonsDown.add(button);
			hasNew = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		justReleased.add(event.getButton());
		buttonsDown.remove(event.getButton());
		hasNew = true;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		wheelChange += event.getWheelRotation();
		hasNew = true;
	}
	
	/** Clear the just pressed and just released buttons, as well
	 * as resetting mouse wheel position changes and removing
	 * mouse coordinate differences since the last frame.
	 * 
	 * This method should be called at the end of a frame
	 */
	public void update() {
		justPressed.clear();
		justReleased.clear();
		hasNew = false;
		hasMoved = false;
		
		wheelChange = 0;
		prevX = currentX;
		prevY = currentY;
	}

	@Override
	public void focusGained(FocusEvent event) {
	}

	@Override
	public void focusLost(FocusEvent event) {
		// Release all keys
		justReleased.addAll(buttonsDown);
		buttonsDown.clear();
		ignoreNext = true;
		hasNew = true;
	}
	

}
