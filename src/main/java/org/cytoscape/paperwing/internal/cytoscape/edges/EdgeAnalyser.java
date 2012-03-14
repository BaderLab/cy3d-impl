package org.cytoscape.paperwing.internal.cytoscape.edges;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

/**
 * This class is responsible for analyzing the current set of edges in the network and
 * generate edge coordinate data for use with rendering the edges.
 */
public class EdgeAnalyser {
	
	/** A set of {@link AugmentedEdgeContainer} objects containing extra generated data relating to each
	 * edge as well as a reference to the edge it contains
	 */
	private Collection<AugmentedEdgeContainer> edgeContainers;
	
	/** The frame number that the generated edge data is current for */
	private Long currentFrame;
	
	// Analyze the network to obtain whether each edge is connecting 2 nodes that
	// are already connected by other nodes
	// Maybe add an optimization so we only have to re-analyze the network each time it changes?
	private Set<EdgeViewContainer> analyzeEdges(CyNetworkView networkView, double distanceScale) {
		
		// Create the set of containers to be returned
		Set<EdgeViewContainer> edgeViewContainers = new HashSet<EdgeViewContainer>(
				networkView.getModel().getEdgeCount());
		
		// This map maps each node-pair identifier to the number of edges between that pair of nodes
		// The identifier is: max(sourceIndex, targetIndex) * nodeCount + min(sourceIndex, targetIndex)
		Map<Long, Integer> pairCoincidenceCount = new HashMap<Long, Integer>(
				networkView.getModel().getNodeCount());
		long identifier;
		int sourceIndex, targetIndex, edgeNumber;
		int nodeCount = networkView.getModel().getNodeCount();
		CyEdge edge;
		
		boolean selfEdge;
		
		for (View<CyEdge> edgeView : networkView.getEdgeViews()) {			
			edge = edgeView.getModel();
			
			// Assign an identifier to each pair of nodes
			sourceIndex = edge.getSource().getIndex();
			targetIndex = edge.getTarget().getIndex();
			
			if (sourceIndex >= targetIndex) {
				identifier = (long) nodeCount * sourceIndex + targetIndex;
			} else {
				identifier = (long) nodeCount * targetIndex + sourceIndex;
			}
			
			// Assign a value that represents how many edges have been found between this pair
			if (!pairCoincidenceCount.containsKey(identifier)) {
				edgeNumber = 1;
			} else {
				edgeNumber = pairCoincidenceCount.get(identifier) + 1;
			}
			
			pairCoincidenceCount.put(identifier, edgeNumber);
			
			// Check if the edge leads from a node to itself
			if (sourceIndex == targetIndex) {
				selfEdge = true;
			} else {
				selfEdge = false;
			}
			
			EdgeViewContainer container = new EdgeViewContainer();
			container.edgeView = edgeView;
			container.pairIdentifier = identifier;
			container.edgeNumber = edgeNumber;
			container.selfEdge = selfEdge;
			
			container.start = obtainCoordinates(edge.getSource(), networkView, distanceScale);
			container.end = obtainCoordinates(edge.getTarget(), networkView, distanceScale);
			
			edgeViewContainers.add(container);
		}
		
		// Update the value for the total number of edges between this pair of nodes
		for (EdgeViewContainer container : edgeViewContainers) {
			container.totalCoincidentEdges = pairCoincidenceCount.get(container.pairIdentifier);
			
			// If there was only 1 edge for that pair of nodes, make it a straight edge
			if (container.totalCoincidentEdges == 1 && !container.selfEdge) {
				container.straightEdge = true;
			}
		}
		
		return edgeViewContainers;
	}
}
