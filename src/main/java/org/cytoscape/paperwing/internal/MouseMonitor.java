package org.cytoscape.paperwing.internal;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;


public class MouseMonitor implements MouseListener, MouseMotionListener, MouseWheelListener, FocusListener {
	private int prevX;
	private int prevY;
	private int currentX;
	private int currentY;
	private int wheelChange;
	
	private boolean hasMoved;
	private boolean hasNew;
	
	private boolean ignoreNext;
	
	private Set<Integer> justPressed;
	private Set<Integer> buttonsDown;
	private Set<Integer> justReleased;
	
	public MouseMonitor() {
		justPressed = new HashSet<Integer>();
		buttonsDown = new HashSet<Integer>();
		justReleased = new HashSet<Integer>();
		
		ignoreNext = true;
	}
	
	public Set<Integer> getPressed() {
		return justPressed;
	}
	
	public Set<Integer> getReleased() {
		return justReleased;
	}
	
	public Set<Integer> getHeld() {
		return buttonsDown;
	}
	
	public boolean hasNew() {
		return hasNew;
	}
	
	public boolean hasMoved() {
		return hasMoved;
	}
	
	public int x() {
		return currentX;
	}
	
	public int dX() {
		return currentX - prevX;
	}
	
	public int y() {
		return currentY;
	}
	
	public int dY() {
		return currentY - prevY;
	}
	
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
	
	// Clear just pressed and just released buttons
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
