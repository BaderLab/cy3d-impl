package org.cytoscape.paperwing.internal.cytoscape.view;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;

public class WindEdgeView extends VisualPropertyKeeper<CyEdge> {

	private CyEdge edge;
	private Long SUID;
	
	public WindEdgeView(CyEdge edge, Long SUID) {
		this.edge = edge;
		this.SUID = SUID;		
	}
	
	@Override
	public long getSUID() {
		return SUID;
	}

	@Override
	public CyEdge getModel() {
		return edge;
	}
}
