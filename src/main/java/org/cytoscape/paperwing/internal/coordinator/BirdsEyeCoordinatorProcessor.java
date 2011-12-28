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
			
			if (coordinator.mainCameraChanged()) {
				coordinatorData.setBounds(ViewingCoordinator.extractBounds(coordinator.getCurrentMainCamera(),
						coordinator.getMainVerticalFov(), coordinator.getMainAspectRatio()));
			
				System.out.println("New Bounds: " + ViewingCoordinator.extractBounds(coordinator.getCurrentMainCamera(),
						coordinator.getMainVerticalFov(), coordinator.getMainAspectRatio()));
				
				// Update the birds eye view camera
				camera.copyOrientation(coordinator.getCurrentMainCamera());
				
				Vector3 networkCenter = GraphicsUtility.findNetworkCenter(graphicsData.getNetworkView(), graphicsData.getDistanceScale());
				camera.moveTo(networkCenter);
				
				Vector3 farthestNode = GraphicsUtility.findFarthestNodeFromCenter(graphicsData.getNetworkView(), networkCenter, graphicsData.getDistanceScale());
				
				double newDistance = farthestNode.distance(networkCenter);
				
				// Further increase the distance needed
				newDistance *= 4;
				
				System.out.println("NewDistance: " + newDistance);
				Vector3 offset = camera.getDirection().multiply(-newDistance);
				
				System.out.println("NetworkCenter: " + networkCenter);
				System.out.println("FarthestNode: " + farthestNode);
				System.out.println("Map camera new position: " + networkCenter.plus(offset));				

				camera.moveTo(networkCenter.plus(offset));
				camera.setDistance(newDistance);
				
				System.out.println("Camera Displacement from center: " + camera.getPosition().distance(networkCenter));
				
				coordinator.updateMainCamera();
			} else if (coordinator.compareBirdsEyeBoundsChanged(coordinatorData.getBounds())) {
				coordinator.updateBirdsEyeBounds(coordinatorData.getBounds());
			}
		}

	}

	@Override
	public void unlinkCoordinator(ViewingCoordinator coordinator) {
		coordinator.unlinkBirdsEye();
	}

}
