package org.baderlab.cy3d.internal.input.handler;

import java.util.Collection;

import org.baderlab.cy3d.internal.camera.Camera;
import org.baderlab.cy3d.internal.camera.CameraPosition;
import org.baderlab.cy3d.internal.camera.OriginOrbitCamera;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.eventbus.BirdsEyeCameraChangeEvent;
import org.baderlab.cy3d.internal.eventbus.BoundingBoxUpdateEvent;
import org.baderlab.cy3d.internal.eventbus.FitInViewEvent;
import org.baderlab.cy3d.internal.eventbus.MainCameraChangeEvent;
import org.baderlab.cy3d.internal.eventbus.ShowLabelsEvent;
import org.baderlab.cy3d.internal.eventbus.UpdateNetworkViewEvent;
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
		graphicsData.getEventBus().post(new UpdateNetworkViewEvent());
	}
	
	
	@Subscribe
	public void handleFitInViewEvent(FitInViewEvent e) {
		Camera camera = graphicsData.getCamera();
		Collection<View<CyNode>> selectedNodeViews = e.getSelectedNodeViews();
		NetworkToolkit.fitInView(camera, selectedNodeViews, 180.0, 2.3, 1.8);
	}
	
	
	@Subscribe
	public void handleBirdsEyeCameraChangeEvent(BirdsEyeCameraChangeEvent e) {
		CameraPosition birdsEyeCamera = e.getNewCameraPosition();
		OriginOrbitCamera mainCamera = graphicsData.getCamera();
		
		// maintain constant distance
		double distance = mainCamera.getDistance();
		mainCamera.moveTo(birdsEyeCamera.getPosition(), birdsEyeCamera.getUp());
		mainCamera.setDistance(distance);
	}
	
	
	/**
	 * Reply to a birds eye camera change by telling the birds eye view to update
	 * its bounding box. Message passing style!
	 */
	@Subscribe
	public void handleBirdsEyeCameraChangeReplyWithBoundingBoxUpdate(BirdsEyeCameraChangeEvent e) {
		graphicsData.getEventBus().post(new BoundingBoxUpdateEvent(graphicsData.getCamera()));
	}
	
	
	/**
	 * Respond to our own camera change event by telling the birds eye view
	 * to update its bounding box.
	 */
	@Subscribe
	public void handleMainCameraChangeEvent(MainCameraChangeEvent e) {
		graphicsData.getEventBus().post(new BoundingBoxUpdateEvent(graphicsData.getCamera()));
	}
}
