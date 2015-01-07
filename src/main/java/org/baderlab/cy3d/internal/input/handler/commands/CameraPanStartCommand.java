package org.baderlab.cy3d.internal.input.handler.commands;

import org.baderlab.cy3d.internal.input.handler.MouseButton;
import org.baderlab.cy3d.internal.input.handler.MouseCommand;

public class CameraPanStartCommand implements MouseCommand {

	private final CameraPanDragCommand cameraPanDragCommand;
	
	public CameraPanStartCommand(CameraPanDragCommand cameraPanDragCommand) {
		this.cameraPanDragCommand = cameraPanDragCommand;
	}

	@Override
	public void command(MouseButton button, int x, int y) {
		if(button == MouseButton.BUTTON_1) {
			cameraPanDragCommand.setStart(x, y);
		}
	}

}
