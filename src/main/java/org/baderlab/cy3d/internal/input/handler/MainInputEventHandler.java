package org.baderlab.cy3d.internal.input.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.baderlab.cy3d.internal.Cy3DRenderingEngine;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.PixelConverter;
import org.baderlab.cy3d.internal.input.handler.commands.CameraPanKeyCommand;
import org.baderlab.cy3d.internal.input.handler.commands.CameraPanMouseCommand;
import org.baderlab.cy3d.internal.input.handler.commands.CameraStrafeKeyCommand;
import org.baderlab.cy3d.internal.input.handler.commands.CameraStrafeMouseCommand;
import org.baderlab.cy3d.internal.input.handler.commands.CameraZoomCommand;
import org.baderlab.cy3d.internal.input.handler.commands.SelectionMenuMouseCommand;
import org.baderlab.cy3d.internal.input.handler.commands.SelectionMouseCommand;


/**
 * ALT - forces camera mode
 * SHIFT - forces selection mode
 * 
 * This class has two responsibilities.
 * 1) Handle input events from Swing.
 * 2) On each input event decides which command should be executed
 * 
 * Mouse Modes:
 * 
 * Modes
 * - Shift: force select mode
 * - Alt: force camera mode
 * 
 * Select mode
 * - Left drag (Ctrl modifier) = selection rectangle
 * - Right drag (Ctrl modifier) = selection rectange
 * - Left click (Ctrl modifyier) = select single node
 * - Right click = context menu
 * 
 * Camera mode
 * - Left drag - pan
 * - Right drag - strafe
 * - Left/Right click = do nothing?
 * 
 * Keyboard:
 * - always controlls the camera
 * 
 * @author mkucera
 *
 */
public class MainInputEventHandler implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

	private final GraphicsData graphicsData;
	private final PixelConverter pixelConverter;
	private final int[] coords = new int[2];
	
	private Timer heartBeat;
	
	private boolean keyUp;
	private boolean keyLeft;
	private boolean keyRight;
	private boolean keyDown;
	
	private MouseWheelCommand mouseWheelCommand;
	private MouseCommand rightMouseCommand;
	private MouseCommand leftMouseCommand;
	private KeyCommand keyCommand;
	
	
	public MainInputEventHandler(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
		this.pixelConverter = graphicsData.getPixelConverter();
		createInitialCommands();
		startHeartbeat();
	}
	
	
	
	// *** Mode selection ***
	
	private void createInitialCommands() {
		mouseWheelCommand = new CameraZoomCommand(graphicsData);
		keyCommand = new CameraPanKeyCommand(graphicsData.getCamera());
		// assume toolbar also starts off using the default
		setMouseMode(MouseMode.getDefault());
	}
	
	
	// Setting the mouse mode can be independent from the toolbar, eg) Alt forces camera mode
	public void setMouseMode(MouseMode mouseMode) {
		switch(mouseMode) {
			case CAMERA: 
				leftMouseCommand  = new CameraPanMouseCommand(graphicsData.getCamera()); 
				rightMouseCommand = new CameraStrafeMouseCommand(graphicsData.getCamera()); 
				break;
			case SELECT: 
				leftMouseCommand  = new SelectionMouseCommand(graphicsData); 
				rightMouseCommand = new SelectionMenuMouseCommand(graphicsData);
				break;
		}
	}
	
	
	// *** Mouse event handling ***
	
	private void convertCoords(MouseEvent e) {
		// put the result in coords to use
		coords[0] = e.getX();
		coords[1] = e.getY();
		pixelConverter.convertToPixelUnits(coords);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		mouseWheelCommand.execute(e.getWheelRotation());
	}
	

	private MouseCommand currentDragCommand;
	
	
	@Override
	public void mousePressed(MouseEvent e) {
		currentDragCommand = MouseCommand.EMPTY;
		
		if(SwingUtilities.isLeftMouseButton(e)) { 
			currentDragCommand = leftMouseCommand;
			if(e.isAltDown())
				currentDragCommand = new CameraPanMouseCommand(graphicsData.getCamera()); 
			if(e.isShiftDown())
				currentDragCommand = new SelectionMouseCommand(graphicsData); 
		}
		else if(SwingUtilities.isRightMouseButton(e)) {
			currentDragCommand = rightMouseCommand;
			if(e.isAltDown())
				currentDragCommand = new CameraStrafeMouseCommand(graphicsData.getCamera()); 
			if(e.isShiftDown())
				currentDragCommand = new SelectionMenuMouseCommand(graphicsData);
		}
		
		convertCoords(e);
		currentDragCommand.pressed(coords[0], coords[1]);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		convertCoords(e);
		currentDragCommand.dragged(coords[0], coords[1]);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		convertCoords(e);
		currentDragCommand.released(coords[0], coords[1]);
	}
	

	@Override
	public void mouseClicked(MouseEvent e) {
		
		if(SwingUtilities.isLeftMouseButton(e)) {
			convertCoords(e);
			leftMouseCommand.pressed(coords[0], coords[1]);
		}
		else if(SwingUtilities.isRightMouseButton(e)) {
			convertCoords(e);
			rightMouseCommand.pressed(coords[0], coords[1]);
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		// probably need to do something for hover
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	
	
	// *** Key event handling ***
	
	
	/**
	 * Keyboard movement needs to be animated.
	 * We will do that at a constant rate in order to avoid direct interaction with the main render loop.
	 */
	public void startHeartbeat() {
		// need to tick at the at least as fast as the renderer to get smooth movement
		heartBeat = new Timer(Cy3DRenderingEngine.FPS_TARGET, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tickKey();
			}
		});
		heartBeat.start();
	}
	
	public void dispose() {
		heartBeat.stop();
	}
	
	private void tickKey() {
		if(keyUp)
			keyCommand.up();
		if(keyDown)
			keyCommand.down();
		if(keyLeft)
			keyCommand.left();
		if(keyRight)
			keyCommand.right();
	}
	

	@Override
	public void keyPressed(KeyEvent e) {
		
		switch(e.getKeyCode()) {
			case KeyEvent.VK_UP:    keyUp    = true;  break;
			case KeyEvent.VK_LEFT:  keyLeft  = true;  break;
			case KeyEvent.VK_RIGHT: keyRight = true;  break;
			case KeyEvent.VK_DOWN:  keyDown  = true;  break;
			case KeyEvent.VK_ALT:
				keyCommand = new CameraStrafeKeyCommand(graphicsData.getCamera());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_UP:    keyUp    = false;  break;
			case KeyEvent.VK_LEFT:  keyLeft  = false;  break;
			case KeyEvent.VK_RIGHT: keyRight = false;  break;
			case KeyEvent.VK_DOWN:  keyDown  = false;  break;
			case KeyEvent.VK_ALT:
				keyCommand = new CameraPanKeyCommand(graphicsData.getCamera());
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	
	
	

	
	
}
