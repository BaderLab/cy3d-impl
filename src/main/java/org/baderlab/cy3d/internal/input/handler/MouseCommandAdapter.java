package org.baderlab.cy3d.internal.input.handler;

/**
 * A convenience class with empty implementations of the methods declared in {@link MouseCommand}.
 * 
 * @author mkucera
 */
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

	@Override
	public void exited() {
	}
	
	@Override
	public void entered() {
	}
	
	/**
	 * Default implementation returns the current object.
	 */
	@Override
	public MouseCommand modify() {
		return this;
	}

	

}
