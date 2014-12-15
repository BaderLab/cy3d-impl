package org.baderlab.cy3d.internal.coordinator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.Manifest;

import javax.media.opengl.GLAnimatorControl;
import javax.swing.SwingUtilities;

import org.baderlab.cy3d.internal.cytoscape.edges.EdgeAnalyser;
import org.baderlab.cy3d.internal.geometric.Quadrilateral;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.graphics.AnimatorController;
import org.baderlab.cy3d.internal.tools.GeometryToolkit;
import org.baderlab.cy3d.internal.tools.SimpleCamera;
import org.cytoscape.view.model.CyNetworkView;

import com.jogamp.opengl.util.FPSAnimator;

/**
 * This class is responsible for allowing communication between the main
 * and bird's eye rendering objects without introducing circular reference schemes,
 * as well allowing for either of the main and bird's eye pair to be switched during
 * runtime.
 *
 * @author yuedong
 */
public class ViewingCoordinator {

	// Distance between bounds and camera position
	// TODO: Make into member variable, with adjustable value
	public static final double NEAR_BOUNDS_DISTANCE = 0.9;
	
	// Distance between the back bounds and the camera
	public static final double FAR_BOUNDS_DISTANCE = 1.1;
	
	public static final double BOUNDS_CHANGE_THRESHOLD = 5e-16;
	public static final double CAMERA_CHANGE_THRESHOLD = 5e-25;
	
	private SimpleCamera mainCameraCopy;
	private Quadrilateral birdsEyeBoundsCopy;
	
	private double verticalFov;
	private double aspectRatio;
	
	private boolean mainCameraMoved = false;
	private boolean birdsEyeBoundsMoved = false;
	private boolean initialMainCameraInitialized = false;
	private boolean initialBoundsMatched = false;
	private boolean suggestRecalculateBounds = false;
	
	private EdgeAnalyser mainEdgeAnalyser = null;
	
	private GLAnimatorControl birdsEyeAnimatorControl;
	private AnimatorController mainAnimatorController;
	
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
				mainCameraCopy.getDirection(), mainCameraCopy.getUp(), NEAR_BOUNDS_DISTANCE, verticalFov, aspectRatio);
		
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
	public Quadrilateral calculateNearBounds() {
		if (!initialMainCameraInitialized) {
			return null;
		} else {
			return GeometryToolkit.generateViewingBounds(mainCameraCopy.getPosition(), 
					mainCameraCopy.getDirection(), 
					mainCameraCopy.getUp(),
					NEAR_BOUNDS_DISTANCE, verticalFov, aspectRatio);
		}
	}
	
	// Returns null if main camera not initialized
	public Quadrilateral calculateBackBounds() {
		if (!initialMainCameraInitialized) {
			return null;
		} else {
			return GeometryToolkit.generateViewingBounds(mainCameraCopy.getPosition(), 
					mainCameraCopy.getDirection(), 
					mainCameraCopy.getUp(),
					FAR_BOUNDS_DISTANCE, verticalFov, aspectRatio);
		}
	}
	
	public Vector3 calculateCameraPosition(Vector3 cameraDirection) {
		if (!initialBoundsMatched) {
			return null;
		} else {
			return GeometryToolkit.generateCameraPosition(birdsEyeBoundsCopy, cameraDirection, NEAR_BOUNDS_DISTANCE);
		}
	}
	
	public Vector3 getUpdatedMainCameraPosition() {
		return mainCameraCopy.getPosition().copy();
	}
	
	// Claim by a main window rendering object
	public void claimMain() {
		mainStatus = CoordinatorStatus.CLAIMED_AND_LINKED;
	}

	// Claim by a birds eye view rendering object
	public void claimBirdsEye() {
		birdsEyeStatus = CoordinatorStatus.CLAIMED_AND_LINKED;
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

	public void setMainEdgeAnalyser(EdgeAnalyser mainEdgeAnalyser) {
		this.mainEdgeAnalyser = mainEdgeAnalyser;
	}

	public EdgeAnalyser getMainEdgeAnalyser() {
		return mainEdgeAnalyser;
	}

	public void setBirdsEyeAnimatorControl(GLAnimatorControl birdsEyeAnimatorControl) {
		this.birdsEyeAnimatorControl = birdsEyeAnimatorControl;
	}

	public GLAnimatorControl getBirdsEyeAnimatorControl() {
		return birdsEyeAnimatorControl;
	}

	public void setMainAnimatorController(AnimatorController mainAnimatorController) {
		this.mainAnimatorController = mainAnimatorController;
	}

	public AnimatorController getMainAnimatorController() {
		return mainAnimatorController;
	}
}
