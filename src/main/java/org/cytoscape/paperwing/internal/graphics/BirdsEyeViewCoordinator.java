package org.cytoscape.paperwing.internal.graphics;

import org.cytoscape.paperwing.internal.SimpleCamera;
import org.cytoscape.paperwing.internal.Vector3;

public class BirdsEyeViewCoordinator {
	
	private SimpleCamera newMainCamera;
	private SimpleCamera newBirdsEyeCamera;
	
	private boolean mainCameraChanged = false;
	private boolean birdsEyeCameraChanged = false;
	
	private boolean mainClaimed = false;
	private boolean birdsEyeClaimed = false;
	
	public BirdsEyeViewCoordinator() {
		newMainCamera = new SimpleCamera();
		newBirdsEyeCamera = new SimpleCamera();
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
	}
	
	// Claim by a birds eye view rendering object
	public void claimBirdsEye() {
		birdsEyeClaimed = true;
	}
}
