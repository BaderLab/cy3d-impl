package org.baderlab.cy3d.internal.input;

import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.JPopupMenu;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.task.PopupMenuCreator;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

/**
 * Input handler responsible for creating the right-click context menu.
 */
public class ContextMenuInputHandler implements InputHandler {

	private PopupMenuCreator popupMenuCreator = null;
	
	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse, GraphicsData graphicsData) {

		processNetworkRightClickMenu(mouse, graphicsData);
	}

	private void processNetworkRightClickMenu(MouseMonitor mouse, GraphicsData graphicsData){
		if (popupMenuCreator == null) {
			popupMenuCreator = new PopupMenuCreator(graphicsData.getTaskManager());
		}
		
		Set<Integer> pressed = mouse.getPressed();
		
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		CyNode node = networkView.getModel().getNode(graphicsData.getSelectionData().getHoverNodeIndex());
		CyEdge edge = networkView.getModel().getEdge(graphicsData.getSelectionData().getHoverEdgeIndex());
		
		if (pressed.contains(MouseEvent.BUTTON3)) {
			
			JPopupMenu popupMenu = null;
			
			if (node != null) {
				View<CyNode> nodeView = networkView.getNodeView(node);
				
				popupMenu = popupMenuCreator.createNodeMenu(nodeView, 
						networkView, graphicsData.getVisualLexicon(), 
						graphicsData.getTaskFactoryListener().getNodeViewTaskFactories());
			} else if (edge != null) {
				View<CyEdge> edgeView = networkView.getEdgeView(edge);
				
				popupMenu = popupMenuCreator.createEdgeMenu(edgeView, 
						networkView, graphicsData.getVisualLexicon(), 
						graphicsData.getTaskFactoryListener().getEdgeViewTaskFactories());
			} else {
				popupMenu = popupMenuCreator.createNetworkMenu(networkView, 
						graphicsData.getVisualLexicon(),
						graphicsData.getTaskFactoryListener().getNetworkViewTaskFactories());
			}
			
			if (popupMenu != null) {
				int[] coords = new int[] {mouse.x(), mouse.y()};
				graphicsData.getPixelConverter().convertToWindowUnits(coords);
				popupMenu.show(graphicsData.getContainer(), coords[0], coords[1]);
			}
		}
		
	}
}
