package org.baderlab.cy3d.internal.data;

import org.baderlab.cy3d.internal.coordinator.ViewingCoordinator;
import org.baderlab.cy3d.internal.geometric.Quadrilateral;
import org.baderlab.cy3d.internal.graphics.RenderEventListener;

/**
 * A {@link CoordinatorData} object responsible for storing data related to
 * the relevant {@link ViewingCoordinator}, ie. it stores the data related
 * to coordination with another {@link RenderEventListener} object, such as in the
 * relationship between bird's eye and main window rendering objects.
 */

public class CoordinatorData {
	private Quadrilateral nearBounds;
	private Quadrilateral farBounds;
	
	private boolean boundsManuallyChanged;
	private boolean initialBoundsMatched;
	
	// This class only works with copies of Vector3 objects
	public CoordinatorData() {
		nearBounds = new Quadrilateral();
		farBounds = new Quadrilateral();
		
		boundsManuallyChanged = false;
		initialBoundsMatched = false;
	}

	public void setNearBoundsTo(Quadrilateral nearBounds) {
		this.nearBounds.set(nearBounds);
	}

	public Quadrilateral getNearBounds() {
		return nearBounds;
	}

	public void setFarBoundsTo(Quadrilateral backBounds) {
		this.farBounds.set(backBounds);
	}

	public Quadrilateral getFarBounds() {
		return farBounds;
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
}
