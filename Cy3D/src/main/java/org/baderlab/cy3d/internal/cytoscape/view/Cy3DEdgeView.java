package org.baderlab.cy3d.internal.cytoscape.view;

import org.cytoscape.model.CyEdge;

public class Cy3DEdgeView extends Cy3DView<CyEdge> {

	private final CyEdge edge;
	
	public Cy3DEdgeView(DefaultValueVault defaultValueVault, CyEdge edge) {
		super(defaultValueVault);
		this.edge = edge;
	}
	
	@Override
	public CyEdge getModel() {
		return edge;
	}
	
}
