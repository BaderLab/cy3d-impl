package org.baderlab.cy3d.internal.input.handler;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.PixelConverter;
import org.baderlab.cy3d.internal.input.handler.commands.BirdsEyeBoundsMouseCommand;
import org.cytoscape.view.model.CyNetworkView;

public class BirdsEyeInputEventListener implements MouseListener, MouseMotionListener {

	private final CyNetworkView networkView;
	
	private final PixelConverter pixelConverter;
	private final int[] coords = new int[2];
	
	
	private MouseCommand mouseCommand;

	public BirdsEyeInputEventListener(GraphicsData graphicsData) {
		this.networkView = graphicsData.getNetworkView();
		this.pixelConverter = graphicsData.getPixelConverter();
		this.mouseCommand = new BirdsEyeBoundsMouseCommand(graphicsData);
	}
	
	public static BirdsEyeInputEventListener attach(Component component, GraphicsData graphicsData) {
		BirdsEyeInputEventListener inputListener = new BirdsEyeInputEventListener(graphicsData);
		component.addMouseMotionListener(inputListener);
		component.addMouseListener(inputListener);
		return inputListener;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		pixelConverter.convertMouse(e, coords);
		mouseCommand.pressed(coords[0], coords[1]);
		networkView.updateView();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		pixelConverter.convertMouse(e, coords);
		mouseCommand.dragged(coords[0], coords[1]);
		networkView.updateView();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		pixelConverter.convertMouse(e, coords);
		mouseCommand.released(coords[0], coords[1]);
		networkView.updateView();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		pixelConverter.convertMouse(e, coords);
		mouseCommand.clicked(e.getX(), e.getY());
		networkView.updateView();
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
