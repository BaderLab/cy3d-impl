package org.cytoscape.paperwing.internal.input;

import org.cytoscape.paperwing.internal.data.GraphicsData;

public class MainInputProcessor implements InputProcessor {
	
	private InputHandler cameraInputHandler = new CameraInputHandler();
	private InputHandler networkChangeInputHandler = new NetworkChangeInputHandler();
	private InputHandler dragMovementInputHandler = new DragMovementInputHandler();
	private InputHandler selectionInputHandler = new SelectionInputHandler();
	
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {

		cameraInputHandler.processInput(keys, mouse, graphicsData);
		networkChangeInputHandler.processInput(keys, mouse, graphicsData);
		dragMovementInputHandler.processInput(keys, mouse, graphicsData);
		selectionInputHandler.processInput(keys, mouse, graphicsData);
		
		keys.update();
		mouse.update();
	}
}
