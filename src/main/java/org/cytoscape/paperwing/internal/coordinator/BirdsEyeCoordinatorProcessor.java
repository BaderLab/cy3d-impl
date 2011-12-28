package org.cytoscape.paperwing.internal.coordinator;

import org.cytoscape.paperwing.internal.data.CoordinatorData;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.utility.GraphicsUtility;
import org.cytoscape.paperwing.internal.utility.SimpleCamera;

public class BirdsEyeCoordinatorProcessor implements CoordinatorProcessor {

	@Override
	public void initializeCoordinator(ViewingCoordinator coordinator,
			GraphicsData graphicsData) {
		
		coordinator.claimBirdsEye();
	}

	@Override
	public void extractData(ViewingCoordinator coordinator,
			GraphicsData graphicsData) {
		
		if (coordinator.isMainClaimed()) {
			
			// Initialize bounds if not done so
			if (!coordinator.isBoundsInitialized()) {
				graphicsData.getCoordinatorData().setBounds(coordinator.getCurrentBirdsEyeBounds());
			}
			
			SimpleCamera camera = graphicsData.getCamera();
			CoordinatorData coordinatorData = graphicsData.getCoordinatorData();
			
			// Check if bounds have changed
			if (coordinator.compareBirdsEyeBoundsChanged(coordinatorData.getBounds())) {
				coordinator.updateBirdsEyeBounds(coordinatorData.getBounds());
			
			// Check if the main camera moved
			} else if (coordinator.mainCameraChanged()) {
				coordinatorData.setBounds(ViewingCoordinator.extractBounds(coordinator.getCurrentMainCamera(),
						coordinator.getMainVerticalFov(), coordinator.getMainAspectRatio()));
			
				// Update the birds eye view camera
				camera.copyOrientation(coordinator.getCurrentMainCamera());
				
				Vector3 networkCenter = GraphicsUtility.findNetworkCenter(graphicsData.getNetworkView(), graphicsData.getDistanceScale());
				camera.moveTo(networkCenter);
				
				Vector3 farthestNode = GraphicsUtility.findFarthestNodeFromCenter(graphicsData.getNetworkView(), networkCenter, graphicsData.getDistanceScale());
				
				double newDistance = farthestNode.distance(networkCenter);
				Vector3 offset = camera.getDirection().multiply(-newDistance);
				
				camera.moveTo(camera.getPosition().plus(offset));
				camera.setDistance(newDistance);
				
				coordinator.updateMainCamera();
			}
		}

	}

	@Override
	public void unlinkCoordinator(ViewingCoordinator coordinator) {
		coordinator.unlinkBirdsEye();
	}

}
