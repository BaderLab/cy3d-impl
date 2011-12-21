package org.cytoscape.paperwing.internal.graphics;

import org.cytoscape.paperwing.internal.Vector3;

public class CoordinatorData {
	private Vector3 topLeftBound;
	private Vector3 topRightBound;
	private Vector3 bottomLeftBound;
	private Vector3 bottomRightBound;
	
	// This class only works with copies of Vector3 objects
	public CoordinatorData() {
		topLeftBound = new Vector3();
		topRightBound = new Vector3();
		
		bottomLeftBound = new Vector3();
		bottomRightBound = new Vector3();
	}
	
	public Vector3 getTopLeftBound() {
		return new Vector3(topLeftBound);
	}
	
	public void setTopLeftBound(Vector3 topLeftBound) {
		this.topLeftBound.set(topLeftBound);
	}
	
	public Vector3 getTopRightBound() {
		return new Vector3(topRightBound);
	}
	
	public void setTopRightBound(Vector3 topRightBound) {
		this.topRightBound.set(topRightBound);
	}
	
	public Vector3 getBottomLeftBound() {
		return new Vector3(bottomLeftBound);
	}
	
	public void setBottomLeftBound(Vector3 bottomLeftBound) {
		this.bottomLeftBound.set(bottomLeftBound);
	}
	
	public Vector3 getBottomRightBound() {
		return new Vector3(bottomRightBound);
	}
	
	public void setBottomRightBound(Vector3 bottomRightBound) {
		this.bottomRightBound.set(bottomRightBound);
	}
	
}
