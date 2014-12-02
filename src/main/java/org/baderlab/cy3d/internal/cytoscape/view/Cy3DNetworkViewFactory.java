package org.baderlab.cy3d.internal.cytoscape.view;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingManager;

public class Cy3DNetworkViewFactory implements CyNetworkViewFactory {

	private VisualLexicon visualLexicon;
	
	private CyServiceRegistrar serviceRegistrar;
	private VisualMappingManager visualMappingManager;
	
	public Cy3DNetworkViewFactory(CyServiceRegistrar serviceRegistrar,
			VisualLexicon visualLexicon,
			VisualMappingManager visualMappingManager) {
		this.serviceRegistrar = serviceRegistrar;
		this.visualLexicon = visualLexicon;
		this.visualMappingManager = visualMappingManager;
	}
	
	@Override
	public CyNetworkView createNetworkView(CyNetwork network) {
	
		// TODO: Implement use of useThreshold parameter
		Cy3DNetworkView networkView = new Cy3DNetworkView(network, 
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

}
