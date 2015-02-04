package org.baderlab.cy3d.internal.input.handler.commands;

import org.baderlab.cy3d.internal.camera.SimpleCamera;
import org.baderlab.cy3d.internal.input.handler.MouseCommandAdapter;

public class CameraStrafeMouseCommand extends MouseCommandAdapter {

	private final SimpleCamera camera;
	
	private int prevX;
	private int prevY;
	
	
	public CameraStrafeMouseCommand(SimpleCamera camera) {
		this.camera = camera;
	}
	
	
	@Override
	public void dragStart(int x, int y) {
		prevX = x;
		prevY = y;
	}

	@Override
	public void dragMove(int x, int y) {
		int dx = x - prevX;
		int dy = y - prevY;
		
		camera.moveRight(-dx);
		camera.moveDown(-dy);
		
		prevX = x;
		prevY = y;
	}

}
