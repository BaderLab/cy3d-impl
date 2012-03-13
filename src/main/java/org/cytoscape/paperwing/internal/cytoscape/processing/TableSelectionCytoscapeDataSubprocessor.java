package org.cytoscape.paperwing.internal.cytoscape.processing;

import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.data.GraphicsSelectionData;
import org.cytoscape.paperwing.internal.tools.NetworkToolkit;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * This {@link CytoscapeDataSubprocessor} is used to update data in the relevant {@link CyTable}
 * objects to match the currently selected nodes and edges.
 */
public class TableSelectionCytoscapeDataSubprocessor implements CytoscapeDataSubprocessor {
	
	@Override
	public void processCytoscapeData(GraphicsData graphicsData) {
		
		// Note: For the below method, Ding does not fill in selected states, so for now the 3d renderer will not do so either.
		// initializeTableSelectionState(graphicsData);
		
		// Update CyTable with the currently selected set of nodes and edges
		processSelectionData(graphicsData);
		
		processUpdateSelected(graphicsData);
	}
	
	// Performs selection in Cytoscape data objects, such as CyTable
	private void processSelectionData(GraphicsData graphicsData) {
		
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
	
	// Checks if nodes and edges were made to be selected by other components of Cytoscape
	private void processUpdateSelected(GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		Set<Integer> selectedNodeIndices = graphicsData.getSelectionData().getSelectedNodeIndices();
		Set<Integer> selectedEdgeIndices = graphicsData.getSelectionData().getSelectedEdgeIndices();
		
		for (View<CyNode> nodeView : networkView.getNodeViews()) {
			if (nodeView.getVisualProperty(BasicVisualLexicon.NODE_SELECTED)) {
				selectedNodeIndices.add(nodeView.getModel().getIndex());
			}
		}
		
		for (View<CyEdge> edgeView : networkView.getEdgeViews()) {
			if (edgeView.getVisualProperty(BasicVisualLexicon.EDGE_SELECTED)) {
				selectedEdgeIndices.add(edgeView.getModel().getIndex());
			}
		}
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
			NetworkToolkit.setNodeSelected(node.getIndex(), networkView, false);
		}
		
		for (CyEdge edge : network.getEdgeList()) {
			NetworkToolkit.setEdgeSelected(edge.getIndex(), networkView, false);
		}
	}
}
