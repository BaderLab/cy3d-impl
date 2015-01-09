package org.baderlab.cy3d.internal.input.handler.commands;

import org.baderlab.cy3d.internal.input.handler.KeyCommand;
import org.baderlab.cy3d.internal.tools.SimpleCamera;

public class CameraStrafeKeyCommand implements KeyCommand {

	private SimpleCamera camera; 

	public CameraStrafeKeyCommand(SimpleCamera camera) {
		this.camera = camera;
	}

	@Override
	public void up() {
		camera.moveUp(2.0);
	}

	@Override
	public void down() {
		camera.moveDown(2.0);
	}

	@Override
	public void left() {
		camera.moveLeft(2.0);
	}

	@Override
	public void right() {
		camera.moveRight(2.0);
	}

}
