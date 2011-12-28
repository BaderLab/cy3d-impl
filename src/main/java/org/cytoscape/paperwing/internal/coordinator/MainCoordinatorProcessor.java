package org.cytoscape.paperwing.internal.coordinator;

import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.utility.SimpleCamera;

public class MainCoordinatorProcessor implements CoordinatorProcessor {
	
	// Read-only for GraphicsData
	@Override
	public void initializeCoordinator(ViewingCoordinator coordinator,
			GraphicsData graphicsData) {
		coordinator.claimMain();
		coordinator.setInitialMainCameraOrientation(graphicsData.getCamera());
		
		double verticalFov = graphicsData.getVerticalFov();
		double aspectRatio = graphicsData.getScreenWidth() / (Math.max(graphicsData.getScreenHeight(), 1));
		
		coordinator.setMainVerticalFov(verticalFov);
		coordinator.setMainAspectRatio(aspectRatio);
		coordinator.setInitialBirdsEyeBounds(ViewingCoordinator.extractBounds(graphicsData.getCamera(), 
				verticalFov, 
				aspectRatio));
		
		//debug
		System.out.println("Initial bounds: " + coordinator.getCurrentBirdsEyeBounds());
	}
	
	@Override
	public void extractData(ViewingCoordinator coordinator,
			GraphicsData graphicsData) {
		
		if (coordinator.isBirdsEyeClaimed()) {
			SimpleCamera camera = graphicsData.getCamera();
			Vector3 newPosition;
			
			// TODO: consider moving these somewhere else
			coordinator.setMainVerticalFov(graphicsData.getVerticalFov());
			coordinator.setMainAspectRatio(graphicsData.getScreenWidth() / (Math.max(graphicsData.getScreenHeight(), 1)));
			
			if (coordinator.compareMainCameraChanged(camera)) {
				coordinator.updateMainCamera(camera);
			} else if (coordinator.birdsEyeBoundsChanged()) {
				newPosition = ViewingCoordinator.extractCameraPosition(coordinator, camera.getDirection(), camera.getDistance());
				camera.moveTo(newPosition);
				
				coordinator.updateBirdsEyeBounds();
			}
		}
	}

	@Override
	public void unlinkCoordinator(ViewingCoordinator coordinator) {
		coordinator.unlinkMain();
	}
}
