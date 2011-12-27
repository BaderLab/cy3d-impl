package org.cytoscape.paperwing.internal.graphics;

import java.util.LinkedHashSet;
import java.util.Set;

public class PickingData {
	private Set<Integer> pickedNodeIndices;
	private Set<Integer> pickedEdgeIndices;
	
	private int closestPickedNodeIndex;
	private int closestPickedEdgeIndex;
	
	public PickingData() {
		pickedNodeIndices = new LinkedHashSet<Integer>();
		pickedEdgeIndices = new LinkedHashSet<Integer>();
	}
	
	public Set<Integer> getPickedNodeIndices() {
		return pickedNodeIndices;
	}
	
	public void setPickedNodeIndices(Set<Integer> pickedNodeIndices) {
		this.pickedNodeIndices = pickedNodeIndices;
	}
	
	public Set<Integer> getPickedEdgeIndices() {
		return pickedEdgeIndices;
	}
	
	public void setPickedEdgeIndices(Set<Integer> pickedEdgeIndices) {
		this.pickedEdgeIndices = pickedEdgeIndices;
	}
	
	public int getClosestPickedNodeIndex() {
		return closestPickedNodeIndex;
	}
	
	public void setClosestPickedNodeIndex(int closestPickedNodeIndex) {
		this.closestPickedNodeIndex = closestPickedNodeIndex;
	}
	
	public int getClosestPickedEdgeIndex() {
		return closestPickedEdgeIndex;
	}
	
	public void setClosestPickedEdgeIndex(int closestPickedEdgeIndex) {
		this.closestPickedEdgeIndex = closestPickedEdgeIndex;
	}
	
}
