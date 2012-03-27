package org.cytoscape.paperwing.internal;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.swing.SwingUtilities;

import org.cytoscape.paperwing.internal.coordinator.ViewingCoordinator;

public class AnimatorController implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
	
	private Set<Integer> keysDown;
	private Set<Integer> mouseKeysDown;
	
	private GLAnimatorControl animatorControl;
	private ViewingCoordinator coordinator;

	private boolean mouseInRegion = false;
	
	public AnimatorController(final GLAnimatorControl animatorControl) {
		this.animatorControl = animatorControl;
		coordinator = null;
		
		keysDown = new HashSet<Integer>();
		mouseKeysDown = new HashSet<Integer>();
	}
	
	public boolean hasKeysDown() {
		return (keysDown.size() != 0 || mouseKeysDown.size() != 0);
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		mouseInRegion = true;
	}

	@Override
	public void mouseExited(MouseEvent event) {
		mouseInRegion = false;
	}

	@Override
	public void mousePressed(MouseEvent event) {
		mouseKeysDown.add(event.getButton());
		
		if (!animatorControl.isAnimating()) {
			startAnimator();
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		mouseKeysDown.remove(event.getButton());
		
		if (keysDown.isEmpty()) {
			stopAnimator();
		}
	}

	@Override
	public void keyPressed(KeyEvent event) {
		keysDown.add(event.getKeyCode());
		
		if (!animatorControl.isAnimating()) {
			startAnimator();
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		keysDown.remove(event.getKeyCode());
		
		if (keysDown.isEmpty()) {
			stopAnimator();
		}
	}

	@Override
	public void keyTyped(KeyEvent event) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		startAnimator();
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		startAnimator();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		startAnimator();
	}

	public void setCoordinator(ViewingCoordinator coordinator) {
		this.coordinator = coordinator;
	}

	public ViewingCoordinator getCoordinator() {
		return coordinator;
	}

	public void startAnimator() {
		animatorControl.start();
		
		if (coordinator.isBirdsEyeClaimed() && coordinator.getBirdsEyeAnimatorControl() != null) {
			coordinator.getBirdsEyeAnimatorControl().start();
		}
	}
	
	public void stopAnimator() {
		animatorControl.stop();
		
		if (coordinator.isBirdsEyeClaimed() && coordinator.getBirdsEyeAnimatorControl() != null) {
			coordinator.getBirdsEyeAnimatorControl().stop();
		}
	}
	
}
