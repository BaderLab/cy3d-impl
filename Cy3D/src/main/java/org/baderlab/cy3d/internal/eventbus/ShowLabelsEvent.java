package org.baderlab.cy3d.internal.eventbus;

public class ShowLabelsEvent {

	final boolean showLabels;

	public ShowLabelsEvent(boolean showLabels) {
		this.showLabels = showLabels;
	}
	
	public boolean showLabels() {
		return showLabels;
	}
	
}
