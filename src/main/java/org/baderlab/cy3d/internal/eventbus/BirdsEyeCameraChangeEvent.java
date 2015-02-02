package org.baderlab.cy3d.internal.eventbus;

import org.baderlab.cy3d.internal.camera.CameraPosition;

public class BirdsEyeCameraChangeEvent {

	private final CameraPosition newCameraPosition;

	public BirdsEyeCameraChangeEvent(CameraPosition newCameraPosition) {
		this.newCameraPosition = newCameraPosition;
	}

	public CameraPosition getNewCameraPosition() {
		return newCameraPosition;
	}
}
