package org.baderlab.cy3d.internal.input.handler.commands;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.input.handler.MouseButton;
import org.baderlab.cy3d.internal.input.handler.MouseCommand;
import org.baderlab.cy3d.internal.tools.SimpleCamera;

public class CameraPanDragCommand implements MouseCommand {

	private final GraphicsData graphicsData;
	
	private int prevX;
	private int prevY;
	
	
	public CameraPanDragCommand(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}
	
	
	void setStart(int startX, int startY) {
		prevX = startX;
		prevY = startY;
	}

	@Override
	public void command(MouseButton button, int x, int y) {
		SimpleCamera camera = graphicsData.getCamera();
		
		int dx = x - prevX;
		int dy = y - prevY;
		
		camera.turnRight(dx);
		camera.turnDown(dy);
		
		prevX = x;
		prevY = y;
	}

}
