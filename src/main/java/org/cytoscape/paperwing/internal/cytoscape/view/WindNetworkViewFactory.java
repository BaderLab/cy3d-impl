package org.cytoscape.paperwing.internal.cytoscape.view;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.VisualLexicon;

public class WindNetworkViewFactory implements CyNetworkViewFactory {

	private VisualLexicon visualLexicon;
	
	public WindNetworkViewFactory(VisualLexicon visualLexicon) {
		this.visualLexicon = visualLexicon;
	}
	
	@Override
	public CyNetworkView createNetworkView(CyNetwork network,
			Boolean useThreshold) {
		
		// TODO: Implement use of useThreshold parameter
		return new WindNetworkView(network, visualLexicon);
	}
	
	@Override
	public CyNetworkView createNetworkView(CyNetwork network) {
		return createNetworkView(network, true);
	}

}
