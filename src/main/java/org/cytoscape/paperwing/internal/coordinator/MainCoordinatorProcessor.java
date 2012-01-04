package org.cytoscape.paperwing.internal.coordinator;

import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.tools.SimpleCamera;

public class MainCoordinatorProcessor implements CoordinatorProcessor {
	
	// Read-only for GraphicsData
	@Override
	public void initializeCoordinator(ViewingCoordinator coordinator,
			GraphicsData graphicsData) {
		coordinator.claimMain();
		coordinator.setInitialMainCameraOrientation(graphicsData.getCamera());
	}
	
	@Override
	public void extractData(ViewingCoordinator coordinator,
			GraphicsData graphicsData) {
		
		if (!coordinator.isBoundsInitialized()) {
			SimpleCamera camera = graphicsData.getCamera();
			
			double verticalFov = graphicsData.getVerticalFov();
			double aspectRatio = (float) graphicsData.getScreenWidth() / (Math.max(graphicsData.getScreenHeight(), 1));
			
			coordinator.setMainVerticalFov(verticalFov);
			coordinator.setMainAspectRatio(aspectRatio);
			
			System.out.println("ScreenWidth: " + graphicsData.getScreenWidth() + ", ScreenHeight: " + graphicsData.getScreenHeight());
			System.out.println("Initialing birds eye bounds: fov " + verticalFov + ", aspectRatio " + aspectRatio);
			
			coordinator.setInitialBirdsEyeBounds(ViewingCoordinator.extractNewDrawnBounds(camera.getPosition(), 
					camera.getDirection(), camera.getUp(), camera.getDistance(), verticalFov, aspectRatio));
			
			//debug
			System.out.println("Initial bounds: " + coordinator.getCurrentBirdsEyeBounds());
		}
		
		if (coordinator.isBirdsEyeClaimed()) {
			SimpleCamera camera = graphicsData.getCamera();
			Vector3 newPosition;
			
			// TODO: consider moving these somewhere else
			coordinator.setMainVerticalFov(graphicsData.getVerticalFov());
			coordinator.setMainAspectRatio((float) graphicsData.getScreenWidth() / (Math.max(graphicsData.getScreenHeight(), 1)));
			
			if (coordinator.compareMainCameraChanged(camera)) {
				coordinator.updateMainCamera(camera);
			} else if (coordinator.birdsEyeBoundsChanged()) {
				//newPosition = ViewingCoordinator.extractCameraPosition(coordinator, camera.getDirection(), camera.getDistance());
				newPosition = ViewingCoordinator.findNewOrthoCameraPosition(coordinator.getCurrentBirdsEyeBounds(), camera.getPosition(), camera.getDirection());
				camera.moveTo(newPosition);
				
				coordinator.updateMainCamera(camera);
				
				coordinator.updateBirdsEyeBounds();
			}
		}
	}

	@Override
	public void unlinkCoordinator(ViewingCoordinator coordinator) {
		coordinator.unlinkMain();
	}
}
