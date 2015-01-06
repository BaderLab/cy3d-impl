package org.baderlab.cy3d.internal;

import org.baderlab.cy3d.internal.cytoscape.view.Cy3DNetworkView;
import org.baderlab.cy3d.internal.graphics.BirdsEyeGraphicsHandler;
import org.baderlab.cy3d.internal.graphics.GraphicsEventHandler;
import org.cytoscape.application.events.SetCurrentRenderingEngineListener;
import org.cytoscape.view.model.CyNetworkView;
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

	public Cy3DBirdsEyeRenderingEngine(Cy3DNetworkView viewModel, VisualLexicon visualLexicon) {
		super(viewModel, visualLexicon);
	}

	@Override
	protected GraphicsEventHandler getGraphicsInstance(CyNetworkView networkView, VisualLexicon visualLexicon) {
		return new GraphicsEventHandler(networkView, visualLexicon, new BirdsEyeGraphicsHandler());
	}

	@Override
	protected SetCurrentRenderingEngineListener getSetCurrentRenderingEngineListener(FPSAnimator animator) {
		// Cytoscape creates a new RenderingEngine instance every time the current RenderingEngine changes
		return null;
	}
}
