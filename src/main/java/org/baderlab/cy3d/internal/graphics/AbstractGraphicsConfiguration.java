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
	
	// MKTODO This is kind of a hack. Just having add() and remove() methods makes it hard
	// to maintain an ordering to the render procedures. For now its ok, but if this method
	// gets called more often it would be better to move to a sorted data structure with
	// explicit prioritization for each procedure.
	protected void remove(ReadOnlyGraphicsProcedure procedure) {
		renderProcedures.remove(procedure);
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
