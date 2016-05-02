package org.baderlab.cy3d.internal.input.handler;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

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
	
	public static MainInputEventListener attach(JComponent component, GraphicsData graphicsData, MouseZoneInputListener mouseZoneListener) {
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
	
	
	@Override
	protected void fireUpdateEvents() {
		graphicsData.getEventBus().post(new MainCameraChangeEvent(graphicsData.getCamera()));
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
				orbitCommand.setIsRotateSampler(mouseZoneListener);
				setPrimaryMouseCommand(orbitCommand); 
				setSecondaryMouseCommand(orbitCommand); 
				break;
			case SELECT: 
				setPrimaryMouseCommand(new SelectionMouseCommand(graphicsData)); 
				setSecondaryMouseCommand(new PopupMenuMouseCommand(graphicsData));
				break;
		}
	}
	
	
	@SuppressWarnings("serial")
	@Override
	protected void setUpKeyboardInput(JComponent component) {
		super.setUpKeyboardInput(component);
		
		InputMap inputMap = component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap actionMap = component.getActionMap();
		
		inputMap.put(KeyStroke.getKeyStroke("pressed R"), "PRESSED_R");
		 
		actionMap.put("PRESSED_R", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				graphicsData.getCamera().reset();
				graphicsData.getEventBus().post(new MainCameraChangeEvent(graphicsData.getCamera()));
				networkView.fitContent();
			}
		});
	}
	
}
