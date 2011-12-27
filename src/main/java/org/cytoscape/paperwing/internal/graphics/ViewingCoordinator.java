package org.cytoscape.paperwing.internal.graphics;

import java.util.LinkedHashMap;
import java.util.Map;

import org.cytoscape.paperwing.internal.SimpleCamera;
import org.cytoscape.paperwing.internal.Vector3;
import org.cytoscape.view.model.CyNetworkView;

public class ViewingCoordinator {

	private SimpleCamera newMainCamera;
	private Quadrilateral newBirdsEyeBounds;

	private boolean mainCameraChanged = false;
	private boolean birdsEyeBoundsChanged = false;

	private boolean mainClaimed = false;
	private boolean birdsEyeClaimed = false;

	private boolean mainToUnlink = false;
	private boolean birdsEyeToUnlink = false;
	
	// Assumes 1 CyNetworkView object per main-bird pair
	private static Map<CyNetworkView, ViewingCoordinator> coordinators = new LinkedHashMap<CyNetworkView, ViewingCoordinator>();
	
	// 1:1 relationship between coordinators and networkViews, can use this to make an inverse map used to remove coordinators from
	// the maps
	private static Map<ViewingCoordinator, CyNetworkView> networkViews = new LinkedHashMap<ViewingCoordinator, CyNetworkView>();
	
	// Camera direction must be a unit vector
	public static Vector3 extractCameraPosition(ViewingCoordinator coordinator, Vector3 cameraDirection, double cameraDistance) {
		Vector3 offset = cameraDirection.copy();
		offset.multiplyLocal(cameraDistance);
		
		Quadrilateral bounds = coordinator.getNewBirdsEyeBounds();
		
		Vector3 position = bounds.getCenterPoint();
		position.addLocal(offset);
		
		return position;
	}
	
	public static Quadrilateral extractBounds(SimpleCamera camera, double verticalFov, double aspectRatio) {
		return GraphicsUtility.generateViewingBounds(camera.getPosition(), camera.getDirection(), camera.getUp(), 
				camera.getDistance(), verticalFov, aspectRatio);
	}
	
	// This networkView is only used to differentiate between main camera and
	// birds eye camera pairs
	private ViewingCoordinator(CyNetworkView networkView) {
		newMainCamera = new SimpleCamera();
		newBirdsEyeBounds = new Quadrilateral();
	}
	
	public static ViewingCoordinator getCoordinator(CyNetworkView networkView) {
		return coordinators.get(networkView);
	}
	
	public static ViewingCoordinator createCoordinator(CyNetworkView networkView) {
		ViewingCoordinator coordinator = new ViewingCoordinator(networkView);
		
		coordinators.put(networkView, coordinator);
		networkViews.put(coordinator, networkView);
		
		return coordinator;
	}
	
	// Update orientation
	public void updateMainCamera(SimpleCamera mainCamera) {
		newMainCamera.copyOrientation(mainCamera);
		mainCameraChanged = true;
	}

	// Update orientation
	public void updateBirdsEyeBounds(Quadrilateral bounds) {
		newBirdsEyeBounds.set(bounds);
		birdsEyeBoundsChanged = true;
	}
	
	// Get the updated camera
	public SimpleCamera getNewMainCamera() {
		return newMainCamera;
	}

	// Get the updated camera
	public Quadrilateral getNewBirdsEyeBounds() {
		return newBirdsEyeBounds;
	}

	public boolean mainCameraChanged() {
		return mainCameraChanged;
	}

	public boolean birdsEyeBoundsChanged() {
		return birdsEyeBoundsChanged;
	}

	public void updateMainCamera() {
		mainCameraChanged = false;
	}

	public void updateBirdsEyeBounds() {
		birdsEyeBoundsChanged = false;
	}

	// Claim by a main window rendering object
	public void claimMain() {
		mainClaimed = true;
		mainToUnlink = false;
	}

	// Claim by a birds eye view rendering object
	public void claimBirdsEye() {
		birdsEyeClaimed = true;
		birdsEyeToUnlink = false;
	}
	
	// Notify that main will no longer look after this coordinator
	public void unlinkMain() {
		mainClaimed = false;
		mainToUnlink = true;
		
		// Both unlinked; prepare to remove this object
		if (birdsEyeToUnlink) {
			CyNetworkView networkView = networkViews.get(this);
			
			coordinators.remove(networkView);
			networkViews.remove(this);
		}
	}
	
	// Notify that bird's eye view will no longer look after this coordinator
	public void unlinkBirdsEye() {
		birdsEyeClaimed = false;
		birdsEyeToUnlink = true;
		
		// Both unlinked; prepare to remove this object
		if (mainToUnlink) {
			CyNetworkView networkView = networkViews.get(this);
			
			coordinators.remove(networkView);
			networkViews.remove(this);
		}
	}
	
}
