package org.baderlab.cy3d.internal.cytoscape.processing;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.tools.NetworkToolkit;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;

/**
 * This {@link CytoscapeDataSubprocessor} is used to update data in the relevant {@link CyTable}
 * objects to match the currently selected nodes and edges.
 */
public class TableSelectionCytoscapeDataSubprocessor implements CytoscapeDataSubprocessor {
	
	@Override
	public void processCytoscapeData(GraphicsData graphicsData) {
		
	}
	
	/**
	 *  Fills in the missing "selected" boolean values in CyTable.
	 *  
	 *  @param graphicsData The {@link GraphicsData} object containing a reference to the network view.
	 */
	private void initializeTableSelectionState(GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		CyNetwork network = graphicsData.getNetworkView().getModel();
		
		for (CyNode node : network.getNodeList()) {
			NetworkToolkit.setNodeSelected(node.getSUID(), networkView, false);
		}
		
		for (CyEdge edge : network.getEdgeList()) {
			NetworkToolkit.setEdgeSelected(edge.getSUID(), networkView, false);
		}
	}
}
