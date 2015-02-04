package org.baderlab.cy3d.internal.input.handler;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.eventbus.BirdsEyeCameraChangeEvent;
import org.cytoscape.view.model.CyNetworkView;

public class BirdsEyeInputEventListener implements MouseListener, MouseMotionListener {

	private final GraphicsData graphicsData;
	private final CyNetworkView networkView;
	
	private final int[] coords = new int[2];
	
	
	private MouseCommand mouseCommand;

	public BirdsEyeInputEventListener(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
		this.networkView = graphicsData.getNetworkView();
		
//		this.mouseCommand = new BirdsEyeBoundsMouseCommand(graphicsData);
		this.mouseCommand = MouseCommand.EMPTY;  // MKTODO for now ignore input
	}
	
	public static BirdsEyeInputEventListener attach(Component component, GraphicsData graphicsData) {
		BirdsEyeInputEventListener inputListener = new BirdsEyeInputEventListener(graphicsData);
		component.addMouseMotionListener(inputListener);
		component.addMouseListener(inputListener);
		return inputListener;
	}
	
	private void updateBothRenderers() {
		graphicsData.getEventBus().post(new BirdsEyeCameraChangeEvent(graphicsData.getCamera()));
		networkView.updateView();
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		graphicsData.getPixelConverter().convertMouse(e, coords);
		mouseCommand.dragStart(coords[0], coords[1]);
		updateBothRenderers();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		graphicsData.getPixelConverter().convertMouse(e, coords);
		mouseCommand.dragMove(coords[0], coords[1]);
		updateBothRenderers();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		graphicsData.getPixelConverter().convertMouse(e, coords);
		mouseCommand.dragEnd(coords[0], coords[1]);
		updateBothRenderers();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		graphicsData.getPixelConverter().convertMouse(e, coords);
		mouseCommand.clicked(e.getX(), e.getY());
		updateBothRenderers();
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
