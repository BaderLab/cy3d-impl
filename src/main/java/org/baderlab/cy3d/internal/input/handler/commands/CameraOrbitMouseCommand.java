package org.baderlab.cy3d.internal.input.handler.commands;

import org.baderlab.cy3d.internal.camera.SimpleCamera;
import org.baderlab.cy3d.internal.input.handler.MouseCommandAdapter;

public class CameraOrbitMouseCommand extends MouseCommandAdapter {

	private final SimpleCamera camera;
	
	private int prevX;
	private int prevY;
	
	
	public CameraOrbitMouseCommand(SimpleCamera camera) {
		this.camera = camera;
	}
	
	
	@Override
	public void pressed(int x, int y) {
		prevX = x;
		prevY = y;
	}

	@Override
	public void dragged(int x, int y) {
		System.out.println("orbit");
		int dx = x - prevX;
		int dy = y - prevY;
		
		camera.orbitLeft(dx);
		camera.orbitUp(dy);
		
		prevX = x;
		prevY = y;
	}
}
