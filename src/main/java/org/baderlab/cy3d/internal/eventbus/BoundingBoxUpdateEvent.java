package org.baderlab.cy3d.internal.eventbus;

import org.baderlab.cy3d.internal.camera.CameraPosition;

public class BoundingBoxUpdateEvent {

	private final CameraPosition cameraPosition;
	
	public BoundingBoxUpdateEvent(CameraPosition cameraPosition) {
		this.cameraPosition = cameraPosition;
	}

	public CameraPosition getCameraPosition() {
		return cameraPosition;
	}
	
}
