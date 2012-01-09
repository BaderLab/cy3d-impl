package org.cytoscape.paperwing.internal.cytoscape.processing;

import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.data.GraphicsSelectionData;
import org.cytoscape.paperwing.internal.tools.NetworkToolkit;
import org.cytoscape.view.model.CyNetworkView;

public class MainCytoscapeDataProcessor implements CytoscapeDataProcessor {

	@Override
	public void processCytoscapeData(GraphicsData graphicsData) {
		
		// Perform on second frame, first frame performs initial drawing
		// TODO: Check if necessary, the ding renderer doesn't do this
//		if (graphicsData.getFramesElapsed() == 1) {
//			initializeTableSelectionState(graphicsData);
//		}
		
		processDeselectedData(graphicsData);
	}

	// Performs deselection in Cytoscape data objects, such as CyTable
	private void processDeselectedData(GraphicsData graphicsData) {
		
		CyNetworkView networkView = graphicsData.getNetworkView();
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		
		Set<Integer> toBeDeselectedNodeIndices = selectionData.getToBeDeselectedNodeIndices();
		Set<Integer> toBeDeselectedEdgeIndices = selectionData.getToBeDeselectedEdgeIndices();
		
		NetworkToolkit.deselectNodes(toBeDeselectedNodeIndices, networkView);
		NetworkToolkit.deselectEdges(toBeDeselectedEdgeIndices, networkView);
		toBeDeselectedNodeIndices.clear();
		toBeDeselectedEdgeIndices.clear();
		
		// Select nodes
		for (int index : selectionData.getSelectedNodeIndices()) {
			if (!NetworkToolkit.checkNodeSelected(index, networkView)) {
				NetworkToolkit.setNodeSelected(index, networkView, true);
			}
		}
		
		// Select edges
		for (int index : selectionData.getSelectedEdgeIndices()) {
			if (!NetworkToolkit.checkEdgeSelected(index, networkView)) {
				NetworkToolkit.setEdgeSelected(index, networkView, true);
			}
		}
	}
	
	// Fills in the missing "selected" boolean values in CyTable
	private void initializeTableSelectionState(GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		CyNetwork network = graphicsData.getNetworkView().getModel();
		
		for (CyNode node : network.getNodeList()) {
			NetworkToolkit.setNodeSelected(node.getIndex(), networkView, false);
		}
		
		for (CyEdge edge : network.getEdgeList()) {
			NetworkToolkit.setEdgeSelected(edge.getIndex(), networkView, false);
		}
	}
	
}
