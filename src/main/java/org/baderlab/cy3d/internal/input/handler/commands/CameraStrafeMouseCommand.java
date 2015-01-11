package org.baderlab.cy3d.internal.input.handler.commands;

import org.baderlab.cy3d.internal.input.handler.MouseCommand;
import org.baderlab.cy3d.internal.tools.SimpleCamera;

public class CameraStrafeMouseCommand implements MouseCommand {

	private final SimpleCamera camera;
	
	private int prevX;
	private int prevY;
	
	
	public CameraStrafeMouseCommand(SimpleCamera camera) {
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
		
		camera.moveRight(dx);
		camera.moveDown(dy);
		
		prevX = x;
		prevY = y;
	}

	@Override
	public void clicked(int x, int y) {
	}

	@Override
	public void released(int x, int y) {
	}

	@Override
	public MouseCommand modify() {
		return this;
	}
}
