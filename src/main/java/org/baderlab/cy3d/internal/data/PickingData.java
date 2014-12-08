package org.baderlab.cy3d.internal.data;

import java.util.LinkedHashSet;
import java.util.Set;

public class PickingData {
	private Set<Long> pickedNodeIndices;
	private Set<Long> pickedEdgeIndices;
	
	private long closestPickedNodeIndex;
	private long closestPickedEdgeIndex;
	
	public PickingData() {
		pickedNodeIndices = new LinkedHashSet<Long>();
		pickedEdgeIndices = new LinkedHashSet<Long>();
	}
	
	public Set<Long> getPickedNodeIndices() {
		return pickedNodeIndices;
	}
	
	public void setPickedNodeIndices(Set<Long> pickedNodeIndices) {
		this.pickedNodeIndices = pickedNodeIndices;
	}
	
	public Set<Long> getPickedEdgeIndices() {
		return pickedEdgeIndices;
	}
	
	public void setPickedEdgeIndices(Set<Long> pickedEdgeIndices) {
		this.pickedEdgeIndices = pickedEdgeIndices;
	}
	
	public long getClosestPickedNodeIndex() {
		return closestPickedNodeIndex;
	}
	
	public void setClosestPickedNodeIndex(long closestPickedNodeIndex) {
		this.closestPickedNodeIndex = closestPickedNodeIndex;
	}
	
	public long getClosestPickedEdgeIndex() {
		return closestPickedEdgeIndex;
	}
	
	public void setClosestPickedEdgeIndex(long closestPickedEdgeIndex) {
		this.closestPickedEdgeIndex = closestPickedEdgeIndex;
	}
	
}
