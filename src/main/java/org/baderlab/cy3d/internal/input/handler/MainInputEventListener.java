package org.baderlab.cy3d.internal.input.handler;

import java.awt.Component;
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

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.eventbus.MainCameraChangeEvent;
import org.baderlab.cy3d.internal.eventbus.MouseModeChangeEvent;
import org.baderlab.cy3d.internal.input.handler.commands.CameraOrbitKeyCommand;
import org.baderlab.cy3d.internal.input.handler.commands.CameraOrbitMouseCommand;
import org.baderlab.cy3d.internal.input.handler.commands.CameraZoomCommand;
import org.baderlab.cy3d.internal.input.handler.commands.PopupMenuMouseCommand;
import org.baderlab.cy3d.internal.input.handler.commands.SelectionMouseCommand;
import org.cytoscape.view.model.CyNetworkView;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;


/**
 * This class has two responsibilities.
 * 1) Handle input events from Swing.
 * 2) On each input event decides which command should be executed
 * 
 * Mouse Modes:
 * 
 * The current mouse mode is the one selected in the toolbar.
 * Alt and Shift "override" the current mode, they force camera 
 * mode and selection mode respectively (this is done in {@link ToolPanel}).
 * Ctrl is a "modifier", it takes the current command and "modifies" it.
 * 
 * Keyboard:
 * 
 * The keyboard always controls the camera.
 * 
 * @author mkucera
 *
 */
public class MainInputEventListener implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

	private final GraphicsData graphicsData;
	private final CyNetworkView networkView;
	
	private final int[] coords = new int[2];
	private Timer heartBeat;
	
	private boolean keyUp;
	private boolean keyLeft;
	private boolean keyRight;
	private boolean keyDown;
	
	private MouseWheelCommand mouseWheelCommand;
	private MouseCommand primaryMouseCommand;
	private MouseCommand secondaryMouseCommand;
	private KeyCommand keyCommand;
	
	
	private MainInputEventListener(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
		this.networkView = graphicsData.getNetworkView();
		createInitialCommands();
		startHeartbeat();
	}
	
	public static MainInputEventListener attach(Component component, GraphicsData graphicsData) {
		MainInputEventListener inputHandler = new MainInputEventListener(graphicsData);
		
		component.addMouseWheelListener(inputHandler);
		component.addMouseMotionListener(inputHandler);
		component.addMouseListener(inputHandler);
		component.addKeyListener(inputHandler);
		
		EventBus eventBus = graphicsData.getEventBus();
		eventBus.register(inputHandler);
		
		return inputHandler;
	}
	
	private void createInitialCommands() {
		mouseWheelCommand = new CameraZoomCommand(graphicsData);
		keyCommand = new CameraOrbitKeyCommand(graphicsData.getCamera());
		setMouseMode(MouseMode.getDefault()); // assume toolbar also starts off using the default
	}
	
	
	// *** Mode selection ***
	
	
	
	/** Called when a button on the toolbar is pressed. */
	@Subscribe
	public void mouseModeChanged(MouseModeChangeEvent mouseModeChangeEvent) { 
		setMouseMode(mouseModeChangeEvent.getMouseMode());
	}
	
	private void setMouseMode(MouseMode mouseMode) {
		switch(mouseMode) {
			case CAMERA: 
				primaryMouseCommand   = new CameraOrbitMouseCommand(graphicsData.getCamera()); 
				secondaryMouseCommand = new CameraOrbitMouseCommand(graphicsData.getCamera()); 
				break;
			case SELECT: 
				primaryMouseCommand   = new SelectionMouseCommand(graphicsData); 
				secondaryMouseCommand = new PopupMenuMouseCommand(graphicsData);
				break;
		}
	}
	
	
	public void touch() {
		networkView.updateView();
	}
	
	private void updateBothRenderers() {
		graphicsData.getEventBus().post(new MainCameraChangeEvent(graphicsData.getCamera()));
		networkView.updateView();
	}
	
	// *** Mouse event handling ***
	

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		mouseWheelCommand.execute(e.getWheelRotation());
		networkView.updateView();
	}
	
	
	/**
	 * The current mouse command is modified by holding Ctrl.
	 */
	private MouseCommand getModifiedMouseCommand(MouseEvent e) {
		MouseCommand command = MouseCommand.EMPTY;
		if(SwingUtilities.isLeftMouseButton(e))
			command = primaryMouseCommand;
		else if(SwingUtilities.isRightMouseButton(e))
			command = secondaryMouseCommand;
		
		if(e.isControlDown())
			command = command.modify();
		
		return command;
	}
	
	
	
	private MouseCommand currentDragCommand;
	
	@Override
	public void mousePressed(MouseEvent e) {
		currentDragCommand = getModifiedMouseCommand(e);
		graphicsData.getPixelConverter().convertMouse(e, coords);
		currentDragCommand.pressed(coords[0], coords[1]);
		updateBothRenderers();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		graphicsData.getPixelConverter().convertMouse(e, coords);
		currentDragCommand.dragged(coords[0], coords[1]);
		updateBothRenderers();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		graphicsData.getPixelConverter().convertMouse(e, coords);
		currentDragCommand.released(coords[0], coords[1]);
		updateBothRenderers();
	}
	

	@Override
	public void mouseClicked(MouseEvent e) {
		MouseCommand clickCommand = getModifiedMouseCommand(e);
		graphicsData.getPixelConverter().convertMouse(e, coords);
		clickCommand.clicked(coords[0], coords[1]);
		updateBothRenderers();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		graphicsData.getPixelConverter().convertMouse(e, coords);
		// needed for hover highlight
		graphicsData.setMouseCurrentX(coords[0]);
		graphicsData.setMouseCurrentY(coords[1]);
		primaryMouseCommand.moved(coords[0], coords[1]);
		updateBothRenderers();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		primaryMouseCommand.entered();
		updateBothRenderers();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		primaryMouseCommand.exited();
		updateBothRenderers();
	}
	
	
	
	// *** Key event handling ***
	
	
	/**
	 * Keyboard movement needs to be animated.
	 * We will do that at a constant rate in order to avoid direct interaction with the main render loop.
	 */
	public void startHeartbeat() {
		// need to tick at least as fast as the renderer loop to get smooth movement
		heartBeat = new Timer(30, new ActionListener() {
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
		
		if(keyUp || keyDown || keyLeft || keyRight)
			updateBothRenderers();
	}
	

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_UP:    keyUp    = true;  break;
			case KeyEvent.VK_LEFT:  keyLeft  = true;  break;
			case KeyEvent.VK_RIGHT: keyRight = true;  break;
			case KeyEvent.VK_DOWN:  keyDown  = true;  break;
//			case KeyEvent.VK_ALT:
//				keyCommand = new CameraStrafeKeyCommand(graphicsData.getCamera());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_UP:    keyUp    = false;  break;
			case KeyEvent.VK_LEFT:  keyLeft  = false;  break;
			case KeyEvent.VK_RIGHT: keyRight = false;  break;
			case KeyEvent.VK_DOWN:  keyDown  = false;  break;
//			case KeyEvent.VK_ALT:
//				keyCommand = new CameraPanKeyCommand(graphicsData.getCamera());
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		switch(e.getKeyChar()) {
			case 'R':
			case 'r': 
				graphicsData.getCamera().reset();
				graphicsData.getEventBus().post(new MainCameraChangeEvent(graphicsData.getCamera()));
				networkView.fitContent();
				break;
		}
	}
	
}
