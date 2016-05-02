package org.baderlab.cy3d.internal.input.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.cytoscape.view.model.CyNetworkView;

/**
 * Provides reusable input event listening with pluggable Commands.
 * 
 * MKTODO updateBothRenderes() is always fired on all input, it would be better 
 * if each Command returned a flag indicating if the renderers actually need to be updated.
 * 
 * @author mkucera
 */
public class InputEventListener implements MouseListener, MouseMotionListener, MouseWheelListener {

	protected final GraphicsData graphicsData;
	protected final CyNetworkView networkView;
	private final int[] coords = new int[2];
	
	private Timer heartBeat;
	
	private int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
	private boolean[] key = new boolean[4];
	
	private MouseWheelCommand mouseWheelCommand = MouseWheelCommand.EMPTY;
	private MouseCommand primaryMouseCommand = MouseCommand.EMPTY;
	private MouseCommand secondaryMouseCommand = MouseCommand.EMPTY;
	private KeyCommand keyCommand = KeyCommand.EMPTY;
	
	private MouseCommand currentDragCommand;

	
	public InputEventListener(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
		this.networkView = graphicsData.getNetworkView();
	}
	
	
	public void setMouseWheelCommand(MouseWheelCommand mouseWheelCommand) {
		this.mouseWheelCommand = mouseWheelCommand;
	}

	public void setPrimaryMouseCommand(MouseCommand primaryMouseCommand) {
		this.primaryMouseCommand = primaryMouseCommand;
	}

	public void setSecondaryMouseCommand(MouseCommand secondaryMouseCommand) {
		this.secondaryMouseCommand = secondaryMouseCommand;
	}

	public void setKeyCommand(KeyCommand keyCommand) {
		this.keyCommand = keyCommand;
	}

	
	public void attachAll(JComponent component) {
		component.addMouseWheelListener(this);
		component.addMouseMotionListener(this);
		component.addMouseListener(this);
		setUpKeyboardInput(component);
	}
	

	private void updateBothRenderers() {
		fireUpdateEvents();
		networkView.updateView();
	}

	
	/** 
	 * Called before the view is updated, subclasses may override
	 * to fire any additional events.
	 */
	protected void fireUpdateEvents() {
		
	}
	
	// *** Mouse event handling ***
	
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		mouseWheelCommand.execute(e.getWheelRotation());
		updateBothRenderers();
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

	

	@Override
	public void mousePressed(MouseEvent e) {
		currentDragCommand = getModifiedMouseCommand(e);
		graphicsData.getPixelConverter().convertMouse(e, coords);
		currentDragCommand.dragStart(coords[0], coords[1]);
		updateBothRenderers();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		graphicsData.getPixelConverter().convertMouse(e, coords);
		currentDragCommand.dragMove(coords[0], coords[1]);
		updateBothRenderers();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		graphicsData.getPixelConverter().convertMouse(e, coords);
		currentDragCommand.dragEnd(coords[0], coords[1]);
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

	/**
	 * Keyboard movement needs to be animated.
	 * We will do that at a constant rate in order to avoid direct interaction with the main render loop.
	 */
	public void startKeyboardAnimation() {
		// need to tick at least as fast as the renderer loop to get smooth movement
		heartBeat = new Timer(30, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tickKey();
			}
		});
	}

	public void dispose() {
		if(heartBeat != null) {
			heartBeat.stop();
		}
	}

	private void tickKey() {
		if(key[UP])
			keyCommand.up();
		if(key[DOWN])
			keyCommand.down();
		if(key[LEFT])
			keyCommand.left();
		if(key[RIGHT])
			keyCommand.right();
		
		if(key[UP] || key[DOWN] || key[LEFT] || key[RIGHT]) {
			updateBothRenderers();
		}
		else {
			heartBeat.stop();
		}
	}
	
	
	protected void setUpKeyboardInput(JComponent component) {
		InputMap inputMap = component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap actionMap = component.getActionMap();
		
		inputMap.put(KeyStroke.getKeyStroke("pressed UP"),    "PRESSED_UP");
		inputMap.put(KeyStroke.getKeyStroke("pressed DOWN"),  "PRESSED_DOWN");
		inputMap.put(KeyStroke.getKeyStroke("pressed LEFT"),  "PRESSED_LEFT");
		inputMap.put(KeyStroke.getKeyStroke("pressed RIGHT"), "PRESSED_RIGHT");
		
		inputMap.put(KeyStroke.getKeyStroke("released UP"),    "RELEASED_UP");
		inputMap.put(KeyStroke.getKeyStroke("released DOWN"),  "RELEASED_DOWN");
		inputMap.put(KeyStroke.getKeyStroke("released LEFT"),  "RELEASED_LEFT");
		inputMap.put(KeyStroke.getKeyStroke("released RIGHT"), "RELEASED_RIGHT");
		 
		actionMap.put("PRESSED_UP",    keyAction(UP, true));
		actionMap.put("PRESSED_DOWN",  keyAction(DOWN, true));
		actionMap.put("PRESSED_LEFT",  keyAction(LEFT, true));
		actionMap.put("PRESSED_RIGHT", keyAction(RIGHT, true));
		
		actionMap.put("RELEASED_UP",    keyAction(UP, false));
		actionMap.put("RELEASED_DOWN",  keyAction(DOWN, false));
		actionMap.put("RELEASED_LEFT",  keyAction(LEFT, false));
		actionMap.put("RELEASED_RIGHT", keyAction(RIGHT, false));
	}
	
	
	@SuppressWarnings("serial")
	private Action keyAction(final int keyIndex, final boolean pressed) {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				key[keyIndex] = pressed;
				if(pressed && heartBeat != null) {
					heartBeat.start();
				}
			}
		};
	}


}