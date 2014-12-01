package org.baderlab.cy3d.internal.coordinator;

import java.util.Collection;

import org.baderlab.cy3d.internal.data.CoordinatorData;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.tools.NetworkToolkit;
import org.baderlab.cy3d.internal.tools.SimpleCamera;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;

public class BirdsEyeCoordinatorProcessor implements CoordinatorProcessor {

	@Override
	public void initializeCoordinator(ViewingCoordinator coordinator,
			GraphicsData graphicsData) {
		
		coordinator.claimBirdsEye();
		coordinator.setInitialBoundsMatched(false);
	}

	@Override
	public void extractData(ViewingCoordinator coordinator,
			GraphicsData graphicsData) {
		
		coordinator.setBirdsEyeAnimatorControl(graphicsData.getAnimatorControl());
		
		if (coordinator.isMainClaimed()) {
			CoordinatorData coordinatorData = graphicsData.getCoordinatorData();
			
			// Initialize bounds if not done so
			if (coordinator.isInitialMainCameraInitialized()
					&& !coordinator.isInitialBoundsMatched()) {
				
				coordinatorData.setNearBoundsTo(coordinator.getMainCameraBounds());
				coordinatorData.setFarBoundsTo(coordinator.calculateBackBounds());
				coordinatorData.setInitialBoundsMatched(true);
				coordinator.setInitialBoundsMatched(true);
			}
			
			// This is the regular case
			if (coordinator.isInitialBoundsMatched()) {
				
				// User moved box, this case takes priority
				if (coordinatorData.isBoundsManuallyChanged()) {
					
					// Transfer data to coordinator
					coordinator.setBirdsEyeBoundsCopy(coordinatorData.getNearBounds());
					
					// Use new information to recalculate far bounds
					coordinator.getMainCameraCopy().getPosition().set(
							coordinator.calculateCameraPosition(
									coordinator.getMainCameraCopy().getDirection()));
					coordinatorData.setFarBoundsTo(coordinator.calculateBackBounds());
					
					// Set flag
					coordinator.setBirdsEyeBoundsMoved(true);
					
					// Unset internal flag
					coordinatorData.setBoundsManuallyChanged(false);
					
					
				// User moved the main camera
				} else if (coordinator.isMainCameraMoved()) {
					
					// Obtain data from coordinator
					coordinatorData.setNearBoundsTo(coordinator.calculateNearBounds());
					coordinatorData.setFarBoundsTo(coordinator.calculateBackBounds());
					
					// Unset flag
					coordinator.setMainCameraMoved(false);
				}
				
				// Check if bound recalculation is suggested due to bounds movement
				if (coordinator.isSuggestRecalculateBounds()) {
					
					// Obtain data from coordinator
					coordinatorData.setNearBoundsTo(coordinator.calculateNearBounds());
					coordinatorData.setFarBoundsTo(coordinator.calculateBackBounds());
					
					// Unset flag
					coordinator.setSuggestRecalculateBounds(false);
				}
				
				// Zoom to extents, use appropriate angles
				updateBirdsEyeCamera(coordinator.getMainCameraCopy(), graphicsData);	
			}
			
			// main should check bounds moved first, then perform its own update
		} else {
			
			// Update camera using default angles
			updateBirdsEyeCamera(graphicsData.getCamera(), graphicsData);
		}
	}

	private void updateBirdsEyeCamera(SimpleCamera mainCameraCopy,
			GraphicsData graphicsData) {
		SimpleCamera camera = graphicsData.getCamera();
		
		// Update the birds eye view camera
		camera.set(mainCameraCopy);
		
		/*
	
		Vector3 networkCenter = NetworkToolkit.findNetworkCenter(graphicsData.getNetworkView(), graphicsData.getDistanceScale());
		Vector3 farthestNode = NetworkToolkit.findFarthestNodeFromCenter(graphicsData.getNetworkView(), networkCenter, graphicsData.getDistanceScale());
		
		double newDistance = farthestNode.distance(networkCenter);
		
		// Further increase the distance needed
		newDistance *= 3;
		// newDistance = Math.max(newDistance, coordinator.getCurrentMainCamera().getPosition().distance(networkCenter) * 2);
		newDistance = Math.max(newDistance, 5);
		
//		System.out.println("NewDistance: " + newDistance);
		Vector3 offset = camera.getDirection().multiply(-newDistance);
		
//		System.out.println("NetworkCenter: " + networkCenter);
//		System.out.println("FarthestNode: " + farthestNode);
//		System.out.println("Map camera new position: " + networkCenter.plus(offset));				

		camera.moveTo(networkCenter.plus(offset));
		camera.setDistance(newDistance);
		
		*/
		
		NetworkToolkit.fitInView(camera, graphicsData.getNetworkView().getNodeViews(), graphicsData.getDistanceScale(), 3.0, 5.0);
	}
	

	
	@Override
	public void unlinkCoordinator(ViewingCoordinator coordinator) {
		coordinator.unlinkBirdsEye();
	}

}