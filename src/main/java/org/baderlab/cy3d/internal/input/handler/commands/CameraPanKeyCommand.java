package org.baderlab.cy3d.internal.input.handler.commands;

import org.baderlab.cy3d.internal.input.handler.KeyCommand;
import org.baderlab.cy3d.internal.tools.SimpleCamera;

public class CameraPanKeyCommand implements KeyCommand {
	
	private SimpleCamera camera; 

	public CameraPanKeyCommand(SimpleCamera camera) {
		this.camera = camera;
	}

	@Override
	public void up() {
		camera.turnUp(2.0);
	}

	@Override
	public void down() {
		camera.turnDown(2.0);
	}

	@Override
	public void left() {
		camera.turnLeft(2.0);
	}

	@Override
	public void right() {
		camera.turnRight(2.0);
	}
	
}
