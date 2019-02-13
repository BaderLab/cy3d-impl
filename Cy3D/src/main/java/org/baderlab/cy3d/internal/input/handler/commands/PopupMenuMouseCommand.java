package org.baderlab.cy3d.internal.input.handler.commands;

import java.awt.Point;

import javax.swing.JPopupMenu;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.input.handler.MouseCommandAdapter;
import org.baderlab.cy3d.internal.task.PopupMenuCreator;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

public class PopupMenuMouseCommand extends MouseCommandAdapter {

	private PopupMenuCreator popupMenuCreator = null;
	private final GraphicsData graphicsData;
	
	public PopupMenuMouseCommand(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}

	@Override
	public void clicked(int x, int y) {
		if (popupMenuCreator == null) {
			popupMenuCreator = new PopupMenuCreator(graphicsData.getTaskManager());
		}
		
		CyNetworkView networkView = graphicsData.getNetworkSnapshot().getMutableNetworkView();
		long hoverNodeIndex = graphicsData.getSelectionData().getHoverNodeIndex();
		long hoverEdgeIndex = graphicsData.getSelectionData().getHoverEdgeIndex();
		
		View<CyNode> nodeView = getHoverNode(networkView, hoverNodeIndex);
		View<CyEdge> edgeView = getHoverEdge(networkView, hoverEdgeIndex);
		
		JPopupMenu popupMenu = null;
		if (nodeView != null) {
			popupMenu = popupMenuCreator.createNodeMenu(nodeView, 
					networkView, graphicsData.getVisualLexicon(), 
					graphicsData.getTaskFactoryListener().getNodeViewTaskFactories());
		} else if (edgeView != null) {
			popupMenu = popupMenuCreator.createEdgeMenu(edgeView, 
					networkView, graphicsData.getVisualLexicon(), 
					graphicsData.getTaskFactoryListener().getEdgeViewTaskFactories());
		} else {
			popupMenu = popupMenuCreator.createNetworkMenu(networkView, 
					graphicsData.getVisualLexicon(),
					graphicsData.getTaskFactoryListener().getNetworkViewTaskFactories());
		}
		
		if (popupMenu != null) {
			// This is kind of a hack, but we have to convert BACK to window coordinates, this at least keeps a consistent interface.
			Point p = new Point(x, y);
			graphicsData.getPixelConverter().convertToWindowUnits(p);
			popupMenu.show(graphicsData.getContainer(), p.x, p.y);
		}
	}

	private static View<CyNode> getHoverNode(CyNetworkView networkView, long hoverNodeIndex) {
		for(View<CyNode> nodeView : networkView.getNodeViews()) {
			if(nodeView.getSUID().equals(hoverNodeIndex)) {
				return nodeView;
			}
		}
		return null;
	}
	
	private static View<CyEdge> getHoverEdge(CyNetworkView networkView, long hoverEdgeIndex) {
		for(View<CyEdge> edgeView : networkView.getEdgeViews()) {
			if(edgeView.getSUID().equals(hoverEdgeIndex)) {
				return edgeView;
			}
		}
		return null;
	}

}
