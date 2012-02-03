package org.cytoscape.paperwing.internal.cytoscape.view;

import java.util.Properties;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.model.events.AboutToRemoveEdgesListener;
import org.cytoscape.model.events.AboutToRemoveNodesListener;
import org.cytoscape.model.events.AddedEdgesListener;
import org.cytoscape.model.events.AddedNodesListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingManager;

public class WindNetworkViewFactory implements CyNetworkViewFactory {

	private VisualLexicon visualLexicon;
	
	private CyServiceRegistrar serviceRegistrar;
	private VisualMappingManager visualMappingManager;
	
	public WindNetworkViewFactory(CyServiceRegistrar serviceRegistrar,
			VisualLexicon visualLexicon,
			VisualMappingManager visualMappingManager) {
		this.serviceRegistrar = serviceRegistrar;
		this.visualLexicon = visualLexicon;
		this.visualMappingManager = visualMappingManager;
	}
	
	@Override
	public CyNetworkView createNetworkView(CyNetwork network,
			Boolean useThreshold) {
	
		// TODO: Implement use of useThreshold parameter
		WindNetworkView networkView = new WindNetworkView(network, 
				visualLexicon, visualMappingManager);
		
//		serviceRegistrar.registerService(networkView, AddedNodesListener.class, 
//				new Properties());
//		serviceRegistrar.registerService(networkView, AddedEdgesListener.class, 
//				new Properties());
//		
//		serviceRegistrar.registerService(networkView, AboutToRemoveNodesListener.class, 
//				new Properties());
//		serviceRegistrar.registerService(networkView, AboutToRemoveEdgesListener.class, 
//				new Properties());
		
		// TODO: Now that we've registered the service, we need to unregister them once
		// the NetworkView is removed from Cytoscape
		
		return networkView;
	}
	
	@Override
	public CyNetworkView createNetworkView(CyNetwork network) {
		return createNetworkView(network, true);
	}

}
