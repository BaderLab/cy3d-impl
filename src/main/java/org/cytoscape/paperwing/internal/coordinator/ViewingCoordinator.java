package org.cytoscape.paperwing.internal.coordinator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.Manifest;

import org.cytoscape.paperwing.internal.geometric.Quadrilateral;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.tools.GeometryToolkit;
import org.cytoscape.paperwing.internal.tools.SimpleCamera;
import org.cytoscape.view.model.CyNetworkView;

public class ViewingCoordinator {

	public static double BOUNDS_CHANGE_THRESHOLD = 5e-16;
	public static double CAMERA_CHANGE_THRESHOLD = 5e-25;
	
	private SimpleCamera mainCameraCopy;
	private Quadrilateral birdsEyeBoundsCopy;
	
	private double verticalFov;
	private double aspectRatio;
	
	private boolean mainCameraMoved = false;
	private boolean birdsEyeBoundsMoved = false;
	private boolean initialMainCameraInitialized = false;
	private boolean initialBoundsMatched = false;
	private boolean suggestRecalculateBounds = false;
	
	// Claiming: Means whether a Graphics object has claimed ownership of this coordinator by obtaining a reference to it.
	// Linking: Whether or not that Graphics object wishes to retain the reference to the coordinator, useful for garbage collection
	
	private enum CoordinatorStatus {
		CLAIMED_AND_LINKED, // Graphics object has reference, currently keeping reference
		CLAIMED_AND_UNLINKED, // Graphics object has reference, about to remove reference
		UNCLAIMED_AND_UNLINKED // Initial state
	}; 
	
	private CoordinatorStatus mainStatus = CoordinatorStatus.UNCLAIMED_AND_UNLINKED;
	private CoordinatorStatus birdsEyeStatus = CoordinatorStatus.UNCLAIMED_AND_UNLINKED;
	
	// Assumes 1 CyNetworkView object per main-bird pair
	private static Map<CyNetworkView, ViewingCoordinator> coordinators = new LinkedHashMap<CyNetworkView, ViewingCoordinator>();
	
	
	// Orthogonally shifts the camera to match new bounds, orthogonal with respect to the camera's direction vector
	public static Vector3 findNewOrthoCameraPosition(Quadrilateral newBounds, Vector3 oldCameraPosition, Vector3 cameraDirection) {
		return GeometryToolkit.findNewOrthogonalAnchoredPosition(newBounds.getCenterPoint(), oldCameraPosition, cameraDirection);
	}
	
	public void updateVerticalFov(double verticalFov) {
		this.verticalFov = verticalFov;
	}
	
	public void updateAspectRatio(double aspectRatio) {
		this.aspectRatio = aspectRatio;
	}
	
	public Quadrilateral getMainCameraBounds() {
		Quadrilateral bounds;
		
		bounds = GeometryToolkit.generateViewingBounds(mainCameraCopy.getPosition(), 
				mainCameraCopy.getDirection(), mainCameraCopy.getUp(), mainCameraCopy.getDistance(), verticalFov, aspectRatio);
		
		return bounds;
	}
	
	// This networkView is only used to differentiate between main camera and
	// birds eye camera pairs
	private ViewingCoordinator(CyNetworkView networkView) {
		mainCameraCopy = new SimpleCamera();
		birdsEyeBoundsCopy = new Quadrilateral();
	}
	
	public static ViewingCoordinator getCoordinator(CyNetworkView networkView) {
		return coordinators.get(networkView);
	}
	
	public static ViewingCoordinator createCoordinator(CyNetworkView networkView) {
		ViewingCoordinator coordinator = new ViewingCoordinator(networkView);
		
		coordinators.put(networkView, coordinator);
		
		return coordinator;
	}
	
	public boolean checkCameraChanged(SimpleCamera mainCamera) {
		double threshold = CAMERA_CHANGE_THRESHOLD;
		
		// Positional movement
		if (mainCameraCopy.getPosition().distanceSquared(mainCamera.getPosition()) > threshold) {
			return true;
		// Rotation
		} else if (mainCameraCopy.getDirection().distanceSquared(mainCamera.getDirection()) > threshold) {
			return true;
		// Camera rolling
		} else if (mainCameraCopy.getUp().distanceSquared(mainCamera.getUp()) > threshold) {
			return true;
		}
		
		return false;
	}

	// Returns null if main camera not initialized
	public Quadrilateral calculateBounds() {
		if (!initialMainCameraInitialized) {
			return null;
		} else {
			return GeometryToolkit.generateViewingBounds(mainCameraCopy.getPosition(), 
					mainCameraCopy.getDirection(), 
					mainCameraCopy.getUp(),
					mainCameraCopy.getDistance(), verticalFov, aspectRatio);
		}
	}
	
