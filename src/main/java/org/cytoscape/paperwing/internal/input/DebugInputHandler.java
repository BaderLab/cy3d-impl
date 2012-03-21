package org.cytoscape.paperwing.internal.input;

import java.util.Set;

import org.cytoscape.paperwing.internal.data.GraphicsData;

import com.jogamp.newt.event.KeyEvent;

/**
 * {@link InputHandler} object used to handle debug-related input
 */
public class DebugInputHandler implements InputHandler {

	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {
		
		Set<Integer> pressed = keys.getPressed();
		
		if (pressed.contains(KeyEvent.VK_M)) {
			graphicsData.setUpdateScene(true);
		}
		
		// Toggle FPS display
		if (pressed.contains(KeyEvent.VK_K)) {
			graphicsData.setShowFPS(!graphicsData.getShowFPS());
		}
		
		if (pressed.contains(KeyEvent.VK_L)) {
			graphicsData.setShowAllNodeLabels(!graphicsData.getShowAllNodeLabels());
		}
	}

}
