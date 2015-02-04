package org.baderlab.cy3d.internal.input.handler.commands;

import org.baderlab.cy3d.internal.camera.Camera;
import org.baderlab.cy3d.internal.input.handler.MouseCommandAdapter;

public class CameraOrbitMouseCommand extends MouseCommandAdapter {
	
	private final Camera camera;
	
	private int prevX;
	private int prevY;
	
	private IsRotateSampler sampler;
	
	public interface IsRotateSampler {
		boolean isRotate();
	}
	
	
	public CameraOrbitMouseCommand(Camera camera) {
		this.camera = camera;
	}
	
	public void setIsRotateSampler(IsRotateSampler sampler) {
		this.sampler = sampler;
	}
	
	
	@Override
	public void dragStart(int x, int y) {
		prevX = x;
		prevY = y;
	}

	@Override
	public void dragMove(int x, int y) {
		int dx = x - prevX;
		int dy = y - prevY;
		
		if(sampler != null && sampler.isRotate()) {
			System.out.println("ROTATE");
		}
		else {
			camera.orbitRight(-dx * 0.5);
			camera.orbitUp(dy * 0.5);
		}
		
		prevX = x;
		prevY = y;
	}
}
