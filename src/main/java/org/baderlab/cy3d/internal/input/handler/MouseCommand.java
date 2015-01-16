package org.baderlab.cy3d.internal.input.handler;


/**
 * A standard interface for a set of mouse input callbacks.
 * 
 * @see MouseCommandAdapter
 * @author mkucera
 */
public interface MouseCommand {

	public static MouseCommand EMPTY = new MouseCommandAdapter();
	
	
	/**
	 * Start of a drag operation.
	 */
	void pressed(int x, int y);
	
	/**
	 * The mouse was dragged by some amount.
	 */
	void dragged(int x, int y);
	
	/**
	 * End of a drag operation.
	 */
	void released(int x, int y);
	
	/**
	 * A full click (press then release) of the mouse button.
	 * MKTODO add double click
	 */
	void clicked(int x, int y);
	
	/**
	 * Mouse is hovered over location.
	 */
	void moved(int x, int y);
	
	/**
	 * The mouse was moved off of the panel.
	 */
	void exited();
	
	/**
	 * The mouse entered the panel.
	 */
	void entered();
	
	
	/**
	 * Returns a mouse command that is modified by holding down Ctrl.
	 */
	MouseCommand modify();
}
