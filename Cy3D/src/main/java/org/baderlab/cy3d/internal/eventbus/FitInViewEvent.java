package org.baderlab.cy3d.internal.eventbus;

import java.util.Collection;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.ReadableView;

public class FitInViewEvent {

	private final Collection<? extends ReadableView<CyNode>> selectedNodeViews;
	
	public FitInViewEvent(Collection<? extends ReadableView<CyNode>> selectedNodeViews) {
		this.selectedNodeViews = selectedNodeViews;
	}
	
	public Collection<? extends ReadableView<CyNode>> getSelectedNodeViews() {
		return selectedNodeViews;
	}
	
}
