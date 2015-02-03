package org.baderlab.cy3d.internal.camera;

import org.baderlab.cy3d.internal.geometric.Vector3;

public interface Camera extends CameraPosition {
	
	/**
	 * Moves the camera to the specified position and orientation.
	 * @param up may be null to keep current orientation.
	 */
	public void moveTo(Vector3 position, Vector3 up);
	
	
	public void orbitUp(double angle);
	
	public void orbitRight(double angle);
	
	
	public void moveForward(double multiplier);
	
	
	public void reset();
	


}
