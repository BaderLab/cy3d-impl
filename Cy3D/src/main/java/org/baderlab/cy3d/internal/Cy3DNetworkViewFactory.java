package org.baderlab.cy3d.internal;

import org.baderlab.cy3d.internal.cytoscape.view.Cy3DNetworkView;
import org.baderlab.cy3d.internal.eventbus.EventBusProvider;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingManager;

public class Cy3DNetworkViewFactory implements CyNetworkViewFactory {

	private final VisualLexicon visualLexicon;
	private final VisualMappingManager visualMappingManager;
	private final EventBusProvider eventBusProvider;
	
	public Cy3DNetworkViewFactory(VisualLexicon visualLexicon, VisualMappingManager visualMappingManager, EventBusProvider eventBusProvider) {
		this.visualLexicon = visualLexicon;
		this.visualMappingManager = visualMappingManager;
		this.eventBusProvider = eventBusProvider;
	}
	
	@Override
	public CyNetworkView createNetworkView(CyNetwork network) {
		return new Cy3DNetworkView(network, visualLexicon, visualMappingManager, eventBusProvider);
	}

}
