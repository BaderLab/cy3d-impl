package org.baderlab.cy3d.internal.input.handler;

public interface MouseWheelCommand {

	public void execute(int dWheel);
	
	
	public static MouseWheelCommand EMPTY = new MouseWheelCommand() {
		public void execute(int dWheel) { }
	};
	
}
