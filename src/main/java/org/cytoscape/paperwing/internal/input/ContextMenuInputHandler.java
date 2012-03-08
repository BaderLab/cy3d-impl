package org.cytoscape.paperwing.internal.input;

import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.cytoscape.paperwing.internal.data.GraphicsData;

/**
 * Input handler responsible for creating the right-click context menu.
 */
public class ContextMenuInputHandler implements InputHandler {

	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {

		processNetworkRightClickMenu(mouse, graphicsData);
	}

	private void processNetworkRightClickMenu(MouseMonitor mouse, GraphicsData graphicsData){
		Set<Integer> pressed = mouse.getPressed();
		
		if (pressed.contains(MouseEvent.BUTTON3)) {
			JPopupMenu menu = new JPopupMenu("Empty Menu");
			menu.add(new JMenuItem("Sample Action"));
			
			System.out.println("Creating context menu at : " + mouse.x() + ", " + mouse.y());
			menu.show(graphicsData.getContainer(), mouse.x(), mouse.y());
		}
	}
}
