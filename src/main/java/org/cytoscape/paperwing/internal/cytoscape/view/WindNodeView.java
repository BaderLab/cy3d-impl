package org.cytoscape.paperwing.internal.cytoscape.view;

import java.util.HashMap;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;

public class WindNodeView extends VisualPropertyKeeper<CyNode> {

	private CyNode node;
	private Long suid;
	
	public WindNodeView(CyNode node, Long suid) {
		this.node = node;
		this.suid = suid;		
	}
	
	@Override
	public Long getSUID() {
		return suid;
	}

	@Override
	public CyNode getModel() {
		return node;
	}

}
