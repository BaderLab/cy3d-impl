package org.baderlab.cy3d.internal.data;

public interface SettingsData {

	public enum CameraDragMode { OFF, PAN, STRAFE, ORBIT };
	
	
	public CameraDragMode getCameraDragMode();
	
	public boolean isSelectMode();
	
	public boolean resetCamera();
	
	
}
