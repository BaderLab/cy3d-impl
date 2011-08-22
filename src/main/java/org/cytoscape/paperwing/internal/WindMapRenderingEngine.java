package org.cytoscape.paperwing.internal;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;

/** This class represents the RenderingEngine object for the smaller
 * navigation window in Cytoscape.
 * 
 * It is expected that this class will be expanded on in the near future
 * 
 * @author Paperwing (Yue Dong)
 */
public class WindMapRenderingEngine extends WindRenderingEngine {

	/** Construct a new WindMapRenderingEngine object, currently simply
	 * produces an object nearly identical to a WindRenderingEngine
	 * 
	 * @param container The container in the GUI window used to contain the
	 * rendered results
	 * @param viewModel The {@link View} of the {@link CyNetwork} that is used
	 * to render
	 * @param visualLexicon The {@link VisualLexicon} for the rendering engine
	 */
	public WindMapRenderingEngine(Object container, View<CyNetwork> viewModel,
			VisualLexicon visualLexicon) {
		
		super(container, viewModel, visualLexicon);
		
		graphics.provideCentralView();
	}

}
