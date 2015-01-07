package org.baderlab.cy3d.internal.input.handler;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.PixelConverter;
import org.baderlab.cy3d.internal.input.handler.commands.CameraPanDragCommand;
import org.baderlab.cy3d.internal.input.handler.commands.CameraPanStartCommand;
import org.baderlab.cy3d.internal.input.handler.commands.CameraZoomCommand;
import org.baderlab.cy3d.internal.input.handler.commands.SelectionClickCommand;
import org.baderlab.cy3d.internal.input.handler.commands.SelectionDragCommand;
import org.baderlab.cy3d.internal.input.handler.commands.SelectionReleaseCommand;
import org.baderlab.cy3d.internal.input.handler.commands.SelectionStartCommand;


public class MainInputEventHandler implements MouseListener, MouseMotionListener, MouseWheelListener {

	private final GraphicsData graphicsData;
	private final PixelConverter pixelConverter;
	private final int[] coords = new int[2];
	
	private MouseWheelCommand mouseWheelCommand;
	private MouseCommand mousePressedCommand;
	private MouseCommand mouseDraggedCommand;
	private MouseCommand mouseReleasedCommand;
	private MouseCommand mouseClickedCommand;
	
	
	public MainInputEventHandler(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
		this.pixelConverter = graphicsData.getPixelConverter();
		createInitialCommands();
	}
	
	
	// Mode selection
	
	private void createInitialCommands() {
		mouseWheelCommand    = new CameraZoomCommand(graphicsData);
		setMouseMode(MouseMode.getDefault());
	}
	
	public void setMouseMode(MouseMode mode) {
		switch(mode) {
		case STRAFE: // MKTODO
		case ORBIT:  // MKTODO
		case SELECT: enterSelectionMode(); break;
		case PAN:    enterCameraPanMode(); break;
		}
	}
	
	private void enterSelectionMode() {
		mousePressedCommand  = new SelectionStartCommand(graphicsData);
		mouseDraggedCommand  = new SelectionDragCommand(graphicsData);
		mouseReleasedCommand = new SelectionReleaseCommand(graphicsData);
		mouseClickedCommand  = new SelectionClickCommand(graphicsData);
	}
	
	private void enterCameraPanMode() {
		CameraPanDragCommand dragCommand = new CameraPanDragCommand(graphicsData);
		mousePressedCommand  = new CameraPanStartCommand(dragCommand);
		mouseDraggedCommand  = dragCommand;
		mouseReleasedCommand = MouseCommand.EMPTY;
		mouseClickedCommand  = MouseCommand.EMPTY;
	}
	
	
	// Input event handling
	
	private void convertCoords(MouseEvent e) {
		// put the result in coords to use
		coords[0] = e.getX();
		coords[1] = e.getY();
		pixelConverter.convertToPixelUnits(coords);
	}

	private static MouseButton convertMouseButton(MouseEvent e) {
		switch(e.getButton()) {
			case MouseEvent.BUTTON1: return MouseButton.BUTTON_1;
			case MouseEvent.BUTTON2: return MouseButton.BUTTON_2;
			case MouseEvent.BUTTON3: return MouseButton.BUTTON_3;
			default: return null;
		}
	}
	
	private void handleMouse(MouseEvent e, MouseCommand mouseCommand) {
		MouseButton button = convertMouseButton(e);
		if(button == null)
			return;
		convertCoords(e);
		mouseCommand.command(button, coords[0], coords[1]);
	}
	
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		mouseWheelCommand.execute(e.getWheelRotation());
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		handleMouse(e, mousePressedCommand);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		handleMouse(e, mouseReleasedCommand);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		handleMouse(e, mouseDraggedCommand);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		handleMouse(e, mouseClickedCommand);
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
	
	
}
