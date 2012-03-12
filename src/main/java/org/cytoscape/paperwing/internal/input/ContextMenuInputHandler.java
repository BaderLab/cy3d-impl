package org.cytoscape.paperwing.internal.input;

import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.cytoscape.model.CyNode;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.task.PopupMenuCreator;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

/**
 * Input handler responsible for creating the right-click context menu.
 */
public class ContextMenuInputHandler implements InputHandler {

	private PopupMenuCreator popupMenuCreator = null;
	
	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {

		processNetworkRightClickMenu(mouse, graphicsData);
	}

	private void processNetworkRightClickMenu(MouseMonitor mouse, GraphicsData graphicsData){
		if (popupMenuCreator == null) {
			popupMenuCreator = new PopupMenuCreator(graphicsData.getTaskManager());
		}
		
		Set<Integer> pressed = mouse.getPressed();
		
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		int nodeIndex = graphicsData.getSelectionData().getHoverNodeIndex();
		CyNode node = networkView.getModel().getNode(nodeIndex);
		
		View<CyNode> nodeView = null;
		if (node != null) {
			nodeView = networkView.getNodeView(node);
		}
		
		if (pressed.contains(MouseEvent.BUTTON3) && nodeView != null) {
			
			
			
			JPopupMenu popupMenu = popupMenuCreator.createNodeMenu(nodeView, 
					networkView, graphicsData.getVisualLexicon(), 
					graphicsData.getTaskFactoryListener().getNodeViewTaskFactories());
			
			
			// menu.add(new JMenuItem("Sample Action"));
			
			System.out.println("Creating context menu at : " + mouse.x() + ", " + mouse.y());
			popupMenu.show(graphicsData.getContainer(), mouse.x(), mouse.y());
		}
	}
}
