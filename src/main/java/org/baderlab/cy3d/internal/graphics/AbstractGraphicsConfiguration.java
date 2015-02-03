package org.baderlab.cy3d.internal.graphics;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JInternalFrame;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.rendering.ReadOnlyGraphicsProcedure;

public abstract class AbstractGraphicsConfiguration implements GraphicsConfiguration {

	private List<ReadOnlyGraphicsProcedure> renderProcedures = new LinkedList<ReadOnlyGraphicsProcedure>();
	
	protected GraphicsData graphicsData;
	
	
	protected void add(ReadOnlyGraphicsProcedure procedure) {
		if(!renderProcedures.contains(procedure))
			renderProcedures.add(procedure);
	}
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
		for (ReadOnlyGraphicsProcedure proc : renderProcedures) {
			proc.initialize(graphicsData);
		}
	}
	
	@Override
	public void drawScene() {
		for (ReadOnlyGraphicsProcedure proc : renderProcedures) {
			proc.execute(graphicsData);
		}
	}
	
	@Override
	public void dispose() {
	}
	
	@Override
	public void initializeFrame(JInternalFrame container) {
	}

}
