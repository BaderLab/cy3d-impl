package org.cytoscape.paperwing.internal.data;

import org.cytoscape.paperwing.internal.geometric.Quadrilateral;
import org.cytoscape.paperwing.internal.geometric.Vector3;

public class CoordinatorData {
	private Quadrilateral bounds;
	
	// This class only works with copies of Vector3 objects
	public CoordinatorData() {
		bounds = new Quadrilateral();
	}

	public void setBounds(Quadrilateral bounds) {
		this.bounds.set(bounds);
	}

	public Quadrilateral getBounds() {
		return bounds;
	}
}
