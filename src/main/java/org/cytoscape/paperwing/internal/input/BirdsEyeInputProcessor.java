package org.cytoscape.paperwing.internal.input;

import java.util.LinkedList;
import java.util.List;

import org.cytoscape.paperwing.internal.data.GraphicsData;

public class BirdsEyeInputProcessor implements InputProcessor {

	/** The list of InputHandler objects used by this InputProcessor */
	private List<InputHandler> inputHandlers;
	
	public BirdsEyeInputProcessor() {
		inputHandlers = new LinkedList<InputHandler>();
	
		// Populate the list of InputHandler objects to be used by this InputProcessor.
		// The InputHandler objects are called in the order that they are added.
		inputHandlers.add(new BoundsInputHandler());
	}
	
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {
		
		for (InputHandler inputHandler : inputHandlers) {
			inputHandler.processInput(keys, mouse, graphicsData);
		}
		
		keys.update();
		mouse.update();
	}
}
