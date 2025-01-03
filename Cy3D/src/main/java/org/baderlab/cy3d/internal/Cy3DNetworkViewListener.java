package org.baderlab.cy3d.internal;

import org.baderlab.cy3d.internal.cytoscape.view.Cy3DVisualLexicon;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.eventbus.FitInViewEvent;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewListener;

public class Cy3DNetworkViewListener implements CyNetworkViewListener {
	
	private final CyNetworkView netView;
	private final GraphicsData graphicsData;

	
	public Cy3DNetworkViewListener(GraphicsData graphicsData, CyNetworkView netView) {
		this.graphicsData = graphicsData;
		this.netView = netView;
	}
	
	
	@Override
	public void handleFitContent() {
		var snapshot = netView.createSnapshot();
		if(snapshot != null) {
			fire(new FitInViewEvent(snapshot.getNodeViews()));
		}
	}
	
	@Override
	public void handleFitSelected() {
		var snapshot = netView.createSnapshot();
		if(snapshot != null) {
			var selectedNodes = snapshot.getTrackedNodes(Cy3DVisualLexicon.CONFIG_PROP_SELECTED_NODES);
			if(!selectedNodes.isEmpty()) {
				fire(new FitInViewEvent(selectedNodes));
			}
		}
	}
	
	private void fire(FitInViewEvent evt) {
		graphicsData.getEventBus().post(evt);
	}
	
	@Override
	public void handleDispose() {
		netView.removeNetworkViewListener(this);
	}
}
