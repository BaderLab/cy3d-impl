package org.baderlab.cy3d.internal.input.handler;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.PixelConverter;
import org.baderlab.cy3d.internal.input.handler.commands.BirdsEyeBoundsMouseCommand;

public class BirdsEyeInputEventListener implements MouseListener, MouseMotionListener {

	private final PixelConverter pixelConverter;
	private final int[] coords = new int[2];
	
	
	private MouseCommand mouseCommand;

	public BirdsEyeInputEventListener(GraphicsData graphicsData) {
		this.pixelConverter = graphicsData.getPixelConverter();
		this.mouseCommand = new BirdsEyeBoundsMouseCommand(graphicsData);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		pixelConverter.convertMouse(e, coords);
		mouseCommand.pressed(coords[0], coords[1]);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		pixelConverter.convertMouse(e, coords);
		mouseCommand.dragged(coords[0], coords[1]);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		pixelConverter.convertMouse(e, coords);
		mouseCommand.released(coords[0], coords[1]);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		pixelConverter.convertMouse(e, coords);
		mouseCommand.clicked(e.getX(), e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	
}
