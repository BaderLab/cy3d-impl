package org.baderlab.cy3d.internal.graphics;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;

import org.baderlab.cy3d.internal.coordinator.ViewingCoordinator;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.lighting.DefaultLightingProcessor;
import org.baderlab.cy3d.internal.lighting.LightingProcessor;
import org.baderlab.cy3d.internal.picking.ShapePickingProcessor;
import org.baderlab.cy3d.internal.rendering.ReadOnlyGraphicsProcedure;
import org.cytoscape.view.model.CyNetworkView;

public abstract class AbstractGraphicsConfiguration implements GraphicsConfiguration {

	private List<ReadOnlyGraphicsProcedure> renderProcedures = new LinkedList<ReadOnlyGraphicsProcedure>();
	
	
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
	public void initializeGraphicsProcedures(GraphicsData graphicsData) {
		for (ReadOnlyGraphicsProcedure proc : renderProcedures) {
			proc.initialize(graphicsData);
		}
	}
	
	@Override
	public void drawScene(GraphicsData graphicsData) {
		for (ReadOnlyGraphicsProcedure proc : renderProcedures) {
			proc.execute(graphicsData);
		}
	}
	
	@Override
	public void dispose(GraphicsData graphicsData) {
	}
	
	/**
	 * Returns a dummy processor that does nothing.
	 */
	@Override
	public ShapePickingProcessor getShapePickingProcessor() {
		return new ShapePickingProcessor() {
			@Override public void initialize(GraphicsData graphicsData) { }
			@Override public void processPicking(GraphicsData graphicsData) { }
		};
	}

	@Override
	public ViewingCoordinator getCoordinator(GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		if (ViewingCoordinator.getCoordinator(networkView) != null) {
			return ViewingCoordinator.getCoordinator(networkView);
		} else {
			return ViewingCoordinator.createCoordinator(networkView);
		}
	}
	
	@Override
	public LightingProcessor getLightingProcessor() {
		return new DefaultLightingProcessor();
	}
	

	@Override
	public void setupLighting(GraphicsData graphicsData) {
	}
	
	@Override
	public void setUpContainer(JComponent container) {
	}


}
