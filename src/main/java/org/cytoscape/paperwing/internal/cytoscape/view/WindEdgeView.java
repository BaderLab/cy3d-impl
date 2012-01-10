package org.cytoscape.paperwing.internal.cytoscape.view;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;

public class WindEdgeView extends VisualPropertyKeeper<CyEdge> {

	private CyEdge edge;
	private Long suid;
	
	public WindEdgeView(CyEdge edge, Long suid) {
		this.edge = edge;
		this.suid = suid;		
	}
	
	@Override
	public long getSUID() {
		return suid;
	}

	@Override
	public CyEdge getModel() {
		return edge;
	}
}
