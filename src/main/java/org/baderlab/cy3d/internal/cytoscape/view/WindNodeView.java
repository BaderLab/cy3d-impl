package org.baderlab.cy3d.internal.cytoscape.view;

import java.util.HashMap;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;

public class WindNodeView extends VisualPropertyKeeper<CyNode> {

	private CyNode node;
	private Long suid;
	private DefaultValueVault defaultValueVault;
	
	public WindNodeView(DefaultValueVault defaultValueVault, 
			CyNode node) {
		this.node = node;
		this.suid = SUIDFactory.getNextSUID();
		this.defaultValueVault = defaultValueVault;
	}
	
	@Override
	public Long getSUID() {
		return suid;
	}

	@Override
	public CyNode getModel() {
		return node;
	}

	@Override
	public <T> T getVisualProperty(VisualProperty<T> visualProperty) {
		T value = super.getVisualProperty(visualProperty);
		
		if (value != null) {
			// If we were given an explicit value, return it
			return value;
		} else {
			// Otherwise, return the default value
			return defaultValueVault.getDefaultValue(visualProperty);
		}
	}
}
