package org.baderlab.cy3d.internal.input.handler;

import java.util.Collection;

import org.baderlab.cy3d.internal.camera.Camera;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.eventbus.FitInViewEvent;
import org.baderlab.cy3d.internal.eventbus.ShowLabelsEvent;
import org.baderlab.cy3d.internal.tools.NetworkToolkit;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;

import com.google.common.eventbus.Subscribe;

public class MainEventBusListener {

	private final GraphicsData graphicsData;

	public MainEventBusListener(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}
	
	
	@Subscribe
	public void handleShowLabelsEvent(ShowLabelsEvent showLabelsEvent) {
		graphicsData.setShowLabels(showLabelsEvent.showLabels());
		graphicsData.getNetworkView().updateView();
	}
	
	@Subscribe
	public void handleFitInViewEvent(FitInViewEvent e) {
		Camera camera = graphicsData.getCamera();
		Collection<View<CyNode>> selectedNodeViews = e.getSelectedNodeViews();
		NetworkToolkit.fitInView(camera, selectedNodeViews, 180.0, 2.3, 1.8);
	}
	
}
