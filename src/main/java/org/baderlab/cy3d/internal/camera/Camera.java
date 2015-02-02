package org.baderlab.cy3d.internal.camera;

import org.baderlab.cy3d.internal.geometric.Vector3;

public interface Camera extends CameraPosition {
	
	
	public void moveTo(Vector3 position);
	
	
	public void orbitUp(double angle);
	
	public void orbitRight(double angle);
	
	
	public void moveForward(double multiplier);
	
	
	public void reset();
	


}
