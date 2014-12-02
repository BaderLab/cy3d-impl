package org.baderlab.cy3d.internal;

import org.cytoscape.application.events.SetCurrentRenderingEngineListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;

import com.jogamp.opengl.util.FPSAnimator;

/** This class represents the RenderingEngine object for the smaller
 * navigation window in Cytoscape.
 * 
 * It is expected that this class will be expanded on in the near future
 * 
 * @author Paperwing (Yue Dong)
 */
public class Cy3DBirdsEyeRenderingEngine extends Cy3DRenderingEngine {

	public Cy3DBirdsEyeRenderingEngine(Object container, View<CyNetwork> viewModel, VisualLexicon visualLexicon) {
		super(container, viewModel, visualLexicon);
	}

	@Override
	protected Graphics getGraphicsInstance(CyNetworkView networkView, VisualLexicon visualLexicon) {
		return new Graphics(networkView, visualLexicon, new BirdsEyeGraphicsHandler());
	}

	@Override
	protected SetCurrentRenderingEngineListener getSetCurrentRenderingEngineListener(FPSAnimator animator) {
		
		// Cytoscape creates a new RenderingEngine instance every time the current RenderingEngine changes
		return null;
	}
}
