package org.baderlab.cy3d.internal.input.handler;

public interface MouseCommand {

	public static MouseCommand EMPTY = new MouseCommandAdapter();
	
	
	void pressed(int x, int y);
	
	void dragged(int x, int y);
	
	void clicked(int x, int y);
	
	void released(int x, int y);
	
	void moved(int x, int y);
	
	/**
	 * Returns a mouse command that is modified by holding down Ctrl.
	 */
	MouseCommand modify();
	
}
