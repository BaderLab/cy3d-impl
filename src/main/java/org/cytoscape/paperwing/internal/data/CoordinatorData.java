package org.cytoscape.paperwing.internal.data;

import org.cytoscape.paperwing.internal.geometric.Quadrilateral;
import org.cytoscape.paperwing.internal.geometric.Vector3;

public class CoordinatorData {
	private Quadrilateral bounds;
	
	
	private boolean boundsManuallyChanged;
	private boolean initialBoundsMatched;

	public Vector3 lastReportedMainCameraPosition;
	
	// This class only works with copies of Vector3 objects
	public CoordinatorData() {
		bounds = new Quadrilateral();
		
		boundsManuallyChanged = false;
		initialBoundsMatched = false;
		
		lastReportedMainCameraPosition = new Vector3();
	}

	public void setBoundsTo(Quadrilateral bounds) {
		this.bounds.set(bounds);
	}

	public Quadrilateral getBounds() {
		return bounds;
	}

	public void setBoundsManuallyChanged(boolean boundsManuallyChanged) {
		this.boundsManuallyChanged = boundsManuallyChanged;
	}

	public boolean isBoundsManuallyChanged() {
		return boundsManuallyChanged;
	}

	public void setInitialBoundsMatched(boolean initalBoundsMatched) {
		this.initialBoundsMatched = initalBoundsMatched;
	}

	public boolean isInitialBoundsMatched() {
		return initialBoundsMatched;
	}

	public Vector3 getLastReportedMainCameraPosition() {
		return lastReportedMainCameraPosition;
	}

	public void setLastReportedMainCameraPosition(
			Vector3 lastReportedMainCameraPosition) {
		this.lastReportedMainCameraPosition.set(lastReportedMainCameraPosition);
	}
}
