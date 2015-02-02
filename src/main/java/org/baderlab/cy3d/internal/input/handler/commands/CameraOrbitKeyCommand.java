package org.baderlab.cy3d.internal.input.handler.commands;

import org.baderlab.cy3d.internal.camera.Camera;
import org.baderlab.cy3d.internal.input.handler.KeyCommand;

public class CameraOrbitKeyCommand implements KeyCommand {

	private Camera camera; 

	public CameraOrbitKeyCommand(Camera camera) {
		this.camera = camera;
	}

	@Override
	public void up() {
		camera.orbitUp(4.0);
	}

	@Override
	public void down() {
		camera.orbitUp(-4.0);
	}

	@Override
	public void left() {
		camera.orbitRight(-4.0);
	}

	@Override
	public void right() {
		camera.orbitRight(4.0);
	}

}
