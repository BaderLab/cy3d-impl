package org.baderlab.cy3d.internal.input.handler;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JInternalFrame;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.eventbus.MouseModeChangeEvent;
import org.baderlab.cy3d.internal.icons.IconManager;
import org.baderlab.cy3d.internal.icons.IconManagerImpl;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;


/**
 * When the cursor is around the edges of the scene we want to switch
 * the camera controls to 'rotate', when near the center use 'orbit'.
 * 
 * This class is responsible for changing the mouse cursor depending 
 * on the location and current mouse mode.
 * 
 * @author mkucera
 *
 */
public class MouseZoneInputListener implements MouseListener, MouseMotionListener {

	private final GraphicsData graphicsData;
	private final Component container;
	private final JInternalFrame frame;
	
	private final Cursor rotateCursor;
	private final Cursor orbitCursor;
	
	private MouseMode mouseMode;
	
	
	public MouseZoneInputListener(GraphicsData graphicsData, JInternalFrame frame, Component container) {
		this.graphicsData = graphicsData;
		this.container = container;
		this.frame = frame;
		this.mouseMode = MouseMode.getDefault();
		
		IconManagerImpl iconManager = new IconManagerImpl();
		this.rotateCursor = iconManager.getIconCursor(IconManager.ICON_REFRESH);
		this.orbitCursor  = iconManager.getIconCursor(IconManager.ICON_MOVE);
	}
	
	public static MouseZoneInputListener attach(JInternalFrame frame, Component component, GraphicsData graphicsData) {
		MouseZoneInputListener inputHandler = new MouseZoneInputListener(graphicsData, frame, component);
		component.addMouseMotionListener(inputHandler);
		component.addMouseListener(inputHandler);
		
		EventBus eventBus = graphicsData.getEventBus();
		eventBus.register(inputHandler);
		
		return inputHandler;
	}
	
	@Subscribe
	public void mouseModeChanged(MouseModeChangeEvent mouseModeChangeEvent) { 
		mouseMode = mouseModeChangeEvent.getMouseMode();
		
		// Immediately change the cursor (eg, user might hold Shift to force select mode)
		Point mousePosition = MouseInfo.getPointerInfo().getLocation();
		Point containerPosition = container.getLocationOnScreen();
		
		// Make mouse position relative
		mousePosition.x -= containerPosition.x;
		mousePosition.y -= containerPosition.y;
		
		chooseCursor(mousePosition.x, mousePosition.y);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		chooseCursor(e.getX(), e.getY());
	}
	
	private void chooseCursor(int x, int y) {
		if(MouseMode.CAMERA.equals(mouseMode)) {
			Point p = new Point();
			p.x = graphicsData.getScreenWidth();
			p.y = graphicsData.getScreenHeight();
			graphicsData.getPixelConverter().convertToWindowUnits(p);
			
			int width = p.x;
			int height = p.y;
			
			int centerx = width / 2;
			int centery = height / 2;
			
			int mouseXRelative = x - centerx;
			int mouseYRelative = y - centery;
			
			double distanceFromCenter = Math.sqrt(mouseXRelative*mouseXRelative + mouseYRelative*mouseYRelative);
			int zoneRadius = centerRadius(width, height);
			
			if(distanceFromCenter > zoneRadius) {
				frame.setCursor(rotateCursor);
			}
			else {
				frame.setCursor(orbitCursor);
			}
		}
		else {
			frame.setCursor(Cursor.getDefaultCursor());
		}
	}
	
	private int centerRadius(int width, int height) {
		int span = Math.min(width, height);
		return (int)(span * 0.5 * 0.9);
	}

	
	@Override
	public void mouseEntered(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		frame.setCursor(Cursor.getDefaultCursor());
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	

}
