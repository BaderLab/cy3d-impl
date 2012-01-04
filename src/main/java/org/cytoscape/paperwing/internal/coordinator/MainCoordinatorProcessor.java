package org.cytoscape.paperwing.internal.coordinator;

import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.geometric.Quadrilateral;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.tools.GeometryToolkit;
import org.cytoscape.paperwing.internal.tools.SimpleCamera;

public class MainCoordinatorProcessor implements CoordinatorProcessor {
	
	// Read-only for GraphicsData
	@Override
	public void initializeCoordinator(ViewingCoordinator coordinator,
			GraphicsData graphicsData) {
		coordinator.claimMain();
	}
	
	@Override
	public void extractData(ViewingCoordinator coordinator,
			GraphicsData graphicsData) {
		
		if (coordinator.isBirdsEyeClaimed()) {
			SimpleCamera camera = graphicsData.getCamera();
			
			// Update screen data, in case the user resizes the window
			coordinator.updateVerticalFov(graphicsData.getVerticalFov());
			coordinator.updateAspectRatio((double) graphicsData.getScreenWidth() 
					/ Math.max(1, graphicsData.getScreenHeight()));
			
			// This case runs only once
			if (!coordinator.isInitialMainCameraInitialized()) {
				coordinator.setMainCameraCopy(camera);
				coordinator.setInitialMainCameraInitialized(true);
			}
			
			// Regular expected case
			if (coordinator.isInitialBoundsMatched()) {
				
				// User moved bird's eye box, this case takes priority
				if (coordinator.isBirdsEyeBoundsMoved()) {
					
					// Transfer data from coordinator
					Vector3 newPosition = coordinator.calculateCameraPosition(camera.getDirection(), camera.getDistance());
					camera.moveTo(newPosition);
					
					// Unset flag
					coordinator.setBirdsEyeBoundsMoved(false);
					
				// User moved the main camera
				} else if (coordinator.checkCameraChanged(camera)) {
					
					// Transfer data to coordinator
					coordinator.setMainCameraCopy(camera);
					
					// Set flag
					coordinator.setMainCameraMoved(true);
				}
			}
		}
	}

	@Override
	public void unlinkCoordinator(ViewingCoordinator coordinator) {
		coordinator.unlinkMain();
	}
}
