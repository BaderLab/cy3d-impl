package org.baderlab.cy3d.internal.input.handler.commands;


import java.util.List;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.input.handler.MouseCommand;
import org.baderlab.cy3d.internal.input.handler.MouseCommandAdapter;
import org.baderlab.cy3d.internal.tools.NetworkToolkit;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.view.model.CyNetworkView;

/**
 * Deselects currently selected nodes and edges before selecting new ones.
 * 
 * @author mkucera
 */
public class SelectionMouseCommand extends MouseCommandAdapter {

	private final GraphicsData graphicsData;
	private final SelectionAddMouseCommand addCommand;
	
	public SelectionMouseCommand(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
		this.addCommand = new SelectionAddMouseCommand(graphicsData);
	}

	@Override
	public void clicked(int x, int y) {
		deselectOther();
		addCommand.clicked(x, y);
	}
	
	@Override
	public void pressed(int x, int y) {
		deselectOther();
		addCommand.pressed(x, y);
	}
	
	@Override
	public void dragged(int x, int y) {
		addCommand.dragged(x, y);
	}

	@Override
	public void released(int x, int y) {
		addCommand.released(x, y);
	}
	
	@Override
	public void moved(int x, int y) {
		addCommand.moved(x, y);
	}
	
	private void deselectOther() {
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		// Deselect currently selected nodes
		List<CyNode> selectedNodes = CyTableUtil.getNodesInState(networkView.getModel(), "selected", true);
		for (CyNode node : selectedNodes) {
			NetworkToolkit.setNodeSelected(node.getSUID(), networkView, false);
		}

		// Deselect currently selected edges
		List<CyEdge> selectedEdges = CyTableUtil.getEdgesInState(networkView.getModel(), "selected", true);
		for (CyEdge edge : selectedEdges) {
			NetworkToolkit.setEdgeSelected(edge.getSUID(), networkView, false);
		}
	}
	
	@Override
	public MouseCommand modify() {
		return addCommand;
	}

	
}
