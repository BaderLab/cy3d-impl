package org.baderlab.cy3d.internal.input.handler.commands;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.GraphicsSelectionData;
import org.baderlab.cy3d.internal.input.handler.MouseButton;
import org.baderlab.cy3d.internal.input.handler.MouseCommand;

public class SelectionStartCommand implements MouseCommand {

	private final GraphicsData graphicsData;
	
	
	public SelectionStartCommand(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}
	
	@Override
	public void command(MouseButton button, int x, int y) {
		if(button == MouseButton.BUTTON_1) {
			GraphicsSelectionData selectionData = graphicsData.getSelectionData();
			selectionData.setSelectTopLeftX(x);
			selectionData.setSelectTopLeftY(y);
			selectionData.setSelectTopLeftFound(true);
		}
	}

}
