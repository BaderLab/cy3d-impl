package org.baderlab.cy3d.internal.input.handler;

import java.util.Collection;

import org.baderlab.cy3d.internal.camera.Camera;
import org.baderlab.cy3d.internal.camera.CameraPosition;
import org.baderlab.cy3d.internal.camera.OriginOrbitCamera;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.eventbus.FitInViewEvent;
import org.baderlab.cy3d.internal.eventbus.MainCameraChangeEvent;
import org.baderlab.cy3d.internal.tools.NetworkToolkit;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;

import com.google.common.eventbus.Subscribe;

/**
 * Updates the birds-eye camera whenever the main camera changes 
 * by listening for camera change events on the event bus.
 * 
 * @author mkucera
 */
public class BirdsEyeEventBusListener {
	
	// The GraphicsData from the birds-eye renderer.
	private final GraphicsData graphicsData;
	
	
	public BirdsEyeEventBusListener(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}
	
	
	@Subscribe
	public void handleFitInViewEvent(FitInViewEvent e) {
		Camera camera = graphicsData.getCamera();
		
		// ignore selected node views, always use all of them
		Collection<View<CyNode>> nodeViews = graphicsData.getNetworkView().getNodeViews(); 
		
		NetworkToolkit.fitInView(camera, nodeViews, GraphicsData.DISTANCE_SCALE, 3.0, 5.0);
	}

	@Subscribe
	public void handleMainCameraChangeEvent(MainCameraChangeEvent e) {
		CameraPosition mainCamera = e.getNewCameraPosition();
		
		OriginOrbitCamera birdsEyeCamera = graphicsData.getCamera();
		
		// maintain constant distance, originally set by the FitInViewEvent
		double distance = birdsEyeCamera.getDistance();
		birdsEyeCamera.moveTo(mainCamera.getPosition(), mainCamera.getUp());
		birdsEyeCamera.setDistance(distance);
	}
	
}
