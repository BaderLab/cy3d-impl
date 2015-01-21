package org.baderlab.cy3d.internal.input.handler.commands;

import org.baderlab.cy3d.internal.input.handler.MouseCommandAdapter;
import org.baderlab.cy3d.internal.tools.SimpleCamera;

public class CameraPanMouseCommand extends MouseCommandAdapter {

	private final SimpleCamera camera;
	
	private int prevX;
	private int prevY;
	
	
	public CameraPanMouseCommand(SimpleCamera camera) {
		this.camera = camera;
	}
	
	
	@Override
	public void pressed(int x, int y) {
		prevX = x;
		prevY = y;
	}

	@Override
	public void dragged(int x, int y) {
		int dx = x - prevX;
		int dy = y - prevY;
		
		camera.turnRight(-dx);
		camera.turnDown(-dy);
		
		prevX = x;
		prevY = y;
	}

}