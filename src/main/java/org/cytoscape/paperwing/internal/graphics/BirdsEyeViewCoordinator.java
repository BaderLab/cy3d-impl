package org.cytoscape.paperwing.internal.graphics;

import java.util.LinkedHashMap;
import java.util.Map;

import org.cytoscape.paperwing.internal.SimpleCamera;
import org.cytoscape.paperwing.internal.Vector3;
import org.cytoscape.view.model.CyNetworkView;

public class BirdsEyeViewCoordinator {

	private SimpleCamera newMainCamera;
	private SimpleCamera newBirdsEyeCamera;

	private boolean mainCameraChanged = false;
	private boolean birdsEyeCameraChanged = false;

	private boolean mainClaimed = false;
	private boolean birdsEyeClaimed = false;

	private boolean mainToUnlink = false;
	private boolean birdsEyeToUnlink = false;
	
	// Assumes 1 CyNetworkView object per main-bird pair
	private static Map<CyNetworkView, BirdsEyeViewCoordinator> coordinators = new LinkedHashMap<CyNetworkView, BirdsEyeViewCoordinator>();
	
	// 1:1 relationship between coordinators and networkViews, can use this to make an inverse map used to remove coordinators from
	// the maps
	private static Map<BirdsEyeViewCoordinator, CyNetworkView> networkViews = new LinkedHashMap<BirdsEyeViewCoordinator, CyNetworkView>();
	
	// This networkView is only used to differentiate between main camera and
	// birds eye camera pairs
	public BirdsEyeViewCoordinator(CyNetworkView networkView) {
		newMainCamera = new SimpleCamera();
		newBirdsEyeCamera = new SimpleCamera();
		
		coordinators.put(networkView, this);
		networkViews.put(this, networkView);
	}
	
	public static BirdsEyeViewCoordinator getCoordinator(CyNetworkView networkView) {
		return coordinators.get(networkView);
	}
	
	// Update orientation
	public void updateMainCamera(SimpleCamera mainCamera) {
		newMainCamera.copyOrientation(mainCamera);
		mainCameraChanged = true;
	}

	// Update orientation
	public void updateBirdsEyeCamera(SimpleCamera birdsEyeCamera) {
		newBirdsEyeCamera.copyOrientation(birdsEyeCamera);
		birdsEyeCameraChanged = true;
	}

	// Get the updated camera
	public SimpleCamera getNewMainCamera() {
		return newMainCamera;
	}

	// Get the updated camera
	public SimpleCamera getNewBirdsEyeCamera() {
		return newBirdsEyeCamera;
	}

	public boolean mainCameraChanged() {
		return mainCameraChanged;
	}

	public boolean birdsEyeCameraChanged() {
		return birdsEyeCameraChanged;
	}

	public void updateMainCamera() {
		mainCameraChanged = false;
	}

	public void updateBirdsEyeCamera() {
		birdsEyeCameraChanged = false;
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
