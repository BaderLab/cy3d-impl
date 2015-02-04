package org.baderlab.cy3d.internal.input.handler;

public interface KeyCommand {

	public void up();
	
	public void down();
	
	public void left();
	
	public void right();
	
	
	public static KeyCommand EMPTY = new KeyCommand() {
		public void up() { }
		public void right() { }
		public void left() { }
		public void down() { }
	};
	
}
