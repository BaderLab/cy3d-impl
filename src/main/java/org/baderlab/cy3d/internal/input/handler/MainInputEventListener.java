package org.baderlab.cy3d.internal.input.handler;

import java.awt.Component;
import java.awt.event.KeyEvent;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.eventbus.MainCameraChangeEvent;
import org.baderlab.cy3d.internal.eventbus.MouseModeChangeEvent;
import org.baderlab.cy3d.internal.input.handler.commands.CameraOrbitKeyCommand;
import org.baderlab.cy3d.internal.input.handler.commands.CameraOrbitMouseCommand;
import org.baderlab.cy3d.internal.input.handler.commands.CameraZoomCommand;
import org.baderlab.cy3d.internal.input.handler.commands.PopupMenuMouseCommand;
import org.baderlab.cy3d.internal.input.handler.commands.SelectionMouseCommand;

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
public class MainInputEventListener extends InputEventListener {

	private final MouseZoneInputListener mouseZoneListener;
	
	private MainInputEventListener(GraphicsData graphicsData, MouseZoneInputListener mouseZoneListener) {
		super(graphicsData);
		this.mouseZoneListener = mouseZoneListener;
		createInitialCommands();
		startKeyboardAnimation();
	}
	
	public static MainInputEventListener attach(Component component, GraphicsData graphicsData, MouseZoneInputListener mouseZoneListener) {
		MainInputEventListener inputHandler = new MainInputEventListener(graphicsData, mouseZoneListener);
		inputHandler.attachAll(component);
		
		EventBus eventBus = graphicsData.getEventBus();
		eventBus.register(inputHandler);
		
		return inputHandler;
	}
	
	private void createInitialCommands() {
		setMouseWheelCommand(new CameraZoomCommand(graphicsData));
		setKeyCommand(new CameraOrbitKeyCommand(graphicsData.getCamera()));
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
				CameraOrbitMouseCommand orbitCommand = new CameraOrbitMouseCommand(graphicsData);
				orbitCommand.setIsRotateSampler(getRotateSampler());
				setPrimaryMouseCommand(orbitCommand); 
				setSecondaryMouseCommand(orbitCommand); 
				break;
			case SELECT: 
				setPrimaryMouseCommand(new SelectionMouseCommand(graphicsData)); 
				setSecondaryMouseCommand(new PopupMenuMouseCommand(graphicsData));
				break;
		}
	}
	
	
	private CameraOrbitMouseCommand.IsRotateSampler getRotateSampler() {
		return new CameraOrbitMouseCommand.IsRotateSampler() {
			public boolean isRotate() {
				return mouseZoneListener.isRotate();
			}
		};
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
