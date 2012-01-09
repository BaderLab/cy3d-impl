package org.cytoscape.paperwing.internal.cytoscape.view;

import java.util.HashMap;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;

public class WindNodeView extends VisualPropertyKeeper<CyNode> {

	private CyNode node;
	private Long SUID;
	
	public WindNodeView(CyNode node, Long SUID) {
		this.node = node;
		this.SUID = SUID;		
	}
	
	@Override
	public long getSUID() {
		return SUID;
	}

	@Override
	public CyNode getModel() {
		return node;
	}

}
