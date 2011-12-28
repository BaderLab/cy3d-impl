package org.cytoscape.paperwing.internal.input;

import org.cytoscape.paperwing.internal.data.GraphicsData;

public class BirdsEyeInputProcessor implements InputProcessor {

	private InputHandler boundsInputHandler = new BoundsInputHandler();
	
	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {
		
		boundsInputHandler.processInput(keys, mouse, graphicsData);

		keys.update();
		mouse.update();
	}

}
