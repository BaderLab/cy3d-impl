package org.baderlab.cy3d.internal.input.handler;

public enum MouseMode {

	SELECT,
	CAMERA;
	
	public static MouseMode getDefault() {
		return CAMERA;
	}
}
