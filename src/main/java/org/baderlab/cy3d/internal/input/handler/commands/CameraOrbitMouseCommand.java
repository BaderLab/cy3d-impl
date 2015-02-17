package org.baderlab.cy3d.internal.input.handler.commands;

import org.baderlab.cy3d.internal.camera.Camera;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.input.handler.MouseCommandAdapter;
import org.baderlab.cy3d.internal.input.handler.MouseZoneInputListener;
import org.baderlab.cy3d.internal.tools.GeometryToolkit;

public class CameraOrbitMouseCommand extends MouseCommandAdapter {
	
	private final GraphicsData graphicsData;
	
	private int prevX;
	private int prevY;
	
	private IsRotateSampler sampler;
	
	public interface IsRotateSampler {
		boolean isRotate();
	}
	
	
	public CameraOrbitMouseCommand(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}
	
	public void setIsRotateSampler(IsRotateSampler sampler) {
		this.sampler = sampler;
	}
	
	public void setIsRotateSampler(final MouseZoneInputListener mouseZoneListener) {
		setIsRotateSampler(
			new IsRotateSampler() {
				public boolean isRotate() {
					return mouseZoneListener.isRotate();
				}
			}
		);
	}
	
	@Override
	public void dragStart(int x, int y) {
		prevX = x;
		prevY = y;
	}

	@Override
	public void dragMove(int x, int y) {
		Camera camera = graphicsData.getCamera();
		
		if(sampler != null && sampler.isRotate()) {
			int centerX = graphicsData.getScreenWidth() / 2;
			int centerY = graphicsData.getScreenHeight() / 2;
			
			int aX = prevX - centerX;
			int aY = prevY - centerY;
			
			int bX = x - centerX;
			int bY = y - centerY;
			
			int dot = aX * bX + aY * bY;
			
			double magPrev = Math.sqrt(aX * aX + aY * aY);
			double magNow  = Math.sqrt(bX * bX + bY * bY);
			
			double angle = GeometryToolkit.saferArcCos(dot / (magPrev * magNow));
			
			double crossX = aX * bY - aY * bX;
			double sign = -Math.signum(crossX);
			
			camera.roll(angle * sign);
		}
		else {
			int dx = x - prevX;
			int dy = y - prevY;
			
			camera.orbitRight(-dx * 0.5);
			camera.orbitUp(dy * 0.5);
		}
		
		prevX = x;
		prevY = y;
	}
}
