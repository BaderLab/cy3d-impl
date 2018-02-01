package org.baderlab.cy3d.internal.eventbus;

import org.baderlab.cy3d.internal.camera.CameraPosition;

public class MainCameraChangeEvent {

	private final CameraPosition newCameraPosition;

	public MainCameraChangeEvent(CameraPosition newCameraPosition) {
		this.newCameraPosition = newCameraPosition;
	}

	public CameraPosition getNewCameraPosition() {
		return newCameraPosition;
	}
}
