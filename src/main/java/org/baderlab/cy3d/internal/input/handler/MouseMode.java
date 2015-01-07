package org.baderlab.cy3d.internal.input.handler;

public enum MouseMode {

	SELECT,
	PAN,
	STRAFE,
	ORBIT;
	
	
	public static MouseMode getDefault() {
		return SELECT;
	}
}
