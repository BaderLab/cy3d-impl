package org.baderlab.cy3d.internal.input.handler;

public interface MouseCommand {

	
	public void command(MouseButton button, int x, int y);
	
	
	public static final MouseCommand EMPTY = new MouseCommand() {
		@Override 
		public void command(MouseButton button, int x, int y) { 
		}
	};
}
