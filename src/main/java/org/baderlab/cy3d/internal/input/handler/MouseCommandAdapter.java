package org.baderlab.cy3d.internal.input.handler;

public class MouseCommandAdapter implements MouseCommand {

	@Override
	public void pressed(int x, int y) {
	}

	@Override
	public void dragged(int x, int y) {
	}

	@Override
	public void clicked(int x, int y) {
	}

	@Override
	public void released(int x, int y) {
	}
	
	@Override
	public void moved(int x, int y) {
	}

	/**
	 * Default implementation returns the current object.
	 */
	@Override
	public MouseCommand modify() {
		return this;
	}

}
