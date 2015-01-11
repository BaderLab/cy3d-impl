package org.baderlab.cy3d.internal.input.handler.commands;

import java.awt.Point;

import javax.swing.JPopupMenu;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.input.handler.MouseCommand;
import org.baderlab.cy3d.internal.task.PopupMenuCreator;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

public class PopupMenuMouseCommand implements MouseCommand {

	private PopupMenuCreator popupMenuCreator = null;
	private final GraphicsData graphicsData;
	
	public PopupMenuMouseCommand(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}

	@Override
	public void clicked(int x, int y) {
		// This is kind of a hack, but we have to convert BACK to window coordinates, this at least keeps a consistent interface.
		Point p = new Point(x, y);
		graphicsData.getPixelConverter().convertToWindowUnits(p);
		
		if (popupMenuCreator == null) {
			popupMenuCreator = new PopupMenuCreator(graphicsData.getTaskManager());
		}
		
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		CyNode node = networkView.getModel().getNode(graphicsData.getSelectionData().getHoverNodeIndex());
		CyEdge edge = networkView.getModel().getEdge(graphicsData.getSelectionData().getHoverEdgeIndex());
		
			
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
			popupMenu.show(graphicsData.getContainer(), p.x, p.y);
		}
	}

	
	@Override
	public void pressed(int x, int y) {
	}

	@Override
	public void dragged(int x, int y) {
	}

	@Override
	public void released(int x, int y) {
	}
	
	@Override
	public MouseCommand modify() {
		return this;
	}
}