	public Vector3 calculateCameraPosition(Vector3 cameraDirection, double distance) {
		if (!initialBoundsMatched) {
			return null;
		} else {
			return GeometryToolkit.generateCameraPosition(birdsEyeBoundsCopy, cameraDirection, distance);
		}
	}
	
	public Vector3 getUpdatedMainCameraPosition() {
		return mainCameraCopy.getPosition().copy();
	}
	
	// Claim by a main window rendering object
	public void claimMain() {
		mainStatus = CoordinatorStatus.CLAIMED_AND_LINKED;
		
		//debug
		System.out.println("Coordinator main claimed: " + this);
		if (isBirdsEyeClaimed()) {
			System.out.println("Coordinator setup: " + this);
		}
	}

	// Claim by a birds eye view rendering object
	public void claimBirdsEye() {
		birdsEyeStatus = CoordinatorStatus.CLAIMED_AND_LINKED;
		
		//debug
		System.out.println("Coordinator birdsEye claimed: " + this);
		if (isMainClaimed()) {
			System.out.println("Coordinator setup: " + this);
		}
	}
	
	public boolean isMainClaimed() {
		if (mainStatus == CoordinatorStatus.CLAIMED_AND_LINKED
				|| mainStatus == CoordinatorStatus.CLAIMED_AND_UNLINKED) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isBirdsEyeClaimed() {
		if (birdsEyeStatus == CoordinatorStatus.CLAIMED_AND_LINKED
				|| birdsEyeStatus == CoordinatorStatus.CLAIMED_AND_UNLINKED) {
			return true;
		} else {
			return false;
		}
	}
	
	// Notify that main will no longer look after this coordinator
	public void unlinkMain() {
		mainStatus = CoordinatorStatus.UNCLAIMED_AND_UNLINKED;
		
		// Both unlinked; prepare to remove this object
		if (birdsEyeStatus == CoordinatorStatus.UNCLAIMED_AND_UNLINKED) {

			removeFromList();
		}
	}
	
	// Notify that bird's eye view will no longer look after this coordinator
	public void unlinkBirdsEye() {
		birdsEyeStatus = CoordinatorStatus.UNCLAIMED_AND_UNLINKED;
		
		// Both unlinked; prepare to remove this object
		if (mainStatus == CoordinatorStatus.UNCLAIMED_AND_UNLINKED) {
			
			removeFromList();
		}
	}
	
	private void removeFromList() {
		for (CyNetworkView networkView : coordinators.keySet()) {
			if (coordinators.get(networkView) == this) {
				coordinators.remove(networkView);
				return;
			}
		}
	}

	public void setMainVerticalFov(double mainVerticalFov) {
		this.verticalFov = mainVerticalFov;
	}

	public double getMainVerticalFov() {
		return verticalFov;
	}

	public void setMainAspectRatio(double mainAspectRatio) {
		this.aspectRatio = mainAspectRatio;
	}

	public double getMainAspectRatio() {
		return aspectRatio;
	}

	public void setMainCameraMoved(boolean mainCameraMoved) {
		this.mainCameraMoved = mainCameraMoved;
	}

	public boolean isMainCameraMoved() {
		return mainCameraMoved;
	}

	public void setBirdsEyeBoundsMoved(boolean birdsEyeBoundsMoved) {
		this.birdsEyeBoundsMoved = birdsEyeBoundsMoved;
	}

	public boolean isBirdsEyeBoundsMoved() {
		return birdsEyeBoundsMoved;
	}

	public void setInitialMainCameraInitialized(boolean initialMainCameraInitialized) {
		this.initialMainCameraInitialized = initialMainCameraInitialized;
	}

	public boolean isInitialMainCameraInitialized() {
		return initialMainCameraInitialized;
	}

	public void setInitialBoundsMatched(boolean boundsInitialSynchronized) {
		this.initialBoundsMatched = boundsInitialSynchronized;
	}

	public boolean isInitialBoundsMatched() {
		return initialBoundsMatched;
	}

	public Quadrilateral getBirdsEyeBoundsCopy() {
		return birdsEyeBoundsCopy;
	}

	public void setBirdsEyeBoundsCopy(Quadrilateral birdsEyeBounds) {
		this.birdsEyeBoundsCopy.set(birdsEyeBounds);
	}

	public SimpleCamera getMainCameraCopy() {
		return mainCameraCopy;
	}	
	
	public void setMainCameraCopy(SimpleCamera camera) {
		mainCameraCopy.set(camera);
	}

	public void setSuggestRecalculateBounds(boolean suggestRecalculateBounds) {
		this.suggestRecalculateBounds = suggestRecalculateBounds;
	}

	public boolean isSuggestRecalculateBounds() {
		return suggestRecalculateBounds;
	}
}
