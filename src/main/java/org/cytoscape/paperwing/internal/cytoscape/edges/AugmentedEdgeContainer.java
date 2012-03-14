package org.cytoscape.paperwing.internal.cytoscape.edges;

import org.cytoscape.model.CyEdge;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.view.model.View;

/**
 * A container class for View<CyEdge> objects that also contains extra information about the edge,
 * such as whether the edge is part of a series of edges that connect the same pair of nodes, and
 * edge coordinates that are used for rendering segmented edges.
 */
public class AugmentedEdgeContainer {
	private View<CyEdge> edgeView;
	
	// Identifies the pair of nodes that the edge connects
	private long pairIdentifier;
	
	// The index of this edge compared to all the other edges that connect the same pair
	// of nodes. If this is the first of 7 edges that connect the same pair of nodes, its
	// edgeNumber would be set to 1.
	private int edgeNumber;
	
	// The total number of edges that connect the pair of nodes.
	private int totalCoincidentEdges;
	
	// Does this edge direct from a node to itself?
	private boolean selfEdge;
	
	// Should this edge be a straight edge because it is the only edge between 2 nodes?
	private boolean straightEdge;
	
	private Vector3[] coordinates;
}
