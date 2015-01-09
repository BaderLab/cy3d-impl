package org.baderlab.cy3d.internal.input.handler;

public interface MouseCommand {

	
	void pressed(int x, int y);
	
	void dragged(int x, int y);
	
	void clicked(int x, int y);
	
	void released(int x, int y);
	
	
	public static MouseCommand EMPTY = new MouseCommand() {
		public void released(int x, int y) { }
		public void pressed(int x, int y) { }
		public void dragged(int x, int y) { }
		public void clicked(int x, int y) { }
	};
}
