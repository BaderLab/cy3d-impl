package org.cytoscape.paperwing.internal.graphics;

import org.cytoscape.paperwing.internal.KeyboardMonitor;
import org.cytoscape.paperwing.internal.MouseMonitor;
import org.cytoscape.paperwing.internal.input.CameraInputHandler;
import org.cytoscape.paperwing.internal.input.InputHandler;
import org.cytoscape.paperwing.internal.input.NetworkChangeInputHandler;
import org.cytoscape.paperwing.internal.input.SelectionInputHandler;

public class MainInputProcessor implements InputProcessor {
	
	private InputHandler cameraInputHandler = new CameraInputHandler();
	private InputHandler networkChangeInputHandler = new NetworkChangeInputHandler();
	private InputHandler selectionInputHandler = new SelectionInputHandler();
	
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {

		cameraInputHandler.processInput(keys, mouse, graphicsData);
		networkChangeInputHandler.processInput(keys, mouse, graphicsData);
		selectionInputHandler.processInput(keys, mouse, graphicsData);
		
		keys.update();
		mouse.update();
	}
}
