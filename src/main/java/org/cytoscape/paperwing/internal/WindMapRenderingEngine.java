package org.cytoscape.paperwing.internal;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;

public class WindMapRenderingEngine extends WindRenderingEngine {

	public WindMapRenderingEngine(Object container, View<CyNetwork> viewModel,
			VisualLexicon visualLexicon) {
		
		super(container, viewModel, visualLexicon);
		
		graphics.provideCentralView();
	}

}
