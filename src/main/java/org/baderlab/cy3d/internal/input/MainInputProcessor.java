package org.baderlab.cy3d.internal.input;

import java.util.LinkedList;
import java.util.List;

import org.baderlab.cy3d.internal.data.GraphicsData;

public class MainInputProcessor implements InputProcessor {
	
	/** The list of InputHandler objects used by this InputProcessor */
	private List<InputHandler> inputHandlers;
	
	public MainInputProcessor() {
		inputHandlers = new LinkedList<InputHandler>();
	
		// Populate the list of InputHandler objects to be used by this InputProcessor.
		// The InputHandler objects are called in the order that they are added.
		inputHandlers.add(new CameraInputHandler());
		inputHandlers.add(new DragMovementInputHandler());
		inputHandlers.add(new SelectionInputHandler());
		inputHandlers.add(new NetworkChangeInputHandler());
	
		inputHandlers.add(new ContextMenuInputHandler());
		inputHandlers.add(new DebugInputHandler());
	}
	
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse, GraphicsData graphicsData) {
		for (InputHandler inputHandler : inputHandlers) {
			inputHandler.processInput(keys, mouse, graphicsData);
		}
		
		keys.update();
		mouse.update();
	}
}
