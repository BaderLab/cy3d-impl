package org.baderlab.cy3d.internal.camera;

import org.baderlab.cy3d.internal.geometric.Vector3;

public interface Camera extends CameraPosition {
	
	/**
	 * Translates the camera to the specified position and orientation.
	 * @param up may be null to keep current orientation.
	 */
	void moveTo(Vector3 position, Vector3 up);
	
	
	void orbitUp(double angle);
	
	void orbitRight(double angle);
	
	
	//void yaw(double angle);
	
	//void pitch(double angle);
	
	/**
	 * @param angle in radians
	 */
	void roll(double angle);
	
	
	void moveForward(double multiplier);
	
	
	void reset();
	


}
