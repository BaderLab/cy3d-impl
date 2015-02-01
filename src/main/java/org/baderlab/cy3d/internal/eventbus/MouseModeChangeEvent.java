package org.baderlab.cy3d.internal.eventbus;

import org.baderlab.cy3d.internal.input.handler.MouseMode;

public class MouseModeChangeEvent {

	private final MouseMode mouseMode;

	public MouseModeChangeEvent(MouseMode mouseMode) {
		this.mouseMode = mouseMode;
	}
	
	public MouseMode getMouseMode() {
		return mouseMode;
	}
}
