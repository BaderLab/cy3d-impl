package org.baderlab.cy3d.internal.eventbus;

import java.util.Collection;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;

public class FitInViewEvent {

	private final Collection<? extends View<CyNode>> selectedNodeViews;
	
	public FitInViewEvent(Collection<? extends View<CyNode>> selectedNodeViews) {
		this.selectedNodeViews = selectedNodeViews;
	}
	
	public Collection<? extends View<CyNode>> getSelectedNodeViews() {
		return selectedNodeViews;
	}
	
}
