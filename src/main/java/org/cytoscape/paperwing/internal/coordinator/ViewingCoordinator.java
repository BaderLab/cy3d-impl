package org.cytoscape.paperwing.internal.coordinator;

import java.util.LinkedHashMap;
import java.util.Map;

import org.cytoscape.paperwing.internal.geometric.Quadrilateral;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.utility.GraphicsUtility;
import org.cytoscape.paperwing.internal.utility.SimpleCamera;
import org.cytoscape.view.model.CyNetworkView;

public class ViewingCoordinator {

	public static double BOUNDS_CHANGE_THRESHOLD = 5e-22;
	public static double CAMERA_CHANGE_THRESHOLD = 5e-22;
	
	private SimpleCamera currentMainCamera;
	private Quadrilateral currentBirdsEyeBounds;

	private boolean mainCameraChanged = false;
	private boolean birdsEyeBoundsChanged = false;
	
	private double mainVerticalFov = 45;
	private double mainAspectRatio = 1;
	
	private boolean boundsInitialized = false;
	
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
	
	// 1:1 relationship between coordinators and networkViews, can use this to make an inverse map used to remove coordinators from
	// the maps
	private static Map<ViewingCoordinator, CyNetworkView> networkViews = new LinkedHashMap<ViewingCoordinator, CyNetworkView>();
	
	// Camera direction must be a unit vector
	public static Vector3 extractCameraPosition(ViewingCoordinator coordinator, Vector3 cameraDirection, double cameraDistance) {
		Vector3 offset = cameraDirection.copy();
		offset.multiplyLocal(cameraDistance);
		
		Quadrilateral bounds = coordinator.getCurrentBirdsEyeBounds();
		
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
		currentMainCamera = new SimpleCamera();
		currentBirdsEyeBounds = new Quadrilateral();
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
	
	// Stores a copy of the given camera
	public void setInitialMainCameraOrientation(SimpleCamera mainCamera) {
		currentMainCamera.copyOrientation(currentMainCamera);
	}
	
	// Stores a copy of the bounds
	public void setInitialBirdsEyeBounds(Quadrilateral bounds) {
		currentBirdsEyeBounds.set(bounds);
		
		boundsInitialized = true;
	}
	
	// Update orientation
	public void updateMainCamera(SimpleCamera newMainCamera) {
		currentMainCamera.copyOrientation(newMainCamera);
		mainCameraChanged = true;
	}
	
	// Update orientation
	public void updateBirdsEyeBounds(Quadrilateral newBounds) {
		currentBirdsEyeBounds.set(newBounds);
		birdsEyeBoundsChanged = true;
	}
	
	public boolean compareMainCameraChanged(SimpleCamera newMainCamera) {
		double threshold = CAMERA_CHANGE_THRESHOLD;
		
		if (currentMainCamera.getPosition().distanceSquared(newMainCamera.getPosition()) > threshold) {
			return true;
		} else if (currentMainCamera.getDirection().distanceSquared(newMainCamera.getDirection()) > threshold) {
			return true;
		} else if (currentMainCamera.getUp().distanceSquared(newMainCamera.getUp()) > threshold) {
			return true;
		}
		
		return false;
	}
	
	public boolean compareBirdsEyeBoundsChanged(Quadrilateral newBounds) {
		double threshold = BOUNDS_CHANGE_THRESHOLD;
		
		if (currentBirdsEyeBounds.getTopLeft().distanceSquared(newBounds.getTopLeft()) > threshold) {
			return true;
		} else if (currentBirdsEyeBounds.getTopRight().distanceSquared(newBounds.getTopRight()) > threshold) {
			return true;
		} else if (currentBirdsEyeBounds.getBottomLeft().distanceSquared(newBounds.getBottomLeft()) > threshold) {
			return true;
		} else if (currentBirdsEyeBounds.getBottomRight().distanceSquared(newBounds.getBottomRight()) > threshold) {
			return true;
		}
		
		return false;
	}
	
	
	// Get the updated camera
	public SimpleCamera getCurrentMainCamera() {
		return currentMainCamera;
	}

	// Get the updated camera
	public Quadrilateral getCurrentBirdsEyeBounds() {
		return currentBirdsEyeBounds;
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
		mainStatus = CoordinatorStatus.CLAIMED_AND_UNLINKED;
		
		// Both unlinked; prepare to remove this object
		if (birdsEyeStatus == CoordinatorStatus.CLAIMED_AND_UNLINKED) {
			CyNetworkView networkView = networkViews.get(this);
			
			coordinators.remove(networkView);
			networkViews.remove(this);
		}
	}
	
	// Notify that bird's eye view will no longer look after this coordinator
	public void unlinkBirdsEye() {
		birdsEyeStatus = CoordinatorStatus.CLAIMED_AND_UNLINKED;
		
		// Both unlinked; prepare to remove this object
		if (mainStatus == CoordinatorStatus.CLAIMED_AND_UNLINKED) {
			CyNetworkView networkView = networkViews.get(this);
			
			coordinators.remove(networkView);
			networkViews.remove(this);
		}
	}

	public void setMainVerticalFov(double mainVerticalFov) {
		this.mainVerticalFov = mainVerticalFov;
	}

	public double getMainVerticalFov() {
		return mainVerticalFov;
	}

	public void setMainAspectRatio(double mainAspectRatio) {
		this.mainAspectRatio = mainAspectRatio;
	}

	public double getMainAspectRatio() {
		return mainAspectRatio;
	}

	public boolean isBoundsInitialized() {
		return boundsInitialized;
	}
	
}
