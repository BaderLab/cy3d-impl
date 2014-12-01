package org.baderlab.cy3d.internal.cytoscape.edges;

import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.tools.PairIdentifier;
import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.View;

/**
 * A container class for View<CyEdge> objects that also contains extra information about the edge,
 * such as whether the edge is part of a series of edges that connect the same pair of nodes, and
 * edge coordinates that are used for rendering segmented edges.
 */
public class AugmentedEdgeContainer {
	private View<CyEdge> edgeView;
	
	// Identifies the pair of nodes that the edge connects
	private PairIdentifier pairIdentifier = null;
	
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
	
	// Does this edge have enough length to be drawn?
	private boolean sufficientLength;
	
	private Vector3 start;
	private Vector3 end;
	
	private Vector3[] coordinates;

	public View<CyEdge> getEdgeView() {
		return edgeView;
	}

	public void setEdgeView(View<CyEdge> edgeView) {
		this.edgeView = edgeView;
	}

	public PairIdentifier getPairIdentifier() {
		return pairIdentifier;
	}

	public void setPairIdentifier(PairIdentifier pairIdentifier) {
		this.pairIdentifier = pairIdentifier;
	}

	public int getEdgeNumber() {
		return edgeNumber;
	}

	public void setEdgeNumber(int edgeNumber) {
		this.edgeNumber = edgeNumber;
	}

	public int getTotalCoincidentEdges() {
		return totalCoincidentEdges;
	}

	public void setTotalCoincidentEdges(int totalCoincidentEdges) {
		this.totalCoincidentEdges = totalCoincidentEdges;
	}

	public boolean isSelfEdge() {
		return selfEdge;
	}

	public void setSelfEdge(boolean selfEdge) {
		this.selfEdge = selfEdge;
	}

	public boolean isStraightEdge() {
		return straightEdge;
	}

	public void setStraightEdge(boolean straightEdge) {
		this.straightEdge = straightEdge;
	}

	public void setSufficientLength(boolean sufficientLength) {
		this.sufficientLength = sufficientLength;
	}

	public boolean isSufficientLength() {
		return sufficientLength;
	}
	
	public Vector3[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Vector3[] coordinates) {
		this.coordinates = coordinates;
	}
	
	public void setStart(Vector3 start) {
		this.start = start;
	}

	public Vector3 getStart() {
		return start;
	}

	public void setEnd(Vector3 end) {
		this.end = end;
	}

	public Vector3 getEnd() {
		return end;
	}



	
	
	
}
