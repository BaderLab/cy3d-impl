package org.cytoscape.paperwing.internal.input;

import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.cytoscape.model.CyEdge;
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
//		if (popupMenuCreator == null) {
//			popupMenuCreator = new PopupMenuCreator(graphicsData.getTaskManager(),
//					graphicsData.getSubmenuTaskManager());
//		}
//		
//		Set<Integer> pressed = mouse.getPressed();
//		
//		CyNetworkView networkView = graphicsData.getNetworkView();
//		
//		CyNode node = networkView.getModel().getNode(graphicsData.getSelectionData().getHoverNodeIndex());
//		CyEdge edge = networkView.getModel().getEdge(graphicsData.getSelectionData().getHoverEdgeIndex());
//		
//		if (pressed.contains(MouseEvent.BUTTON3)) {
//			
//			JPopupMenu popupMenu = null;
//			
//			if (node != null) {
//				View<CyNode> nodeView = networkView.getNodeView(node);;
//				
//				popupMenu = popupMenuCreator.createNodeMenu(nodeView, 
//						networkView, graphicsData.getVisualLexicon(), 
//						graphicsData.getTaskFactoryListener().getNodeViewTaskFactories());
//			} else if (edge != null) {
//				View<CyEdge> edgeView = networkView.getEdgeView(edge);
//				
//				popupMenu = popupMenuCreator.createEdgeMenu(edgeView, 
//						networkView, graphicsData.getVisualLexicon(), 
//						graphicsData.getTaskFactoryListener().getEdgeViewTaskFactories());
//			} else {
//				popupMenu = popupMenuCreator.createNetworkMenu(networkView, 
//						graphicsData.getVisualLexicon(),
//						graphicsData.getTaskFactoryListener().getNetworkViewTaskFactories());
//			}
//			
//			// menu.add(new JMenuItem("Sample Action"));
//			
//			if (popupMenu != null) {
//				popupMenu.show(graphicsData.getContainer(), mouse.x(), mouse.y());
//			}
//		}
//		
	}
}
