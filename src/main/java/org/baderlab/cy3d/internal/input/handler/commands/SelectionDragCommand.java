package org.baderlab.cy3d.internal.input.handler.commands;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.GraphicsSelectionData;
import org.baderlab.cy3d.internal.input.handler.MouseButton;
import org.baderlab.cy3d.internal.input.handler.MouseCommand;

public class SelectionDragCommand implements MouseCommand {

	private final GraphicsData graphicsData;
	
	
	public SelectionDragCommand(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}
	
	@Override
	public void command(MouseButton button, int x, int y) {
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		
		selectionData.setSelectBottomRightX(x);
		selectionData.setSelectBottomRightY(y);
		
		if (Math.abs(selectionData.getSelectTopLeftX() - x) >= 1 && Math.abs(selectionData.getSelectTopLeftY() - y) >= 1) {
			selectionData.setDragSelectMode(true);
		}
	}

}
